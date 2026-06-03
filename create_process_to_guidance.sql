-- ============================================================
-- Table: ProcessToGuidance
-- Description: Stores relationships between Delivery Processes
--              and Guidance definitions.
-- ============================================================

DROP TABLE IF EXISTS ProcessToGuidance;

CREATE TABLE ProcessToGuidance (
    contextID           VARCHAR(36) NOT NULL,
    processID           VARCHAR(36) NOT NULL,
    guidanceID          VARCHAR(25) NOT NULL,

    -- Primary Key
    CONSTRAINT PK_ProcessToGuidance
        PRIMARY KEY (contextID, processID, guidanceID)
);

-- Foreign Key to DeliveryProcessDefinition
ALTER TABLE ProcessToGuidance
ADD CONSTRAINT FK_ProcessToGuidance_DeliveryProcessDefinition
    FOREIGN KEY (processID)
    REFERENCES DeliveryProcessDefinition (processID);

-- Foreign Key to GuidanceDefinition
ALTER TABLE ProcessToGuidance
ADD CONSTRAINT FK_ProcessToGuidance_GuidanceDefinition
    FOREIGN KEY (guidanceID, contextID)
    REFERENCES GuidanceDefinition (guidanceID, contextID);

-- Indexes for optimizing lookups
CREATE INDEX IX_ProcessToGuidance_Process
    ON ProcessToGuidance (contextID, processID);

CREATE INDEX IX_ProcessToGuidance_Guidance
    ON ProcessToGuidance (contextID, guidanceID);
