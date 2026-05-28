DROP TABLE IF EXISTS PlanAttachments;

CREATE TABLE PlanAttachments (
    contextID      VARCHAR(36) NOT NULL,
    guidanceID     VARCHAR(25) NOT NULL,
    excelPlan      VARCHAR(255),
    projectPlan    VARCHAR(255),
    CONSTRAINT PK_PlanAttachments PRIMARY KEY (contextID, guidanceID)
);

ALTER TABLE PlanAttachments
ADD CONSTRAINT FK_PlanAttachments_ProjectPlan
    FOREIGN KEY (contextID, guidanceID)
    REFERENCES ProjectPlan (contextID, guidanceID);

CREATE INDEX IX_PlanAttachments_ProjectPlan
    ON PlanAttachments (contextID, guidanceID);
