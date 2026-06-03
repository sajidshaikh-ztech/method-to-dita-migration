package com.ey.method.migration.service;

import com.ey.method.migration.model.*;
import com.ey.method.migration.parser.*;
import com.ey.method.migration.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProcessHierarchyMigrator {

    private static final Logger logger = LoggerFactory.getLogger(ProcessHierarchyMigrator.class);

    private final WbsRepository wbsRepository;
    private final ActivityRepository activityRepository;
    private final MilestoneDefinitionRepository milestoneDefinitionRepository;
    private final TaskUsageRepository taskUsageRepository;
    private final PredecessorRepository predecessorRepository;

    public ProcessHierarchyMigrator(WbsRepository wbsRepository,
                                    ActivityRepository activityRepository,
                                    MilestoneDefinitionRepository milestoneDefinitionRepository,
                                    TaskUsageRepository taskUsageRepository,
                                    PredecessorRepository predecessorRepository) {
        this.wbsRepository = wbsRepository;
        this.activityRepository = activityRepository;
        this.milestoneDefinitionRepository = milestoneDefinitionRepository;
        this.taskUsageRepository = taskUsageRepository;
        this.predecessorRepository = predecessorRepository;
    }

    private static class PendingPredecessor {
        String wbsId;
        Integer predecessorIndex;
        String type;

        PendingPredecessor(String wbsId, Integer predecessorIndex, String type) {
            this.wbsId = wbsId;
            this.predecessorIndex = predecessorIndex;
            this.type = type;
        }
    }

    public void migrateWbsAndPredecessors(ProcessItemXml rootXml, String parentId, String currentContextId) {
        logger.info("Starting WBS and Predecessors migration...");
        Map<Integer, String> indexToIdMap = new HashMap<>();
        List<PendingPredecessor> pendingList = new ArrayList<>();

        // Phase 1: Recursively traverse the tree, persist items, and map index to IDs
        traverseAndMigrate(rootXml, parentId, currentContextId, indexToIdMap, pendingList);

        // Phase 2: Resolve predecessor indices to their physical IDs and save
        logger.info("Resolving {} pending predecessor linkages...", pendingList.size());
        int savedCount = 0;
        for (PendingPredecessor pending : pendingList) {
            String dependentOnId = indexToIdMap.get(pending.predecessorIndex);
            if (dependentOnId != null) {
                Predecessor predecessor = new Predecessor();
                predecessor.setContextID(currentContextId);
                predecessor.setWBSID(pending.wbsId);
                predecessor.setDependentOn(dependentOnId);
                predecessor.setDependencyType(pending.type);

                predecessorRepository.save(predecessor);
                savedCount++;
            } else {
                logger.warn("Predecessor lookup failed for index {} (WBSID: {})", pending.predecessorIndex, pending.wbsId);
            }
        }
        logger.info("Successfully migrated {} Predecessor relationships.", savedCount);
    }

    private void traverseAndMigrate(ProcessItemXml xml, String parentId, String currentContextId,
                                    Map<Integer, String> indexToIdMap, List<PendingPredecessor> pendingList) {
        String type = xml.getType();

        // Special handling for Delivery Process wrapper (root item)
        if ("Delivery Process".equalsIgnoreCase(type)) {
            logger.debug("Handling Delivery Process wrapper: id={}", xml.getId());
            if (xml.getChildren() != null) {
                for (ProcessItemXml child : xml.getChildren()) {
                    traverseAndMigrate(child, parentId, currentContextId, indexToIdMap, pendingList);
                }
            }
            return;
        }
        
        // Register current process-item's index and id mapping
        if (xml.getIndex() != null && xml.getId() != null) {
            indexToIdMap.put(xml.getIndex(), xml.getId());
        }

        // Collect predecessor tag information for Phase 2 resolution
        if (xml.getPredecessors() != null) {
            for (PredecessorXml predXml : xml.getPredecessors()) {
                if (predXml.getIndex() != null) {
                    pendingList.add(new PendingPredecessor(xml.getId(), predXml.getIndex(), predXml.getType()));
                }
            }
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
                    traverseAndMigrate(child, xml.getId(), currentContextId, indexToIdMap, pendingList);
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
