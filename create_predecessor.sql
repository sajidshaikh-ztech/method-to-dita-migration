-- ============================================================
-- Table: Predecessor
-- Description: Stores predecessor/dependency relationships
--              between WBS items.
-- ============================================================

CREATE TABLE Predecessor (
    contextID           VARCHAR(36) NOT NULL,
    WBSID               VARCHAR(36) NOT NULL,
    dependentOn         VARCHAR(36) NOT NULL,
    dependencyType      VARCHAR(10),

    -- Primary Key (implicitly indexes lookups on upstream predecessors)
    CONSTRAINT PK_Predecessor 
        PRIMARY KEY (contextID, WBSID, dependentOn)
);

-- Index for optimizing downstream lookups (finding dependents)
CREATE INDEX IX_Predecessor_Dependent 
    ON Predecessor (contextID, dependentOn);
