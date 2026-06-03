package com.ey.method.migration.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class SchemaCleanupService {

    private static final Logger logger = LoggerFactory.getLogger(SchemaCleanupService.class);
    private final JdbcTemplate jdbcTemplate;

    public SchemaCleanupService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void truncateTables() {
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
            jdbcTemplate.execute("ALTER TABLE ReleaseInformation MODIFY COLUMN whatsNew TEXT");
            jdbcTemplate.execute("ALTER TABLE ReleaseInformation MODIFY COLUMN revisionHistory TEXT");
            jdbcTemplate.execute("ALTER TABLE ReleaseInformation MODIFY COLUMN acknowledgements TEXT");
            jdbcTemplate.execute("ALTER TABLE ReleaseInformation MODIFY COLUMN internalHistory TEXT");
        } catch (Exception e) {
            logger.warn("Could not alter tables: {}", e.getMessage());
        }

        String[] tables = {
            "DeliveryProcessDefinition", "Activity", "Context", "OutcomeDefinition", 
            "ProjectPlan", "MilestoneDefinition", "RoleDefinition", "WorkProductDefinition", 
            "TaskDefinition", "WorkstreamDefinition", "DeliverableDefinition", "GuidanceDefinition", 
            "GuidanceToGuidance", "RoleToGuidance", "TaskToRole", "TaskToWorkProduct", 
            "TaskToGuidance", "WorkstreamToTask", "TaskUsage", "WBS", "WorkProductToGuidance", 
            "DeliverableParts", "GettingStarted", "PursuitSupport", "PlanAttachments", "ReleaseInformation",
            "Predecessor", "ProcessToGuidance",
            "StepDefinition", "TaskToStep", "TeamDefinition", "TeamToTask",
            "WorkProductToWorkProduct"
        };
        for (String table : tables) {
            jdbcTemplate.execute("TRUNCATE TABLE " + table);
        }
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 1");
        logger.info("Truncation complete.");
    }
}
