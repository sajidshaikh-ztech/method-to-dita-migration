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
public class GuidanceMigrator {

    private static final Logger logger = LoggerFactory.getLogger(GuidanceMigrator.class);

    private final ProjectPlanRepository projectPlanRepository;
    private final GuidanceDefinitionRepository guidanceDefinitionRepository;
    private final GuidanceToGuidanceRepository guidanceToGuidanceRepository;
    private final GettingStartedRepository gettingStartedRepository;
    private final PursuitSupportRepository pursuitSupportRepository;
    private final PlanAttachmentsRepository planAttachmentsRepository;
    private final ReleaseInformationRepository releaseInformationRepository;
    private final DeliveryProcessDefinitionRepository deliveryProcessDefinitionRepository;

    public GuidanceMigrator(ProjectPlanRepository projectPlanRepository,
                            GuidanceDefinitionRepository guidanceDefinitionRepository,
                            GuidanceToGuidanceRepository guidanceToGuidanceRepository,
                            GettingStartedRepository gettingStartedRepository,
                            PursuitSupportRepository pursuitSupportRepository,
                            PlanAttachmentsRepository planAttachmentsRepository,
                            ReleaseInformationRepository releaseInformationRepository,
                            DeliveryProcessDefinitionRepository deliveryProcessDefinitionRepository) {
        this.projectPlanRepository = projectPlanRepository;
        this.guidanceDefinitionRepository = guidanceDefinitionRepository;
        this.guidanceToGuidanceRepository = guidanceToGuidanceRepository;
        this.gettingStartedRepository = gettingStartedRepository;
        this.pursuitSupportRepository = pursuitSupportRepository;
        this.planAttachmentsRepository = planAttachmentsRepository;
        this.releaseInformationRepository = releaseInformationRepository;
        this.deliveryProcessDefinitionRepository = deliveryProcessDefinitionRepository;
    }

    public void migrateGuidances(List<GuidanceXml> guidancesXml, String currentContextId) {
        logger.info("Migrating {} Guidance Definitions...", guidancesXml.size());
        int planCount = 0;
        int totalGuidanceCount = 0;
        int relationCount = 0;
        for (GuidanceXml xml : guidancesXml) {
            // 1. Specific table for Project Plans
            if ("Project Plan".equalsIgnoreCase(xml.getType())) {
                ProjectPlan plan = new ProjectPlan();
                plan.setContextID(currentContextId);
                plan.setGuidanceID(xml.getId());
                plan.setName(xml.getName());
                plan.setPresentationName(xml.getName());
                plan.setMainDescription(xml.getBriefDescription());
                projectPlanRepository.save(plan);
                planCount++;
            }

            // 2. Master table for all Guidance types
            GuidanceDefinition def = new GuidanceDefinition();
            def.setContextID(currentContextId);
            def.setGuidanceID(xml.getId());
            def.setType(xml.getType());
            def.setName(xml.getName());
            def.setPresentationName(xml.getName());
            def.setBriefDescription(xml.getBriefDescription());
            enrichGuidanceWithDetails(def, xml.getType(), xml.getMethodLink());
            guidanceDefinitionRepository.save(def);
            totalGuidanceCount++;

            // 3. Junction table for relationships
            if (xml.getRelatedGuidanceIds() != null) {
                for (String relatedId : xml.getRelatedGuidanceIds()) {
                    GuidanceToGuidance g2g = new GuidanceToGuidance();
                    g2g.setContextID(currentContextId);
                    g2g.setGuidanceID(xml.getId());
                    g2g.setRelatedGuidance(relatedId);
                    g2g.setRelationshipType("Related"); 
                    guidanceToGuidanceRepository.save(g2g);
                    relationCount++;
                }
            }
        }
        logger.info("Migrated {} Project Plans, {} Guidance Definitions, and {} relationships", planCount, totalGuidanceCount, relationCount);
    }

    public void migrateGettingStarted(String currentContextId) {
        logger.info("Migrating Getting Started definitions from secondary XMLs...");
        try {
            File dir = new File("src/main/resources/input/xml/");
            if (!dir.exists() || !dir.isDirectory()) {
                logger.warn("Secondary XML directory not found: {}", dir.getAbsolutePath());
                return;
            }

            File[] files = dir.listFiles((d, name) -> name.startsWith("udt.getting_started_") && name.endsWith(".xml"));
            if (files == null || files.length == 0) {
                logger.info("No udt.getting_started_*.xml files found.");
                return;
            }

            for (File file : files) {
                logger.info("Processing Getting Started XML: {}", file.getName());
                try {
                    JAXBContext context = JAXBContext.newInstance(ElementDetailXml.class);
                    Unmarshaller unmarshaller = context.createUnmarshaller();
                    ElementDetailXml detailXml = (ElementDetailXml) unmarshaller.unmarshal(file);

                    // Create master GuidanceDefinition
                    GuidanceDefinition guidanceDef = new GuidanceDefinition();
                    guidanceDef.setContextID(currentContextId);
                    guidanceDef.setGuidanceID(detailXml.getId());
                    guidanceDef.setType("Getting Started");
                    guidanceDef.setName(detailXml.getName());
                    guidanceDef.setPresentationName(detailXml.getDisplayName());
                    guidanceDef.setBriefDescription("");

                    // Read detailed fields
                    guidanceDef.setChangeHistory(detailXml.getAttributeValue("changeDescription"));
                    String changeDateStr = detailXml.getAttributeValue("changeDate");
                    if (changeDateStr != null && !changeDateStr.isEmpty()) {
                        try {
                            SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
                            guidanceDef.setChangeDate(sdf.parse(changeDateStr));
                        } catch (Exception e) {
                            logger.warn("Failed to parse changeDate '{}' for GettingStarted guidance", changeDateStr);
                        }
                    }
                    String url = detailXml.getUrl();
                    if (url != null && url.contains("/")) {
                        guidanceDef.setOriginatingProcess(url.split("/")[0]);
                    }

                    guidanceDefinitionRepository.save(guidanceDef);

                    GettingStarted gettingStarted = new GettingStarted();
                    gettingStarted.setContextID(currentContextId);
                    gettingStarted.setGuidanceID(detailXml.getId());
                    gettingStarted.setName(detailXml.getName());
                    gettingStarted.setPresentationName(detailXml.getDisplayName());

                    gettingStarted.setBackground(detailXml.getAttributeValue("problem"));
                    gettingStarted.setHowToApply(detailXml.getAttributeValue("goals"));
                    gettingStarted.setConsiderations(detailXml.getAttributeValue("background"));
                    gettingStarted.setSizing(detailXml.getAttributeValue("mainDescription"));
                    gettingStarted.setStaffing(detailXml.getAttributeValue("application"));
                    gettingStarted.setNextSteps(detailXml.getAttributeValue("levelsOfAdoption"));
                    gettingStarted.setPurpose(detailXml.getAttributeValue("additionalInfo"));

                    gettingStartedRepository.save(gettingStarted);
                    logger.info("Successfully migrated Getting Started definition for id: {}", detailXml.getId());
                } catch (Exception e) {
                    logger.error("Error parsing Getting Started XML: {}", file.getName(), e);
                }
            }
        } catch (Exception e) {
            logger.error("Error migrating Getting Started definitions", e);
        }
    }

    public void migratePursuitSupport(String currentContextId) {
        logger.info("Migrating Pursuit Support definitions from secondary XMLs...");
        try {
            File dir = new File("src/main/resources/input/xml/");
            if (!dir.exists() || !dir.isDirectory()) {
                logger.warn("Secondary XML directory not found: {}", dir.getAbsolutePath());
                return;
            }

            File[] files = dir.listFiles((d, name) -> name.startsWith("udt.pursuit_support_") && name.endsWith(".xml"));
            if (files == null || files.length == 0) {
                logger.info("No udt.pursuit_support_*.xml files found.");
                return;
            }

            for (File file : files) {
                logger.info("Processing Pursuit Support XML: {}", file.getName());
                try {
                    JAXBContext context = JAXBContext.newInstance(ElementDetailXml.class);
                    Unmarshaller unmarshaller = context.createUnmarshaller();
                    ElementDetailXml detailXml = (ElementDetailXml) unmarshaller.unmarshal(file);

                    // Create master GuidanceDefinition
                    GuidanceDefinition guidanceDef = new GuidanceDefinition();
                    guidanceDef.setContextID(currentContextId);
                    guidanceDef.setGuidanceID(detailXml.getId());
                    guidanceDef.setType("Pursuit Support");
                    guidanceDef.setName(detailXml.getName());
                    guidanceDef.setPresentationName(detailXml.getDisplayName());
                    guidanceDef.setBriefDescription("");

                    // Read detailed fields
                    guidanceDef.setChangeHistory(detailXml.getAttributeValue("changeDescription"));
                    String changeDateStr = detailXml.getAttributeValue("changeDate");
                    if (changeDateStr != null && !changeDateStr.isEmpty()) {
                        try {
                            SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
                            guidanceDef.setChangeDate(sdf.parse(changeDateStr));
                        } catch (Exception e) {
                            logger.warn("Failed to parse changeDate '{}' for PursuitSupport guidance", changeDateStr);
                        }
                    }
                    String url = detailXml.getUrl();
                    if (url != null && url.contains("/")) {
                        guidanceDef.setOriginatingProcess(url.split("/")[0]);
                    }

                    guidanceDefinitionRepository.save(guidanceDef);

                    PursuitSupport pursuitSupport = new PursuitSupport();
                    pursuitSupport.setContextID(currentContextId);
                    pursuitSupport.setGuidanceID(detailXml.getId());
                    pursuitSupport.setName(detailXml.getName());
                    pursuitSupport.setPresentationName(detailXml.getDisplayName());

                    pursuitSupport.setSolutionOverview(detailXml.getAttributeValue("problem"));
                    pursuitSupport.setElevatorPitch(detailXml.getAttributeValue("goals"));
                    pursuitSupport.setValueProp(detailXml.getAttributeValue("background"));
                    pursuitSupport.setPositioning(detailXml.getAttributeValue("mainDescription"));
                    pursuitSupport.setScoping(detailXml.getAttributeValue("application"));
                    pursuitSupport.setEstimating(detailXml.getAttributeValue("levelsOfAdoption"));
                    pursuitSupport.setAdditionalInfo(detailXml.getAttributeValue("additionalInfo"));

                    pursuitSupportRepository.save(pursuitSupport);
                    logger.info("Successfully migrated Pursuit Support definition for id: {}", detailXml.getId());
                } catch (Exception e) {
                    logger.error("Error parsing Pursuit Support XML: {}", file.getName(), e);
                }
            }
        } catch (Exception e) {
            logger.error("Error migrating Pursuit Support definitions", e);
        }
    }

    public void migratePlanAttachments(MethodXml methodXml, String currentContextId) {
        logger.info("Migrating Plan Attachments...");
        try {
            String planXlsx = methodXml.getPlanXlsx();
            String planMpp = methodXml.getPlanMpp();

            if (planXlsx == null && planMpp == null) {
                logger.info("No plan-xlsx or plan-mpp attributes found in method tag.");
                return;
            }

            // Find the guidance tag with type="Project Plan" to extract the id
            String projectPlanGuidanceId = null;
            if (methodXml.getGuidances() != null) {
                for (GuidanceXml guidance : methodXml.getGuidances()) {
                    if ("Project Plan".equalsIgnoreCase(guidance.getType())) {
                        projectPlanGuidanceId = guidance.getId();
                        break;
                    }
                }
            }

            if (projectPlanGuidanceId == null) {
                logger.warn("No guidance element of type 'Project Plan' found to link PlanAttachments.");
                return;
            }

            PlanAttachments attachments = new PlanAttachments();
            attachments.setContextID(currentContextId);
            attachments.setGuidanceID(projectPlanGuidanceId);
            attachments.setExcelPlan(planXlsx);
            attachments.setProjectPlan(planMpp);

            planAttachmentsRepository.save(attachments);
            logger.info("Successfully migrated PlanAttachments: excelPlan='{}', projectPlan='{}'", attachments.getExcelPlan(), attachments.getProjectPlan());
        } catch (Exception e) {
            logger.error("Error migrating Plan Attachments", e);
        }
    }

    public void migrateReleaseInformation(String currentContextId) {
        logger.info("Migrating Release Information from secondary XMLs...");
        try {
            // Retrieve the saved DeliveryProcessDefinition to get shortname and processID
            List<DeliveryProcessDefinition> dpdList = deliveryProcessDefinitionRepository.findAll();
            if (dpdList.isEmpty()) {
                logger.warn("No DeliveryProcessDefinition found. Skipping ReleaseInformation migration.");
                return;
            }
            DeliveryProcessDefinition dpd = dpdList.get(0);
            String shortname = dpd.getShortname();

            if (shortname == null || shortname.isEmpty()) {
                logger.warn("DeliveryProcessDefinition shortname is null/empty. Skipping ReleaseInformation migration.");
                return;
            }

            File dir = new File("src/main/resources/input/xml/");
            if (!dir.exists() || !dir.isDirectory()) {
                logger.warn("Secondary XML directory not found: {}", dir.getAbsolutePath());
                return;
            }

            // File pattern: udt.<shortname>_info*.xml
            String filePrefix = "udt." + shortname + "_info";
            File[] files = dir.listFiles((d, name) -> name.startsWith(filePrefix) && name.endsWith(".xml"));

            if (files == null || files.length == 0) {
                logger.info("No {} files found.", filePrefix + "*.xml");
                return;
            }

            for (File file : files) {
                logger.info("Processing Release Information XML: {}", file.getName());
                try {
                    JAXBContext jaxbContext = JAXBContext.newInstance(ElementDetailXml.class);
                    Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
                    ElementDetailXml detailXml = (ElementDetailXml) unmarshaller.unmarshal(file);

                    ReleaseInformation ri = new ReleaseInformation();
                    ri.setContextID(currentContextId);
                    ri.setGuidanceID(detailXml.getId());
                    ri.setProcessID(dpd.getProcessID());

                    // Top-level attribute fields
                    ri.setName(detailXml.getAttributeValue("name"));
                    ri.setPresentationName(detailXml.getAttributeValue("presentationName"));

                    // Fields inside <reference name="presentation"><Element> (searched recursively)
                    ri.setWhatsNew(detailXml.getAttributeValue("problem"));
                    ri.setRevisionHistory(detailXml.getAttributeValue("goals"));
                    ri.setAcknowledgements(detailXml.getAttributeValue("background"));
                    ri.setInternalHistory(detailXml.getAttributeValue("mainDescription"));
                    ri.setDesignerCommentary(detailXml.getAttributeValue("application"));

                    // levelsOfAdoption = "Last Reviewed" -> lastReviewed (DATE)
                    String lastReviewedStr = detailXml.getAttributeValue("levelsOfAdoption");
                    if (lastReviewedStr != null && !lastReviewedStr.trim().isEmpty()) {
                        try {
                            ri.setLastReviewed(java.time.LocalDate.parse(lastReviewedStr.trim()));
                        } catch (Exception e1) {
                            try {
                                SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
                                java.util.Date parsed = sdf.parse(lastReviewedStr.trim());
                                ri.setLastReviewed(parsed.toInstant()
                                        .atZone(java.time.ZoneId.systemDefault()).toLocalDate());
                            } catch (Exception e2) {
                                logger.warn("Failed to parse lastReviewed '{}' for ReleaseInformation {}",
                                        lastReviewedStr, detailXml.getId());
                            }
                        }
                    }

                    releaseInformationRepository.save(ri);
                    logger.info("Successfully migrated ReleaseInformation for guidanceID: {}", detailXml.getId());
                } catch (Exception e) {
                    logger.error("Error parsing Release Information XML: {}", file.getName(), e);
                }
            }
        } catch (Exception e) {
            logger.error("Error migrating Release Information", e);
        }
    }

    private void enrichGuidanceWithDetails(GuidanceDefinition guidance, String type, String methodLink) {
        if (methodLink == null || methodLink.isEmpty()) return;

        try {
            String[] parts = methodLink.split("/");
            String lastPart = parts[parts.length - 1];
            if (lastPart.contains(".html")) {
                String fileNameBase = lastPart.replace(".html", "");
                String detailFileName = type + "." + fileNameBase + ".xml";

                File detailFile = new File("src/main/resources/input/xml/" + detailFileName);

                if (detailFile.exists()) {
                    JAXBContext context = JAXBContext.newInstance(ElementDetailXml.class);
                    Unmarshaller unmarshaller = context.createUnmarshaller();
                    ElementDetailXml detailXml = (ElementDetailXml) unmarshaller.unmarshal(detailFile);

                    guidance.setMainDescription(detailXml.getAttributeValue("mainDescription"));
                    guidance.setSourceType(detailXml.getAttributeValue("sourceType"));
                    guidance.setSourcePath(detailXml.getAttributeValue("sourcePath"));

                    String changeDateStr = detailXml.getAttributeValue("changeDate");
                    if (changeDateStr != null && !changeDateStr.isEmpty()) {
                        try {
                            SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
                            guidance.setChangeDate(sdf.parse(changeDateStr));
                        } catch (Exception e) {
                            logger.warn("Failed to parse changeDate '{}' for guidance {}", changeDateStr, guidance.getGuidanceID());
                        }
                    }

                    guidance.setChangeHistory(detailXml.getAttributeValue("changeDescription"));

                    String url = detailXml.getUrl();
                    if (url != null && url.contains("/")) {
                        guidance.setOriginatingProcess(url.split("/")[0]);
                    }

                    logger.info("Enriched Guidance {} from {}", guidance.getGuidanceID(), detailFileName);
                } else {
                    logger.warn("Secondary XML file for guidance does not exist: {}", detailFile.getAbsolutePath());
                }
            }
        } catch (Exception e) {
            logger.error("Error enriching guidance {} with details from {}", guidance.getGuidanceID(), methodLink, e);
        }
    }
}
