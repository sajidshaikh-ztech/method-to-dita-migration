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
public class RoleAndTaskMigrator {

    private static final Logger logger = LoggerFactory.getLogger(RoleAndTaskMigrator.class);

    private final RoleDefinitionRepository roleDefinitionRepository;
    private final RoleToGuidanceRepository roleToGuidanceRepository;
    private final TaskDefinitionRepository taskDefinitionRepository;
    private final TaskToRoleRepository taskToRoleRepository;
    private final TaskToWorkProductRepository taskToWorkProductRepository;
    private final TaskToGuidanceRepository taskToGuidanceRepository;
    private final WorkstreamDefinitionRepository workstreamDefinitionRepository;
    private final WorkstreamToTaskRepository workstreamToTaskRepository;

    public RoleAndTaskMigrator(RoleDefinitionRepository roleDefinitionRepository,
                               RoleToGuidanceRepository roleToGuidanceRepository,
                               TaskDefinitionRepository taskDefinitionRepository,
                               TaskToRoleRepository taskToRoleRepository,
                               TaskToWorkProductRepository taskToWorkProductRepository,
                               TaskToGuidanceRepository taskToGuidanceRepository,
                               WorkstreamDefinitionRepository workstreamDefinitionRepository,
                               WorkstreamToTaskRepository workstreamToTaskRepository) {
        this.roleDefinitionRepository = roleDefinitionRepository;
        this.roleToGuidanceRepository = roleToGuidanceRepository;
        this.taskDefinitionRepository = taskDefinitionRepository;
        this.taskToRoleRepository = taskToRoleRepository;
        this.taskToWorkProductRepository = taskToWorkProductRepository;
        this.taskToGuidanceRepository = taskToGuidanceRepository;
        this.workstreamDefinitionRepository = workstreamDefinitionRepository;
        this.workstreamToTaskRepository = workstreamToTaskRepository;
    }

    public void migrateRoles(List<MethodRoleXml> rolesXml, String currentContextId) {
        logger.info("Migrating {} Roles...", rolesXml.size());
        for (MethodRoleXml xml : rolesXml) {
            RoleDefinition role = new RoleDefinition();
            role.setContextID(currentContextId);
            role.setRoleID(xml.getId());
            role.setName(xml.getName());
            role.setPresentationName(xml.getName());
            role.setBriefDescription(xml.getBriefDescription());
            role.setVariabilityBasedOn(xml.getVariabilityBasedOn());

            enrichRoleWithDetails(role, xml.getMethodLink());
            roleDefinitionRepository.save(role);
        }
        logger.info("Migrated {} Role definitions", rolesXml.size());
    }

    public void migrateTasks(List<TaskXml> tasksXml, String currentContextId) {
        logger.info("Migrating {} Tasks...", tasksXml.size());
        for (TaskXml xml : tasksXml) {
            TaskDefinition task = new TaskDefinition();
            task.setContextID(currentContextId);
            task.setTaskID(xml.getId());
            task.setName(xml.getName());
            task.setPresentationName(xml.getName());
            task.setBriefDescription(xml.getBriefDescription());
            task.setVariabilityBasedOn(xml.getVariabilityBasedOn());

            enrichTaskWithDetails(task, xml.getMethodLink());
            taskDefinitionRepository.save(task);
        }
        logger.info("Migrated {} Task definitions", tasksXml.size());
    }

    public void migrateWorkstreams(List<WorkstreamXml> workstreamsXml, String currentContextId) {
        logger.info("Migrating {} Workstreams...", workstreamsXml.size());
        for (WorkstreamXml xml : workstreamsXml) {
            WorkstreamDefinition ws = new WorkstreamDefinition();
            ws.setContextID(currentContextId);
            ws.setWorkstreamID(xml.getId());
            ws.setName(xml.getName());
            ws.setBriefDescription(xml.getDescription());
            
            enrichWorkstreamWithDetails(ws, xml.getMethodLink());
            workstreamDefinitionRepository.save(ws);
        }
        logger.info("Migrated {} Workstream definitions", workstreamsXml.size());
    }

    public void migrateRoleAndTaskRelationships(List<MethodRoleXml> rolesXml, List<TaskXml> tasksXml, List<WorkstreamXml> workstreamsXml, String currentContextId) {
        logger.info("Migrating Role, Task, and Workstream relationship mappings...");
        int r2gCount = 0;
        int roleRelationCount = 0;
        int wpRelationCount = 0;
        int guidanceRelationCount = 0;
        int taskRelationCount = 0;

        // 1. Role to Guidance Relations
        if (rolesXml != null) {
            for (MethodRoleXml xml : rolesXml) {
                if (xml.getRelatedGuidanceIds() != null) {
                    for (String guidanceId : xml.getRelatedGuidanceIds()) {
                        RoleToGuidance r2g = new RoleToGuidance();
                        r2g.setContextID(currentContextId);
                        r2g.setRoleID(xml.getId());
                        r2g.setGuidanceID(guidanceId);
                        roleToGuidanceRepository.save(r2g);
                        r2gCount++;
                    }
                }
            }
        }

        // 2. Task relations
        if (tasksXml != null) {
            for (TaskXml xml : tasksXml) {
                // Primary performer Task-to-Role
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

                // Additional performer Task-to-Role
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

                // Task-to-WorkProduct (Inputs)
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

                // Task-to-WorkProduct (Outputs)
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

                // Task-to-Guidance
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
        }

        // 3. Workstream to Task relations
        if (workstreamsXml != null) {
            for (WorkstreamXml xml : workstreamsXml) {
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
        }

        logger.info("Migrated Relationships: {} RoleToGuidance, {} TaskToRole, {} TaskToWorkProduct, {} TaskToGuidance, and {} WorkstreamToTask", 
                    r2gCount, roleRelationCount, wpRelationCount, guidanceRelationCount, taskRelationCount);
    }

    private void enrichTaskWithDetails(TaskDefinition task, String methodLink) {
        if (methodLink == null || methodLink.isEmpty()) return;

        try {
            String[] parts = methodLink.split("/");
            String lastPart = parts[parts.length - 1];
            if (lastPart.contains(".html")) {
                String fileNameBase = lastPart.replace(".html", "");
                String detailFileName = "Task." + fileNameBase + ".xml";

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
}
