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
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

@Service
public class MigrationService implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(MigrationService.class);

    @Value("${migration.input.file}")
    private String inputXmlPath;

    private final DeliveryProcessDefinitionRepository deliveryProcessDefinitionRepository;
    private final ActivityRepository activityRepository;
    private final ContextRepository contextRepository;
    private final OutcomeDefinitionRepository outcomeDefinitionRepository;
    private final ProjectPlanRepository projectPlanRepository;
    private final MilestoneDefinitionRepository milestoneDefinitionRepository;
    private final RoleDefinitionRepository roleDefinitionRepository;
    private final WorkProductDefinitionRepository workProductDefinitionRepository;
    private final TaskDefinitionRepository taskDefinitionRepository;
    private final WorkstreamDefinitionRepository workstreamDefinitionRepository;
    private final DeliverableDefinitionRepository deliverableDefinitionRepository;
    private final GuidanceDefinitionRepository guidanceDefinitionRepository;
    private final GuidanceToGuidanceRepository guidanceToGuidanceRepository;
    private final RoleToGuidanceRepository roleToGuidanceRepository;
    private final TaskToRoleRepository taskToRoleRepository;
    private final TaskToWorkProductRepository taskToWorkProductRepository;
    private final TaskToGuidanceRepository taskToGuidanceRepository;
    private final WorkstreamToTaskRepository workstreamToTaskRepository;
    private final TaskUsageRepository taskUsageRepository;
    private final WbsRepository wbsRepository;
    private final WorkProductToGuidanceRepository workProductToGuidanceRepository;
    private final DeliverablePartsRepository deliverablePartsRepository;
    private final GettingStartedRepository gettingStartedRepository;
    private final PursuitSupportRepository pursuitSupportRepository;
    private final JdbcTemplate jdbcTemplate;

    private String currentContextId;

    public MigrationService(DeliveryProcessDefinitionRepository deliveryProcessDefinitionRepository,
                            ActivityRepository activityRepository,
                            ContextRepository contextRepository,
                            OutcomeDefinitionRepository outcomeDefinitionRepository,
                            ProjectPlanRepository projectPlanRepository,
                            MilestoneDefinitionRepository milestoneDefinitionRepository,
                            RoleDefinitionRepository roleDefinitionRepository,
                            WorkProductDefinitionRepository workProductDefinitionRepository,
                            TaskDefinitionRepository taskDefinitionRepository,
                            WorkstreamDefinitionRepository workstreamDefinitionRepository,
                            DeliverableDefinitionRepository deliverableDefinitionRepository,
                            GuidanceDefinitionRepository guidanceDefinitionRepository,
                            GuidanceToGuidanceRepository guidanceToGuidanceRepository,
                            RoleToGuidanceRepository roleToGuidanceRepository,
                            TaskToRoleRepository taskToRoleRepository,
                            TaskToWorkProductRepository taskToWorkProductRepository,
                            TaskToGuidanceRepository taskToGuidanceRepository,
                            WorkstreamToTaskRepository workstreamToTaskRepository,
                            TaskUsageRepository taskUsageRepository,
                            WbsRepository wbsRepository,
                            WorkProductToGuidanceRepository workProductToGuidanceRepository,
                            DeliverablePartsRepository deliverablePartsRepository,
                            GettingStartedRepository gettingStartedRepository,
                            PursuitSupportRepository pursuitSupportRepository,
                            JdbcTemplate jdbcTemplate) {
        this.deliveryProcessDefinitionRepository = deliveryProcessDefinitionRepository;
        this.activityRepository = activityRepository;
        this.contextRepository = contextRepository;
        this.outcomeDefinitionRepository = outcomeDefinitionRepository;
        this.projectPlanRepository = projectPlanRepository;
        this.milestoneDefinitionRepository = milestoneDefinitionRepository;
        this.roleDefinitionRepository = roleDefinitionRepository;
        this.workProductDefinitionRepository = workProductDefinitionRepository;
        this.taskDefinitionRepository = taskDefinitionRepository;
        this.workstreamDefinitionRepository = workstreamDefinitionRepository;
        this.deliverableDefinitionRepository = deliverableDefinitionRepository;
        this.guidanceDefinitionRepository = guidanceDefinitionRepository;
        this.guidanceToGuidanceRepository = guidanceToGuidanceRepository;
        this.roleToGuidanceRepository = roleToGuidanceRepository;
        this.taskToRoleRepository = taskToRoleRepository;
        this.taskToWorkProductRepository = taskToWorkProductRepository;
        this.taskToGuidanceRepository = taskToGuidanceRepository;
        this.workstreamToTaskRepository = workstreamToTaskRepository;
        this.taskUsageRepository = taskUsageRepository;
        this.wbsRepository = wbsRepository;
        this.workProductToGuidanceRepository = workProductToGuidanceRepository;
        this.deliverablePartsRepository = deliverablePartsRepository;
        this.gettingStartedRepository = gettingStartedRepository;
        this.pursuitSupportRepository = pursuitSupportRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) throws Exception {
        logger.info("Starting Migration to MySQL Staging Database...");

        // 0. Clean database
        truncateTables();

        File inputFile = new File(inputXmlPath);
        if (!inputFile.exists()) {
            logger.error("Input file not found at {}", inputFile.getAbsolutePath());
            return;
        }

        // 1. Parse XML
        JAXBContext context = JAXBContext.newInstance(MethodXml.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        MethodXml methodXml = (MethodXml) unmarshaller.unmarshal(inputFile);
        logger.info("XML parsed successfully. External ID: {}", methodXml.getExternalId());

        // 3. Extract Activities, Milestones, and Task Usages from hierarchy
        String deliveryProcessId = migrateMethodMetadata(methodXml);

        // 4. Extract Project Plans and Global Guidance Definitions (including relationships)
        if (methodXml.getGuidances() != null) {
            migrateGuidances(methodXml.getGuidances());
        }

        // Migrate Getting Started definitions from secondary XMLs
        migrateGettingStarted();
        migratePursuitSupport();

        // 5. Extract Work Products (Outcomes, Master Definitions, and Deliverables)
        if (methodXml.getWorkProducts() != null) {
            migrateWorkProducts(methodXml.getWorkProducts());
        }

        // 6. Extract Roles and their Guidance relationships
        if (methodXml.getMethodRoles() != null) {
            migrateRoles(methodXml.getMethodRoles());
        }

        // 7. Extract Tasks and their relationships
        if (methodXml.getTasks() != null) {
            migrateTasks(methodXml.getTasks());
        }

        // 8. Extract Workstreams and their relationships
        if (methodXml.getWorkstreams() != null) {
            migrateWorkstreams(methodXml.getWorkstreams());
        }

        if (methodXml.getRootProcessItem() != null) {
            traverseAndMigrateProcessItems(methodXml.getRootProcessItem(), deliveryProcessId);
        }

        // 9. Post-Migration Audit
        performPostMigrationAudit();

        logger.info("Migration Complete.");
    }

    private void performPostMigrationAudit() {
        logger.info("Performing Post-Migration Relationship Audit...");
        
        // Audit Guidance-to-Guidance
        String g2gSql = "SELECT g2g.guidanceID, g2g.relatedGuidance " +
                        "FROM GuidanceToGuidance g2g " +
                        "LEFT JOIN GuidanceDefinition gd ON g2g.relatedGuidance = gd.guidanceID " +
                        "WHERE gd.guidanceID IS NULL AND g2g.contextID = ?";
        
        List<Map<String, Object>> g2gOrphans = jdbcTemplate.queryForList(g2gSql, currentContextId);
        if (!g2gOrphans.isEmpty()) {
            logger.warn("Found {} broken Guidance-to-Guidance relationships.", g2gOrphans.size());
        }

        // Audit Role-to-Guidance
        String r2gSql = "SELECT r2g.roleID, r2g.guidanceID " +
                        "FROM RoleToGuidance r2g " +
                        "LEFT JOIN GuidanceDefinition gd ON r2g.guidanceID = gd.guidanceID " +
                        "WHERE gd.guidanceID IS NULL AND r2g.contextID = ?";
        
        List<Map<String, Object>> r2gOrphans = jdbcTemplate.queryForList(r2gSql, currentContextId);
        if (!r2gOrphans.isEmpty()) {
            logger.warn("Found {} broken Role-to-Guidance relationships.", r2gOrphans.size());
        }

        // Audit Task-to-Role
        String ttrSql = "SELECT ttr.taskID, ttr.roleID " +
                        "FROM TaskToRole ttr " +
                        "LEFT JOIN RoleDefinition rd ON ttr.roleID = rd.roleID AND ttr.contextID = rd.contextID " +
                        "WHERE rd.roleID IS NULL AND ttr.contextID = ?";
        
        List<Map<String, Object>> ttrOrphans = jdbcTemplate.queryForList(ttrSql, currentContextId);
        if (!ttrOrphans.isEmpty()) {
            logger.warn("Found {} broken Task-to-Role relationships.", ttrOrphans.size());
        }

        // Audit Task-to-WorkProduct
        String ttwpSql = "SELECT ttwp.taskID, ttwp.workProductID " +
                         "FROM TaskToWorkProduct ttwp " +
                         "LEFT JOIN WorkProductDefinition wpd ON ttwp.workProductID = wpd.workProductID AND ttwp.contextID = wpd.contextID " +
                         "LEFT JOIN DeliverableDefinition dd ON ttwp.workProductID = dd.workProductID AND ttwp.contextID = dd.contextID " +
                         "LEFT JOIN OutcomeDefinition od ON ttwp.workProductID = od.workProductID AND ttwp.contextID = od.contextID " +
                         "WHERE wpd.workProductID IS NULL AND dd.workProductID IS NULL AND od.workProductID IS NULL AND ttwp.contextID = ?";
        
        List<Map<String, Object>> ttwpOrphans = jdbcTemplate.queryForList(ttwpSql, currentContextId);
        if (!ttwpOrphans.isEmpty()) {
            logger.warn("Found {} broken Task-to-WorkProduct relationships.", ttwpOrphans.size());
        }

        // Audit Task-to-Guidance
        String ttgSql = "SELECT ttg.taskID, ttg.guidanceID " +
                        "FROM TaskToGuidance ttg " +
                        "LEFT JOIN GuidanceDefinition gd ON ttg.guidanceID = gd.guidanceID AND ttg.contextID = gd.contextID " +
                        "WHERE gd.guidanceID IS NULL AND ttg.contextID = ?";
        
        List<Map<String, Object>> ttgOrphans = jdbcTemplate.queryForList(ttgSql, currentContextId);
        if (ttgOrphans.isEmpty()) {
            logger.info("Audit Result: All Task-to-Guidance relationships are valid.");
        } else {
            logger.warn("Audit Result: Found {} broken Task-to-Guidance relationships.", ttgOrphans.size());
            for (Map<String, Object> orphan : ttgOrphans) {
                logger.warn("  Orphan Found: Task [{}] refers to missing Guidance [{}]", 
                            orphan.get("taskID"), orphan.get("guidanceID"));
            }
        }

        // Audit Workstream-to-Task
        String wstSql = "SELECT wst.workstreamID, wst.taskID " +
                        "FROM WorkstreamToTask wst " +
                        "LEFT JOIN TaskDefinition td ON wst.taskID = td.taskID AND wst.contextID = td.contextID " +
                        "WHERE td.taskID IS NULL AND wst.contextID = ?";
        
        List<Map<String, Object>> wstOrphans = jdbcTemplate.queryForList(wstSql, currentContextId);
        if (wstOrphans.isEmpty()) {
            logger.info("Audit Result: All Workstream-to-Task relationships are valid.");
        } else {
            logger.warn("Audit Result: Found {} broken Workstream-to-Task relationships.", wstOrphans.size());
        }

        // Audit WorkProduct-to-Guidance
        String wp2gSql = "SELECT wp2g.workProductID, wp2g.guidanceID " +
                         "FROM WorkProductToGuidance wp2g " +
                         "LEFT JOIN GuidanceDefinition gd ON wp2g.guidanceID = gd.guidanceID AND wp2g.contextID = gd.contextID " +
                         "WHERE gd.guidanceID IS NULL AND wp2g.contextID = ?";
        
        List<Map<String, Object>> wp2gOrphans = jdbcTemplate.queryForList(wp2gSql, currentContextId);
        if (wp2gOrphans.isEmpty()) {
            logger.info("Audit Result: All WorkProduct-to-Guidance relationships are valid.");
        } else {
            logger.warn("Audit Result: Found {} broken WorkProduct-to-Guidance relationships.", wp2gOrphans.size());
        }

        // Audit DeliverableParts
        String dpSql = "SELECT dp.deliverableID, dp.workProductID " +
                       "FROM DeliverableParts dp " +
                       "LEFT JOIN DeliverableDefinition dd ON dp.deliverableID = dd.workProductID AND dp.contextID = dd.contextID " +
                       "WHERE dd.workProductID IS NULL AND dp.contextID = ?";
        
        List<Map<String, Object>> dpOrphans = jdbcTemplate.queryForList(dpSql, currentContextId);
        if (dpOrphans.isEmpty()) {
            logger.info("Audit Result: All DeliverableParts relationships are valid.");
        } else {
            logger.warn("Audit Result: Found {} broken DeliverableParts relationships.", dpOrphans.size());
        }
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

    private void traverseAndMigrateProcessItems(ProcessItemXml xml, String parentId) {
        String type = xml.getType();

        // Special handling for Delivery Process item in the XML (it's a wrapper for the root items)
        if ("Delivery Process".equalsIgnoreCase(type)) {
            logger.debug("Handling Delivery Process wrapper: id={}", xml.getId());
            if (xml.getChildren() != null) {
                for (ProcessItemXml child : xml.getChildren()) {
                    // Pass the same parentId (which is the DeliveryProcessDefinition UUID) to children
                    traverseAndMigrateProcessItems(child, parentId);
                }
            }
            return;
        }
        
        // 1. Create WBS relationship for this item
        WBS wbs = new WBS();
        wbs.setContextID(currentContextId);
        wbs.setParentID(parentId);
        wbs.setChildID(xml.getId());
        wbs.setItemIndex(xml.getIndex());
        wbsRepository.save(wbs);
        logger.debug("Migrated WBS row: parent=[{}] -> child=[{}] ({})", parentId, xml.getId(), type);

        // 2. Persist the element itself based on type
        if ("Activity".equalsIgnoreCase(type) || "Phase".equalsIgnoreCase(type) || "Iteration".equalsIgnoreCase(type)) {
            Activity activity = new Activity();
            activity.setContextID(currentContextId);
            activity.setActivityID(xml.getId());
            activity.setName(xml.getName());
            activity.setBriefDescription(xml.getBriefDescription());
            activity.setType(xml.getType());
            activityRepository.save(activity);
            logger.debug("Migrated Activity/Phase/Iteration: {}", xml.getName());

            // Container types recurse through children
            if (xml.getChildren() != null) {
                for (ProcessItemXml child : xml.getChildren()) {
                    traverseAndMigrateProcessItems(child, xml.getId());
                }
            }
        } else if ("Milestone".equalsIgnoreCase(type)) {
            MilestoneDefinition milestone = new MilestoneDefinition();
            milestone.setContextID(currentContextId);
            milestone.setMilestoneID(xml.getId());
            milestone.setName(xml.getName());
            milestone.setMainDescription(xml.getBriefDescription());
            milestoneDefinitionRepository.save(milestone);
            logger.info("Migrated Milestone: {}", xml.getName());
            // Milestones are leaf nodes, no recursion
        } else if ("Task Descriptor".equalsIgnoreCase(type) || "Task Description".equalsIgnoreCase(type)) {
            TaskUsage taskUsage = new TaskUsage();
            taskUsage.setContextID(currentContextId);
            taskUsage.setDescriptorID(xml.getId());
            taskUsage.setTaskID(xml.getRelatedTask());
            taskUsageRepository.save(taskUsage);
            logger.debug("Migrated TaskUsage: {}", xml.getName());
            // Task Usages are leaf nodes, no recursion
        }
    }

    private void migrateWorkProducts(List<WorkProductXml> workProductsXml) {
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

            // Junction table for WorkProduct-to-Guidance
            if (xml.getGuidanceIds() != null) {
                for (String guidanceId : xml.getGuidanceIds()) {
                    WorkProductToGuidance wp2g = new WorkProductToGuidance();
                    wp2g.setContextID(currentContextId);
                    wp2g.setWorkProductID(xml.getId());
                    wp2g.setGuidanceID(guidanceId);
                    workProductToGuidanceRepository.save(wp2g);
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
                }
            }
        }
        logger.info("Migrated {} Outcomes, {} Work Products, and {} Deliverables", outcomeCount, definitionCount, deliverableCount);
    }

    private void migrateGuidances(List<GuidanceXml> guidancesXml) {
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

    private void migrateRoles(List<MethodRoleXml> rolesXml) {
        int relationCount = 0;
        for (MethodRoleXml xml : rolesXml) {
            RoleDefinition role = new RoleDefinition();
            role.setContextID(currentContextId);
            role.setRoleID(xml.getId());
            role.setName(xml.getName());
            role.setPresentationName(xml.getName());
            role.setBriefDescription(xml.getBriefDescription());
            role.setVariabilityBasedOn(xml.getVariabilityBasedOn());

            // Enrich with details from secondary XML
            enrichRoleWithDetails(role, xml.getMethodLink());

            roleDefinitionRepository.save(role);

            // Junction table for Role-to-Guidance relationships
            if (xml.getRelatedGuidanceIds() != null) {
                for (String guidanceId : xml.getRelatedGuidanceIds()) {
                    RoleToGuidance r2g = new RoleToGuidance();
                    r2g.setContextID(currentContextId);
                    r2g.setRoleID(xml.getId());
                    r2g.setGuidanceID(guidanceId);
                    roleToGuidanceRepository.save(r2g);
                    relationCount++;
                }
            }
        }
        logger.info("Migrated {} Roles and {} Role-to-Guidance relationships", rolesXml.size(), relationCount);
    }

    private void migrateTasks(List<TaskXml> tasksXml) {
        int roleRelationCount = 0;
        int wpRelationCount = 0;
        int guidanceRelationCount = 0;
        for (TaskXml xml : tasksXml) {
            TaskDefinition task = new TaskDefinition();
            task.setContextID(currentContextId);
            task.setTaskID(xml.getId());
            task.setName(xml.getName());
            task.setPresentationName(xml.getName());
            task.setBriefDescription(xml.getBriefDescription());
            task.setVariabilityBasedOn(xml.getVariabilityBasedOn());

            // Enrich with details from secondary XML
            enrichTaskWithDetails(task, xml.getMethodLink());

            taskDefinitionRepository.save(task);

            // Junction table for Task-to-Role relationships (Primary)
            if (xml.getRoleIds() != null) {
                for (String roleId : xml.getRoleIds()) {
                    TaskToRole ttr = new TaskToRole();
                    ttr.setContextID(currentContextId);
                    ttr.setTaskID(xml.getId());
                    ttr.setRoleID(roleId);
                    ttr.setRelationshipType("primary performer"); 
                    taskToRoleRepository.save(ttr);
                    roleRelationCount++;
                }
            }

            // Junction table for Task-to-Role relationships (Additional)
            if (xml.getAdditionalRoleIds() != null) {
                for (String roleId : xml.getAdditionalRoleIds()) {
                    TaskToRole ttr = new TaskToRole();
                    ttr.setContextID(currentContextId);
                    ttr.setTaskID(xml.getId());
                    ttr.setRoleID(roleId);
                    ttr.setRelationshipType("additional performer"); 
                    taskToRoleRepository.save(ttr);
                    roleRelationCount++;
                }
            }

            // Junction table for Task-to-WorkProduct (Inputs)
            if (xml.getInputWorkProductIds() != null) {
                for (String wpId : xml.getInputWorkProductIds()) {
                    TaskToWorkProduct ttwp = new TaskToWorkProduct();
                    ttwp.setContextID(currentContextId);
                    ttwp.setTaskID(xml.getId());
                    ttwp.setWorkProductID(wpId);
                    ttwp.setRelationshipType("Input"); 
                    taskToWorkProductRepository.save(ttwp);
                    wpRelationCount++;
                }
            }

            // Junction table for Task-to-WorkProduct (Outputs)
            if (xml.getOutputWorkProductIds() != null) {
                for (String wpId : xml.getOutputWorkProductIds()) {
                    TaskToWorkProduct ttwp = new TaskToWorkProduct();
                    ttwp.setContextID(currentContextId);
                    ttwp.setTaskID(xml.getId());
                    ttwp.setWorkProductID(wpId);
                    ttwp.setRelationshipType("Output"); 
                    taskToWorkProductRepository.save(ttwp);
                    wpRelationCount++;
                }
            }

            // Junction table for Task-to-Guidance
            if (xml.getGuidanceIds() != null) {
                for (String guidanceId : xml.getGuidanceIds()) {
                    TaskToGuidance ttg = new TaskToGuidance();
                    ttg.setContextID(currentContextId);
                    ttg.setTaskID(xml.getId());
                    ttg.setGuidanceID(guidanceId);
                    taskToGuidanceRepository.save(ttg);
                    guidanceRelationCount++;
                }
            }
        }
        logger.info("Migrated {} Tasks, {} Role, {} WP, and {} Guidance relationships", 
                    tasksXml.size(), roleRelationCount, wpRelationCount, guidanceRelationCount);
    }

    private void migrateWorkstreams(List<WorkstreamXml> workstreamsXml) {
        int taskRelationCount = 0;
        for (WorkstreamXml xml : workstreamsXml) {
            WorkstreamDefinition ws = new WorkstreamDefinition();
            ws.setContextID(currentContextId);
            ws.setWorkstreamID(xml.getId());
            ws.setName(xml.getName());
            ws.setBriefDescription(xml.getDescription());
            
            enrichWorkstreamWithDetails(ws, xml.getMethodLink());
            
            workstreamDefinitionRepository.save(ws);

            if (xml.getTaskIds() != null) {
                for (String taskId : xml.getTaskIds()) {
                    WorkstreamToTask wst = new WorkstreamToTask();
                    wst.setContextID(currentContextId);
                    wst.setWorkstreamID(xml.getId());
                    wst.setTaskID(taskId);
                    workstreamToTaskRepository.save(wst);
                    taskRelationCount++;
                }
            }
        }
        logger.info("Migrated {} Workstreams and {} Workstream-to-Task relationships", 
                    workstreamsXml.size(), taskRelationCount);
    }

    private void enrichTaskWithDetails(TaskDefinition task, String methodLink) {
        if (methodLink == null || methodLink.isEmpty()) return;

        try {
            // Extract filename from method-link
            // Example: https://methods.ey.com/production/eya_oracle/#practice.pas.perf.train_design.base/tasks/define_training_data_env_rqmts_7115BAFB.html
            String[] parts = methodLink.split("/");
            String lastPart = parts[parts.length - 1];
            if (lastPart.contains(".html")) {
                String fileNameBase = lastPart.replace(".html", "");
                String detailFileName = "Task." + fileNameBase + ".xml";

                // Path as specified: /resources/input/xml/
                File detailFile = new File("src/main/resources/input/xml/" + detailFileName);

                if (detailFile.exists()) {
                    JAXBContext context = JAXBContext.newInstance(ElementDetailXml.class);
                    Unmarshaller unmarshaller = context.createUnmarshaller();
                    ElementDetailXml detailXml = (ElementDetailXml) unmarshaller.unmarshal(detailFile);

                    task.setObjectives(detailXml.getAttributeValue("purpose"));
                    task.setKeyConsiderations(detailXml.getAttributeValue("keyConsiderations"));
                    task.setInstructions(detailXml.getAttributeValue("mainDescription"));

                    String changeDateStr = detailXml.getAttributeValue("changeDate");
                    if (changeDateStr != null && !changeDateStr.isEmpty()) {
                        try {
                            SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
                            task.setChangeDate(sdf.parse(changeDateStr));
                        } catch (Exception e) {
                            logger.warn("Failed to parse changeDate '{}' for task {}", changeDateStr, task.getTaskID());
                        }
                    }

                    task.setChangeDescription(detailXml.getAttributeValue("changeDescription"));
                    
                    String url = detailXml.getUrl();
                    if (url != null && url.contains("/")) {
                        task.setOriginatingProcess(url.split("/")[0]);
                    }

                    logger.info("Enriched task {} from {}", task.getTaskID(), detailFileName);
                }
            }
        } catch (Exception e) {
            logger.error("Error enriching task {} with details from {}", task.getTaskID(), methodLink, e);
        }
    }

    private void enrichRoleWithDetails(RoleDefinition role, String methodLink) {
        if (methodLink == null || methodLink.isEmpty()) return;

        try {
            // Extract filename from method-link
            String[] parts = methodLink.split("/");
            String lastPart = parts[parts.length - 1];
            if (lastPart.contains(".html")) {
                String fileNameBase = lastPart.replace(".html", "");
                String detailFileName = "Role." + fileNameBase + ".xml";

                File detailFile = new File("src/main/resources/input/xml/" + detailFileName);

                if (detailFile.exists()) {
                    JAXBContext context = JAXBContext.newInstance(ElementDetailXml.class);
                    Unmarshaller unmarshaller = context.createUnmarshaller();
                    ElementDetailXml detailXml = (ElementDetailXml) unmarshaller.unmarshal(detailFile);

                    role.setResponsibilities(detailXml.getAttributeValue("mainDescription"));
                    role.setKeyConsiderations(detailXml.getAttributeValue("keyConsiderations"));
                    role.setSkills(detailXml.getAttributeValue("skills"));
                    role.setStaffing(detailXml.getAttributeValue("assignmentApproaches"));
                    role.setSynonyms(detailXml.getAttributeValue("synonyms"));

                    String changeDateStr = detailXml.getAttributeValue("changeDate");
                    if (changeDateStr != null && !changeDateStr.isEmpty()) {
                        try {
                            SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
                            role.setChangeDate(sdf.parse(changeDateStr));
                        } catch (Exception e) {
                            logger.warn("Failed to parse changeDate '{}' for role {}", changeDateStr, role.getRoleID());
                        }
                    }

                    role.setChangeDescription(detailXml.getAttributeValue("changeDescription"));
                    
                    String url = detailXml.getUrl();
                    if (url != null && url.contains("/")) {
                        role.setOriginatingProcess(url.split("/")[0]);
                    }

                    logger.info("Enriched role {} from {}", role.getRoleID(), detailFileName);
                }
            }
        } catch (Exception e) {
            logger.error("Error enriching role {} with details from {}", role.getRoleID(), methodLink, e);
        }
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

    private void enrichWorkstreamWithDetails(WorkstreamDefinition ws, String methodLink) {
        if (methodLink == null || methodLink.isEmpty()) return;

        try {
            String[] parts = methodLink.split("/");
            String lastPart = parts[parts.length - 1];
            if (lastPart.contains(".html")) {
                String fileNameBase = lastPart.replace(".html", "");
                String detailFileName = "CapabilityPattern." + fileNameBase + ".xml";

                File detailFile = new File("src/main/resources/input/xml/" + detailFileName);

                if (detailFile.exists()) {
                    JAXBContext context = JAXBContext.newInstance(ElementDetailXml.class);
                    Unmarshaller unmarshaller = context.createUnmarshaller();
                    ElementDetailXml detailXml = (ElementDetailXml) unmarshaller.unmarshal(detailFile);

                    ws.setPresentationName(detailXml.getAttributeValue("presentationName"));
                    ws.setPurpose(detailXml.getAttributeValue("purpose"));
                    ws.setMainDescription(detailXml.getAttributeValue("mainDescription"));
                    ws.setKeyConsiderations(detailXml.getAttributeValue("keyConsiderations"));
                    ws.setUsageNotes(detailXml.getAttributeValue("usageNotes"));

                    String changeDateStr = detailXml.getAttributeValue("changeDate");
                    if (changeDateStr != null && !changeDateStr.isEmpty()) {
                        try {
                            SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
                            java.util.Date parsedDate = sdf.parse(changeDateStr);
                            ws.setChangeDate(new java.sql.Date(parsedDate.getTime()));
                        } catch (Exception e) {
                            logger.warn("Failed to parse changeDate '{}' for workstream {}", changeDateStr, ws.getWorkstreamID());
                        }
                    }

                    ws.setChangeDescription(detailXml.getAttributeValue("changeDescription"));

                    String url = detailXml.getUrl();
                    if (url != null && url.contains("/")) {
                        ws.setOriginatingProcess(url.split("/")[0]);
                    }

                    logger.info("Enriched Workstream {} from {}", ws.getWorkstreamID(), detailFileName);
                } else {
                    logger.warn("Secondary XML file for workstream does not exist: {}", detailFile.getAbsolutePath());
                }
            }
        } catch (Exception e) {
            logger.error("Error enriching workstream {} with details from {}", ws.getWorkstreamID(), methodLink, e);
        }
    }

    private void truncateTables() {
        logger.info("Truncating all tables for a fresh run...");
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 0");

        // Adjust schema for TaskDefinition, RoleDefinition, and DeliveryProcessDefinition
        try {
            jdbcTemplate.execute("ALTER TABLE TaskDefinition MODIFY COLUMN changeDescription TEXT");
            jdbcTemplate.execute("ALTER TABLE TaskDefinition MODIFY COLUMN originatingProcess VARCHAR(255)");
            jdbcTemplate.execute("ALTER TABLE RoleDefinition MODIFY COLUMN responsibilities TEXT");
            jdbcTemplate.execute("ALTER TABLE RoleDefinition MODIFY COLUMN keyConsiderations TEXT");
            jdbcTemplate.execute("ALTER TABLE RoleDefinition MODIFY COLUMN skills TEXT");
            jdbcTemplate.execute("ALTER TABLE RoleDefinition MODIFY COLUMN staffing TEXT");
            jdbcTemplate.execute("ALTER TABLE RoleDefinition MODIFY COLUMN changeDescription TEXT");
            jdbcTemplate.execute("ALTER TABLE RoleDefinition MODIFY COLUMN originatingProcess VARCHAR(255)");
            jdbcTemplate.execute("ALTER TABLE DeliveryProcessDefinition MODIFY COLUMN mainDescription TEXT");
            jdbcTemplate.execute("ALTER TABLE DeliveryProcessDefinition MODIFY COLUMN usageNotes TEXT");
            jdbcTemplate.execute("ALTER TABLE DeliveryProcessDefinition MODIFY COLUMN presentationName VARCHAR(255)");
            jdbcTemplate.execute("ALTER TABLE DeliveryProcessDefinition MODIFY COLUMN sponsor VARCHAR(255)");
            jdbcTemplate.execute("ALTER TABLE DeliveryProcessDefinition MODIFY COLUMN steward VARCHAR(255)");
            jdbcTemplate.execute("ALTER TABLE DeliverableDefinition MODIFY COLUMN changeDescription TEXT");
            jdbcTemplate.execute("ALTER TABLE DeliverableDefinition MODIFY COLUMN originating_process VARCHAR(255)");
            jdbcTemplate.execute("ALTER TABLE GuidanceDefinition MODIFY COLUMN changeHistory TEXT");
            jdbcTemplate.execute("ALTER TABLE GuidanceDefinition MODIFY COLUMN originatingProcess VARCHAR(255)");
            jdbcTemplate.execute("ALTER TABLE OutcomeDefinition MODIFY COLUMN purpose TEXT");
            jdbcTemplate.execute("ALTER TABLE OutcomeDefinition MODIFY COLUMN mainDescription TEXT");
            jdbcTemplate.execute("ALTER TABLE OutcomeDefinition MODIFY COLUMN keyConsiderations TEXT");
            jdbcTemplate.execute("ALTER TABLE OutcomeDefinition MODIFY COLUMN impactOfNotHaving TEXT");
            jdbcTemplate.execute("ALTER TABLE OutcomeDefinition MODIFY COLUMN reasonsForNotNeeding TEXT");
            jdbcTemplate.execute("ALTER TABLE OutcomeDefinition MODIFY COLUMN changeHistory TEXT");
            jdbcTemplate.execute("ALTER TABLE OutcomeDefinition MODIFY COLUMN originatingProcess VARCHAR(255)");
            jdbcTemplate.execute("ALTER TABLE WorkProductDefinition MODIFY COLUMN purpose TEXT");
            jdbcTemplate.execute("ALTER TABLE WorkProductDefinition MODIFY COLUMN mainDescription TEXT");
            jdbcTemplate.execute("ALTER TABLE WorkProductDefinition MODIFY COLUMN keyConsiderations TEXT");
            jdbcTemplate.execute("ALTER TABLE WorkProductDefinition MODIFY COLUMN briefOutline TEXT");
            jdbcTemplate.execute("ALTER TABLE WorkProductDefinition MODIFY COLUMN selectedRepresentation TEXT");
            jdbcTemplate.execute("ALTER TABLE WorkProductDefinition MODIFY COLUMN impactOfNotHaving TEXT");
            jdbcTemplate.execute("ALTER TABLE WorkProductDefinition MODIFY COLUMN reasonsForNotNeeding TEXT");
            jdbcTemplate.execute("ALTER TABLE WorkProductDefinition MODIFY COLUMN representationOptions TEXT");
            jdbcTemplate.execute("ALTER TABLE WorkProductDefinition MODIFY COLUMN methodSpecificInformation TEXT");
            jdbcTemplate.execute("ALTER TABLE WorkProductDefinition MODIFY COLUMN synonym TEXT");
            jdbcTemplate.execute("ALTER TABLE WorkProductDefinition MODIFY COLUMN changeDescription TEXT");
            jdbcTemplate.execute("ALTER TABLE WorkProductDefinition MODIFY COLUMN originatingProcess VARCHAR(255)");
            jdbcTemplate.execute("ALTER TABLE WorkstreamDefinition MODIFY COLUMN briefDescription TEXT");
            jdbcTemplate.execute("ALTER TABLE WorkstreamDefinition MODIFY COLUMN presentationName VARCHAR(255)");
            jdbcTemplate.execute("ALTER TABLE WorkstreamDefinition MODIFY COLUMN purpose TEXT");
            jdbcTemplate.execute("ALTER TABLE WorkstreamDefinition MODIFY COLUMN mainDescription TEXT");
            jdbcTemplate.execute("ALTER TABLE WorkstreamDefinition MODIFY COLUMN keyConsiderations TEXT");
            jdbcTemplate.execute("ALTER TABLE WorkstreamDefinition MODIFY COLUMN usageNotes TEXT");
            jdbcTemplate.execute("ALTER TABLE WorkstreamDefinition MODIFY COLUMN changeDescription TEXT");
            jdbcTemplate.execute("ALTER TABLE WorkstreamDefinition MODIFY COLUMN originatingProcess VARCHAR(255)");
        } catch (Exception e) {
            logger.warn("Could not alter tables: {}", e.getMessage());
        }

        String[] tables = {
            "DeliveryProcessDefinition", "Activity", "Context", "OutcomeDefinition", 
            "ProjectPlan", "MilestoneDefinition", "RoleDefinition", "WorkProductDefinition", 
            "TaskDefinition", "WorkstreamDefinition", "DeliverableDefinition", "GuidanceDefinition", 
            "GuidanceToGuidance", "RoleToGuidance", "TaskToRole", "TaskToWorkProduct", 
            "TaskToGuidance", "WorkstreamToTask", "TaskUsage", "WBS", "WorkProductToGuidance", 
            "DeliverableParts", "GettingStarted"
        };
        for (String table : tables) {
            jdbcTemplate.execute("TRUNCATE TABLE " + table);
        }
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 1");
        logger.info("Truncation complete.");
    }

    private void migrateGettingStarted() {
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

    private void migratePursuitSupport() {
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
                            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
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
}
