-- ============================================================
-- Table: WorkProductToWorkProduct
-- Description: Stores relationships between different Work Products
--              (polymorphic: Work Product, Outcome, or Deliverable).
-- ============================================================

DROP TABLE IF EXISTS WorkProductToWorkProduct;

CREATE TABLE WorkProductToWorkProduct (
    contextID            VARCHAR(36) NOT NULL,
    workProductID        VARCHAR(25) NOT NULL,
    relatedWorkProductID VARCHAR(25) NOT NULL,
    relationshipType     VARCHAR(50) DEFAULT NULL,

    -- Primary Key
    CONSTRAINT PK_WorkProductToWorkProduct
        PRIMARY KEY (contextID, workProductID, relatedWorkProductID)
);

-- Indexes for optimizing lookups
CREATE INDEX IX_WP2WP_Source
    ON WorkProductToWorkProduct (contextID, workProductID);

CREATE INDEX IX_WP2WP_Target
    ON WorkProductToWorkProduct (contextID, relatedWorkProductID);
