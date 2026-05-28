-- ============================================================
-- Table: ReleaseInformation
-- Description: Stores release metadata for each guidance item
--              associated with a delivery process.
-- ============================================================

CREATE TABLE ReleaseInformation (
    contextID           VARCHAR(36) NOT NULL,
    guidanceID          VARCHAR(25) NOT NULL,

    name                VARCHAR(50),
    presentationName    VARCHAR(75),

    whatsNew            VARCHAR(3500),
    revisionHistory     VARCHAR(16700),
    acknowledgements    VARCHAR(2100),
    internalHistory     VARCHAR(8300),
    designerCommentary  VARCHAR(300),

    lastReviewed        DATE,

    processID           VARCHAR(36),

    -- Primary Key
    CONSTRAINT PK_ReleaseInformation
        PRIMARY KEY (contextID, guidanceID),

    -- Foreign Key to DeliveryProcessDefinition
    CONSTRAINT FK_ReleaseInformation_DeliveryProcessDefinition
        FOREIGN KEY (processID, contextID)
        REFERENCES DeliveryProcessDefinition(processID, contextID)
);
