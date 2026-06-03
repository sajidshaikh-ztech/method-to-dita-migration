package com.ey.method.migration.service;

import com.ey.method.migration.model.*;
import com.ey.method.migration.parser.*;
import com.ey.method.migration.repository.*;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;

@Service
public class WorkProductMigrator {

    private static final Logger logger = LoggerFactory.getLogger(WorkProductMigrator.class);

    private final OutcomeDefinitionRepository outcomeDefinitionRepository;
    private final WorkProductDefinitionRepository workProductDefinitionRepository;
    private final DeliverableDefinitionRepository deliverableDefinitionRepository;
    private final WorkProductToGuidanceRepository workProductToGuidanceRepository;
    private final DeliverablePartsRepository deliverablePartsRepository;
    private final WorkProductToWorkProductRepository workProductToWorkProductRepository;

    public WorkProductMigrator(OutcomeDefinitionRepository outcomeDefinitionRepository,
                               WorkProductDefinitionRepository workProductDefinitionRepository,
                               DeliverableDefinitionRepository deliverableDefinitionRepository,
                               WorkProductToGuidanceRepository workProductToGuidanceRepository,
                               DeliverablePartsRepository deliverablePartsRepository,
                               WorkProductToWorkProductRepository workProductToWorkProductRepository) {
        this.outcomeDefinitionRepository = outcomeDefinitionRepository;
        this.workProductDefinitionRepository = workProductDefinitionRepository;
        this.deliverableDefinitionRepository = deliverableDefinitionRepository;
        this.workProductToGuidanceRepository = workProductToGuidanceRepository;
        this.deliverablePartsRepository = deliverablePartsRepository;
        this.workProductToWorkProductRepository = workProductToWorkProductRepository;
    }

    public void migrateWorkProducts(List<WorkProductXml> workProductsXml, String currentContextId) {
        logger.info("Migrating {} Work Products...", workProductsXml.size());
        int outcomeCount = 0;
        int definitionCount = 0;
        int deliverableCount = 0;
        for (WorkProductXml xml : workProductsXml) {
            if ("Outcome".equalsIgnoreCase(xml.getType())) {
                OutcomeDefinition outcome = new OutcomeDefinition();
                outcome.setContextID(currentContextId);
                outcome.setWorkProductID(xml.getId());
                outcome.setName(xml.getName());
                outcome.setBriefDescription(xml.getBriefDescription());
                enrichOutcomeWithDetails(outcome, xml.getMethodLink());
                outcomeDefinitionRepository.save(outcome);
                outcomeCount++;
            } else if ("Work Product".equalsIgnoreCase(xml.getType())) {
                WorkProductDefinition wp = new WorkProductDefinition();
                wp.setContextID(currentContextId);
                wp.setWorkProductID(xml.getId());
                wp.setName(xml.getName());
                wp.setPresentationName(xml.getName());
                wp.setBriefDescription(xml.getBriefDescription());
                enrichWorkProductWithDetails(wp, xml.getMethodLink());
                workProductDefinitionRepository.save(wp);
                definitionCount++;
            } else if ("Deliverable".equalsIgnoreCase(xml.getType())) {
                DeliverableDefinition deliverable = new DeliverableDefinition();
                deliverable.setContextID(currentContextId);
                deliverable.setWorkProductID(xml.getId());
                deliverable.setName(xml.getName());
                deliverable.setPresentationName(xml.getName());
                deliverable.setBriefDescription(xml.getBriefDescription());
                enrichDeliverableWithDetails(deliverable, xml.getMethodLink());
                deliverableDefinitionRepository.save(deliverable);
                deliverableCount++;
            }
        }
        logger.info("Migrated {} Outcomes, {} Work Products, and {} Deliverables", outcomeCount, definitionCount, deliverableCount);
    }

    public void migrateWorkProductRelationships(List<WorkProductXml> workProductsXml, String currentContextId) {
        logger.info("Migrating Work Product relationships...");
        int wp2gCount = 0;
        int partsCount = 0;
        int wp2wpCount = 0;
        for (WorkProductXml xml : workProductsXml) {
            // Junction table for WorkProduct-to-Guidance
            if (xml.getGuidanceIds() != null) {
                for (String guidanceId : xml.getGuidanceIds()) {
                    WorkProductToGuidance wp2g = new WorkProductToGuidance();
                    wp2g.setContextID(currentContextId);
                    wp2g.setWorkProductID(xml.getId());
                    wp2g.setGuidanceID(guidanceId);
                    workProductToGuidanceRepository.save(wp2g);
                    wp2gCount++;
                }
            }

            // Junction table for Deliverable-to-WorkProduct (DeliverableParts)
            if ("Deliverable".equalsIgnoreCase(xml.getType()) && xml.getWorkProductIds() != null) {
                for (String childWpId : xml.getWorkProductIds()) {
                    DeliverableParts part = new DeliverableParts();
                    part.setContextID(currentContextId);
                    part.setDeliverableID(xml.getId()); // The parent Deliverable
                    part.setWorkProductID(childWpId);   // The child Work Product part
                    deliverablePartsRepository.save(part);
                    partsCount++;
                }
            }

            // Junction table for WorkProduct-to-WorkProduct (composition of Work Products)
            if ("Work Product".equalsIgnoreCase(xml.getType()) && xml.getWorkProductIds() != null) {
                for (String childWpId : xml.getWorkProductIds()) {
                    WorkProductToWorkProduct wp2wp = new WorkProductToWorkProduct();
                    wp2wp.setContextID(currentContextId);
                    wp2wp.setWorkProductID(xml.getId());
                    wp2wp.setRelatedWorkProductID(childWpId);
                    wp2wp.setRelationshipType("Component");
                    workProductToWorkProductRepository.save(wp2wp);
                    wp2wpCount++;
                }
            }
        }
        logger.info("Migrated {} WorkProduct-to-Guidance relations, {} DeliverableParts, and {} WorkProduct-to-WorkProduct relations", 
                    wp2gCount, partsCount, wp2wpCount);
    }

    private void enrichOutcomeWithDetails(OutcomeDefinition outcome, String methodLink) {
        if (methodLink == null || methodLink.isEmpty()) return;

        try {
            String[] parts = methodLink.split("/");
            String lastPart = parts[parts.length - 1];
            if (lastPart.contains(".html")) {
                String fileNameBase = lastPart.replace(".html", "");
                String detailFileName = "Outcome." + fileNameBase + ".xml";

                File detailFile = new File("src/main/resources/input/xml/" + detailFileName);

                if (detailFile.exists()) {
                    JAXBContext context = JAXBContext.newInstance(ElementDetailXml.class);
                    Unmarshaller unmarshaller = context.createUnmarshaller();
                    ElementDetailXml detailXml = (ElementDetailXml) unmarshaller.unmarshal(detailFile);

                    outcome.setPurpose(detailXml.getAttributeValue("purpose"));
                    outcome.setMainDescription(detailXml.getAttributeValue("mainDescription"));
                    outcome.setKeyConsiderations(detailXml.getAttributeValue("keyConsiderations"));
                    outcome.setImpactOfNotHaving(detailXml.getAttributeValue("impactOfNotHaving"));
                    outcome.setReasonsForNotNeeding(detailXml.getAttributeValue("reasonsForNotNeeding"));

                    String changeDateStr = detailXml.getAttributeValue("changeDate");
                    if (changeDateStr != null && !changeDateStr.isEmpty()) {
                        try {
                            SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
                            java.util.Date parsedDate = sdf.parse(changeDateStr);
                            outcome.setChangeDate(new java.sql.Date(parsedDate.getTime()));
                        } catch (Exception e) {
                            logger.warn("Failed to parse changeDate '{}' for outcome {}", changeDateStr, outcome.getWorkProductID());
                        }
                    }

                    outcome.setChangeHistory(detailXml.getAttributeValue("changeDescription"));

                    String url = detailXml.getUrl();
                    if (url != null && url.contains("/")) {
                        outcome.setOriginatingProcess(url.split("/")[0]);
                    }

                    logger.info("Enriched Outcome {} from {}", outcome.getWorkProductID(), detailFileName);
                } else {
                    logger.warn("Secondary XML file for outcome does not exist: {}", detailFile.getAbsolutePath());
                }
            }
        } catch (Exception e) {
            logger.error("Error enriching outcome {} with details from {}", outcome.getWorkProductID(), methodLink, e);
        }
    }

    private void enrichDeliverableWithDetails(DeliverableDefinition deliverable, String methodLink) {
        if (methodLink == null || methodLink.isEmpty()) return;

        try {
            String[] parts = methodLink.split("/");
            String lastPart = parts[parts.length - 1];
            if (lastPart.contains(".html")) {
                String fileNameBase = lastPart.replace(".html", "");
                String detailFileName = "Deliverable." + fileNameBase + ".xml";

                File detailFile = new File("src/main/resources/input/xml/" + detailFileName);

                if (detailFile.exists()) {
                    JAXBContext context = JAXBContext.newInstance(ElementDetailXml.class);
                    Unmarshaller unmarshaller = context.createUnmarshaller();
                    ElementDetailXml detailXml = (ElementDetailXml) unmarshaller.unmarshal(detailFile);

                    deliverable.setClientValue(detailXml.getAttributeValue("purpose"));
                    deliverable.setInternalDescription(detailXml.getAttributeValue("mainDescription"));
                    deliverable.setExternalDescription(detailXml.getAttributeValue("externalDescription"));
                    deliverable.setPackagingGuidance(detailXml.getAttributeValue("packagingGuidance"));

                    String changeDateStr = detailXml.getAttributeValue("changeDate");
                    if (changeDateStr != null && !changeDateStr.isEmpty()) {
                        try {
                            SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
                            deliverable.setChangeDate(sdf.parse(changeDateStr));
                        } catch (Exception e) {
                            logger.warn("Failed to parse changeDate '{}' for deliverable {}", changeDateStr, deliverable.getWorkProductID());
                        }
                    }

                    deliverable.setChangeDescription(detailXml.getAttributeValue("changeDescription"));

                    String url = detailXml.getUrl();
                    if (url != null && url.contains("/")) {
                        deliverable.setOriginating_process(url.split("/")[0]);
                    }

                    logger.info("Enriched Deliverable {} from {}", deliverable.getWorkProductID(), detailFileName);
                } else {
                    logger.warn("Secondary XML file for deliverable does not exist: {}", detailFile.getAbsolutePath());
                }
            }
        } catch (Exception e) {
            logger.error("Error enriching deliverable {} with details from {}", deliverable.getWorkProductID(), methodLink, e);
        }
    }

    private void enrichWorkProductWithDetails(WorkProductDefinition wp, String methodLink) {
        if (methodLink == null || methodLink.isEmpty()) return;

        try {
            String[] parts = methodLink.split("/");
            String lastPart = parts[parts.length - 1];
            if (lastPart.contains(".html")) {
                String fileNameBase = lastPart.replace(".html", "");
                
                // Try "WorkProductDescriptor." prefix first
                File detailFile = new File("src/main/resources/input/xml/WorkProductDescriptor." + fileNameBase + ".xml");
                if (!detailFile.exists()) {
                    // Fallback to "Artifact." prefix
                    detailFile = new File("src/main/resources/input/xml/Artifact." + fileNameBase + ".xml");
                }

                if (detailFile.exists()) {
                    JAXBContext context = JAXBContext.newInstance(ElementDetailXml.class);
                    Unmarshaller unmarshaller = context.createUnmarshaller();
                    ElementDetailXml detailXml = (ElementDetailXml) unmarshaller.unmarshal(detailFile);

                    wp.setPurpose(detailXml.getAttributeValue("purpose"));
                    wp.setMainDescription(detailXml.getAttributeValue("mainDescription"));
                    wp.setKeyConsiderations(detailXml.getAttributeValue("keyConsiderations"));
                    wp.setBriefOutline(detailXml.getAttributeValue("briefOutline"));
                    wp.setSelectedRepresentation(detailXml.getAttributeValue("selectedRepresentation"));
                    wp.setImpactOfNotHaving(detailXml.getAttributeValue("impactOfNotHaving"));
                    wp.setReasonsForNotNeeding(detailXml.getAttributeValue("reasonsForNotNeeding"));
                    wp.setRepresentationOptions(detailXml.getAttributeValue("representationOptions"));
                    wp.setMethodSpecificInformation(detailXml.getAttributeValue("methodSpecificInformation"));
                    wp.setSynonym(detailXml.getAttributeValue("synonym"));
                    wp.setExternalID(detailXml.getAttributeValue("externalId"));

                    String changeDateStr = detailXml.getAttributeValue("changeDate");
                    if (changeDateStr != null && !changeDateStr.isEmpty()) {
                        try {
                            SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
                            java.util.Date parsedDate = sdf.parse(changeDateStr);
                            wp.setChangeDate(new java.sql.Date(parsedDate.getTime()));
                        } catch (Exception e) {
                            logger.warn("Failed to parse changeDate '{}' for WorkProduct {}", changeDateStr, wp.getWorkProductID());
                        }
                    }

                    wp.setChangeDescription(detailXml.getAttributeValue("changeDescription"));

                    String url = detailXml.getUrl();
                    if (url != null && url.contains("/")) {
                        wp.setOriginatingProcess(url.split("/")[0]);
                    }

                    logger.info("Enriched WorkProduct {} from {}", wp.getWorkProductID(), detailFile.getName());
                } else {
                    logger.warn("Secondary XML file for WorkProduct does not exist: {}", fileNameBase);
                }
            }
        } catch (Exception e) {
            logger.error("Error enriching WorkProduct {} with details from {}", wp.getWorkProductID(), methodLink, e);
        }
    }
}
