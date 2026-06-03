package com.ey.method.migration.service;

import com.ey.method.migration.model.*;
import com.ey.method.migration.parser.*;
import com.ey.method.migration.repository.*;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.concurrent.CompletableFuture;

@Service
public class MigrationService implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(MigrationService.class);

    @Value("${migration.input.file}")
    private String inputXmlPath;

    private final ContextRepository contextRepository;
    private final DeliveryProcessDefinitionRepository deliveryProcessDefinitionRepository;

    private final SchemaCleanupService schemaCleanupService;
    private final GuidanceMigrator guidanceMigrator;
    private final WorkProductMigrator workProductMigrator;
    private final RoleAndTaskMigrator roleAndTaskMigrator;
    private final ProcessHierarchyMigrator processHierarchyMigrator;
    private final RelationshipAuditService relationshipAuditService;

    private String currentContextId;

    public MigrationService(ContextRepository contextRepository,
                            DeliveryProcessDefinitionRepository deliveryProcessDefinitionRepository,
                            SchemaCleanupService schemaCleanupService,
                            GuidanceMigrator guidanceMigrator,
                            WorkProductMigrator workProductMigrator,
                            RoleAndTaskMigrator roleAndTaskMigrator,
                            ProcessHierarchyMigrator processHierarchyMigrator,
                            RelationshipAuditService relationshipAuditService) {
        this.contextRepository = contextRepository;
        this.deliveryProcessDefinitionRepository = deliveryProcessDefinitionRepository;
        this.schemaCleanupService = schemaCleanupService;
        this.guidanceMigrator = guidanceMigrator;
        this.workProductMigrator = workProductMigrator;
        this.roleAndTaskMigrator = roleAndTaskMigrator;
        this.processHierarchyMigrator = processHierarchyMigrator;
        this.relationshipAuditService = relationshipAuditService;
    }

    @Override
    public void run(String... args) throws Exception {
        long startTime = System.currentTimeMillis();
        logger.info("Starting Refactored Async Migration to MySQL Staging Database...");

        // Phase I: Synchronous Setup & Global Metadata
        schemaCleanupService.truncateTables();

        File inputFile = new File(inputXmlPath);
        if (!inputFile.exists()) {
            logger.error("Input file not found at {}", inputFile.getAbsolutePath());
            return;
        }

        JAXBContext context = JAXBContext.newInstance(MethodXml.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        MethodXml methodXml = (MethodXml) unmarshaller.unmarshal(inputFile);
        logger.info("Primary XML parsed successfully. External ID: {}", methodXml.getExternalId());

        String deliveryProcessId = migrateMethodMetadata(methodXml);
        if (deliveryProcessId == null) {
            logger.error("Failed to migrate method metadata. Context generation failed.");
            return;
        }

        // Phase II: Parallel Processing of Flat Definitions (Major Performance Boost ⚡)
        logger.info("Initiating Phase II Parallel Asynchronous Migration of Definitions...");
        
        CompletableFuture<Void> flatGuidancesTask = CompletableFuture.runAsync(() -> {
            if (methodXml.getGuidances() != null) {
                guidanceMigrator.migrateGuidances(methodXml.getGuidances(), currentContextId);
            }
        });

        CompletableFuture<Void> gettingStartedTask = CompletableFuture.runAsync(() -> {
            guidanceMigrator.migrateGettingStarted(currentContextId);
        });

        CompletableFuture<Void> pursuitSupportTask = CompletableFuture.runAsync(() -> {
            guidanceMigrator.migratePursuitSupport(currentContextId);
        });

        CompletableFuture<Void> releaseInformationTask = CompletableFuture.runAsync(() -> {
            guidanceMigrator.migrateReleaseInformation(currentContextId);
        });

        CompletableFuture<Void> workProductsTask = CompletableFuture.runAsync(() -> {
            if (methodXml.getWorkProducts() != null) {
                workProductMigrator.migrateWorkProducts(methodXml.getWorkProducts(), currentContextId);
            }
        });

        CompletableFuture<Void> rolesTask = CompletableFuture.runAsync(() -> {
            if (methodXml.getMethodRoles() != null) {
                roleAndTaskMigrator.migrateRoles(methodXml.getMethodRoles(), currentContextId);
            }
        });

        CompletableFuture<Void> tasksTask = CompletableFuture.runAsync(() -> {
            if (methodXml.getTasks() != null) {
                roleAndTaskMigrator.migrateTasks(methodXml.getTasks(), currentContextId);
            }
        });

        CompletableFuture<Void> workstreamsTask = CompletableFuture.runAsync(() -> {
            if (methodXml.getWorkstreams() != null) {
                roleAndTaskMigrator.migrateWorkstreams(methodXml.getWorkstreams(), currentContextId);
            }
        });

        // Wait for all concurrent migration tasks to complete (Barrier)
        CompletableFuture.allOf(
            flatGuidancesTask, gettingStartedTask, pursuitSupportTask, releaseInformationTask,
            workProductsTask, rolesTask, tasksTask, workstreamsTask
        ).join();

        logger.info("Phase II Parallel Processing complete. Entering Phase III...");

        // Phase III: Downstream Relationships, Hierarchy, and Audit
        // Now that ALL master definitions exist in the database, relationships can be safely migrated
        if (methodXml.getWorkProducts() != null) {
            workProductMigrator.migrateWorkProductRelationships(methodXml.getWorkProducts(), currentContextId);
        }

        roleAndTaskMigrator.migrateRoleAndTaskRelationships(
            methodXml.getMethodRoles(), 
            methodXml.getTasks(), 
            methodXml.getWorkstreams(), 
            currentContextId
        );

        guidanceMigrator.migratePlanAttachments(methodXml, currentContextId);

        if (methodXml.getRootProcessItem() != null) {
            logger.info("Migrating WBS Hierarchy...");
            processHierarchyMigrator.migrateWbsAndPredecessors(methodXml.getRootProcessItem(), deliveryProcessId, currentContextId);
        }

        relationshipAuditService.performPostMigrationAudit(currentContextId);

        long duration = System.currentTimeMillis() - startTime;
        logger.info("Migration Complete. Total Execution Time: {} ms", duration);
    }

    private String migrateMethodMetadata(MethodXml xml) {
        if (xml.getExternalId() != null) {
            Context contextEntity = new Context();
            contextEntity = contextRepository.save(contextEntity);
            this.currentContextId = contextEntity.getContextId();
            logger.info("Created new Context ID: {}", currentContextId);

            DeliveryProcessDefinition dpd = new DeliveryProcessDefinition();
            String deliveryProcessId = xml.getRootProcessItem() != null ? xml.getRootProcessItem().getId() : xml.getExternalId().trim();
            dpd.setProcessID(deliveryProcessId);
            dpd.setContextID(currentContextId);
            dpd.setExternalID(xml.getExternalId().trim());
            dpd.setName(xml.getName());
            dpd.setMainDescription(xml.getDescription());
            dpd.setSponsor(xml.getSponsor());
            dpd.setSteward(xml.getSteward());
            dpd.setPublish(xml.getPublish());
            dpd.setPubType(xml.getType());
            dpd.setFoundation(xml.getFoundation());
            dpd.setMercuryServiceCode(xml.getMsc());
            dpd.setGlobalServiceCode(xml.getGsc());

            if (xml.getRootProcessItem() != null && xml.getRootProcessItem().getMethodLink() != null) {
                enrichDeliveryProcessWithDetails(dpd, xml.getRootProcessItem().getMethodLink());
            }

            dpd = deliveryProcessDefinitionRepository.save(dpd);
            logger.info("Migrated Method Metadata (ID: {}, Name: {})", xml.getExternalId(), xml.getName());
            return dpd.getProcessID();
        }
        return null;
    }

    private void enrichDeliveryProcessWithDetails(DeliveryProcessDefinition dpd, String methodLink) {
        if (methodLink == null || methodLink.isEmpty()) return;

        try {
            String[] parts = methodLink.split("/");
            String lastPart = parts[parts.length - 1];
            if (lastPart.contains(".html")) {
                String fileNameBase = lastPart.replace(".html", "");
                String detailFileName = "DeliveryProcess." + fileNameBase + ".xml";

                File detailFile = new File("src/main/resources/input/xml/" + detailFileName);

                if (detailFile.exists()) {
                    JAXBContext context = JAXBContext.newInstance(ElementDetailXml.class);
                    Unmarshaller unmarshaller = context.createUnmarshaller();
                    ElementDetailXml detailXml = (ElementDetailXml) unmarshaller.unmarshal(detailFile);

                    String shortname = detailXml.getAttributeValue("name");
                    if (shortname != null && !shortname.isEmpty()) {
                        dpd.setShortname(shortname);
                    }

                    String mainDesc = detailXml.getAttributeValue("mainDescription");
                    if (mainDesc != null && !mainDesc.isEmpty()) {
                        dpd.setMainDescription(mainDesc);
                    }

                    String usageNotes = detailXml.getAttributeValue("usageNotes");
                    if (usageNotes != null && !usageNotes.isEmpty()) {
                        dpd.setUsageNotes(usageNotes);
                    }

                    String presName = detailXml.getAttributeValue("presentationName");
                    if (presName != null && !presName.isEmpty()) {
                        dpd.setPresentationName(presName);
                    }

                    String keywords = detailXml.getAttributeValue("keywords");
                    if (keywords != null && !keywords.isEmpty()) {
                        dpd.setKeywords(keywords);
                    }

                    String sponsor = detailXml.getAttributeValue("projectMemberExpertise");
                    if (sponsor != null && !sponsor.isEmpty()) {
                        dpd.setSponsor(sponsor);
                    }

                    String publish = detailXml.getRteValue("Publish");
                    if (publish != null && !publish.isEmpty()) {
                        dpd.setPublish(publish);
                    }

                    String type = detailXml.getRteValue("Type");
                    if (type != null && !type.isEmpty()) {
                        dpd.setPubType(type);
                    }

                    String subtype = detailXml.getRteValue("Management");
                    if (subtype != null && !subtype.isEmpty()) {
                        dpd.setSubtype(subtype);
                    }

                    String foundation = detailXml.getRteValue("Foundation");
                    if (foundation != null && !foundation.isEmpty()) {
                        dpd.setFoundation(foundation);
                    }

                    String msc = detailXml.getRteValue("Mercury Service Code");
                    if (msc != null && !msc.isEmpty()) {
                        dpd.setMercuryServiceCode(msc);
                    }

                    String gsc = detailXml.getRteValue("Global Service Code");
                    if (gsc != null && !gsc.isEmpty()) {
                        dpd.setGlobalServiceCode(gsc);
                    }

                    logger.info("Enriched DeliveryProcess {} from {}", dpd.getProcessID(), detailFileName);
                } else {
                    logger.warn("Secondary XML file for delivery process does not exist: {}", detailFile.getAbsolutePath());
                }
            }
        } catch (Exception e) {
            logger.error("Error enriching delivery process {} with details from {}", dpd.getProcessID(), methodLink, e);
        }
    }
}
