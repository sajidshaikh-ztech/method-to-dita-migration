CREATE TABLE PursuitSupport (
    contextID            VARCHAR(36) NOT NULL,
    guidanceID           VARCHAR(25) NOT NULL,

    name                 VARCHAR(50),
    presentationName     VARCHAR(50),

    solutionOverview     TEXT,
    elevatorPitch        TEXT,
    valueProp            TEXT,
    positioning          TEXT,
    scoping              TEXT,
    estimating           TEXT,
    additionalInfo       TEXT,

    CONSTRAINT PK_PursuitSupport
        PRIMARY KEY (contextID, guidanceID)
);

ALTER TABLE PursuitSupport
ADD CONSTRAINT FK_PursuitSupport_GuidanceDefinition
    FOREIGN KEY (guidanceID, contextID)
    REFERENCES GuidanceDefinition (guidanceID, contextID);

CREATE INDEX IX_PursuitSupport_Context
    ON PursuitSupport (contextID);

CREATE INDEX IX_PursuitSupport_Guidance
    ON PursuitSupport (guidanceID);
