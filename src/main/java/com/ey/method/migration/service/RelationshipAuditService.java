package com.ey.method.migration.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class RelationshipAuditService {

    private static final Logger logger = LoggerFactory.getLogger(RelationshipAuditService.class);
    private final JdbcTemplate jdbcTemplate;

    public RelationshipAuditService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void performPostMigrationAudit(String currentContextId) {
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
}
