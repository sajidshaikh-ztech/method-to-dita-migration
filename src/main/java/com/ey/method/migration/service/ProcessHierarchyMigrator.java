package com.ey.method.migration.service;

import com.ey.method.migration.model.*;
import com.ey.method.migration.parser.*;
import com.ey.method.migration.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ProcessHierarchyMigrator {

    private static final Logger logger = LoggerFactory.getLogger(ProcessHierarchyMigrator.class);

    private final WbsRepository wbsRepository;
    private final ActivityRepository activityRepository;
    private final MilestoneDefinitionRepository milestoneDefinitionRepository;
    private final TaskUsageRepository taskUsageRepository;

    public ProcessHierarchyMigrator(WbsRepository wbsRepository,
                                    ActivityRepository activityRepository,
                                    MilestoneDefinitionRepository milestoneDefinitionRepository,
                                    TaskUsageRepository taskUsageRepository) {
        this.wbsRepository = wbsRepository;
        this.activityRepository = activityRepository;
        this.milestoneDefinitionRepository = milestoneDefinitionRepository;
        this.taskUsageRepository = taskUsageRepository;
    }

    public void traverseAndMigrateProcessItems(ProcessItemXml xml, String parentId, String currentContextId) {
        String type = xml.getType();

        // Special handling for Delivery Process wrapper (root item)
        if ("Delivery Process".equalsIgnoreCase(type)) {
            logger.debug("Handling Delivery Process wrapper: id={}", xml.getId());
            if (xml.getChildren() != null) {
                for (ProcessItemXml child : xml.getChildren()) {
                    traverseAndMigrateProcessItems(child, parentId, currentContextId);
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
                    traverseAndMigrateProcessItems(child, xml.getId(), currentContextId);
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
        } else if ("Task Descriptor".equalsIgnoreCase(type) || "Task Description".equalsIgnoreCase(type)) {
            TaskUsage taskUsage = new TaskUsage();
            taskUsage.setContextID(currentContextId);
            taskUsage.setDescriptorID(xml.getId());
            taskUsage.setTaskID(xml.getRelatedTask());
            taskUsageRepository.save(taskUsage);
            logger.debug("Migrated TaskUsage: {}", xml.getName());
        }
    }
}
