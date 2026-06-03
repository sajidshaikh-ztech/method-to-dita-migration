-- ============================================================
-- Table: StepDefinition
-- Description: Stores Step definitions.
-- ============================================================
DROP TABLE IF EXISTS TaskToStep;
DROP TABLE IF EXISTS StepDefinition;

CREATE TABLE StepDefinition (
    contextID           VARCHAR(36) NOT NULL,
    stepID              VARCHAR(25) NOT NULL,
    name                VARCHAR(255),
    presentationName    VARCHAR(255),
    briefDescription    VARCHAR(1000),
    mainDescription     TEXT,
    changeDate          DATE,
    changeDescription   TEXT,
    originatingProcess  VARCHAR(255),
    variabilityBasedOn  VARCHAR(25),

    CONSTRAINT PK_StepDefinition
        PRIMARY KEY (stepID, contextID)
);

CREATE INDEX IX_StepDefinition_Context
    ON StepDefinition (contextID);

CREATE INDEX IX_StepDefinition_Step
    ON StepDefinition (stepID);

-- ============================================================
-- Table: TaskToStep
-- Description: Stores relationship between Tasks and Steps.
-- ============================================================
CREATE TABLE TaskToStep (
    contextID           VARCHAR(36) NOT NULL,
    taskID              VARCHAR(25) NOT NULL,
    stepID              VARCHAR(25) NOT NULL,

    CONSTRAINT PK_TaskToStep
        PRIMARY KEY (contextID, taskID, stepID),

    CONSTRAINT FK_TTS_TaskDefinition
        FOREIGN KEY (taskID, contextID)
        REFERENCES TaskDefinition (taskID, contextID),

    CONSTRAINT FK_TTS_StepDefinition
        FOREIGN KEY (stepID, contextID)
        REFERENCES StepDefinition (stepID, contextID)
);

CREATE INDEX IX_TTS_Task
    ON TaskToStep (contextID, taskID);

CREATE INDEX IX_TTS_Step
    ON TaskToStep (contextID, stepID);

-- ============================================================
-- Table: TeamDefinition
-- Description: Stores Team definitions.
-- ============================================================
DROP TABLE IF EXISTS TeamToTask;
DROP TABLE IF EXISTS TeamDefinition;

CREATE TABLE TeamDefinition (
    contextID           VARCHAR(36) NOT NULL,
    teamID              VARCHAR(25) NOT NULL,
    name                VARCHAR(255),
    presentationName    VARCHAR(255),
    briefDescription    VARCHAR(1000),
    mainDescription     TEXT,
    changeDate          DATE,
    changeDescription   TEXT,
    originatingProcess  VARCHAR(255),
    variabilityBasedOn  VARCHAR(25),

    CONSTRAINT PK_TeamDefinition
        PRIMARY KEY (teamID, contextID)
);

CREATE INDEX IX_TeamDefinition_Context
    ON TeamDefinition (contextID);

CREATE INDEX IX_TeamDefinition_Team
    ON TeamDefinition (teamID);

-- ============================================================
-- Table: TeamToTask
-- Description: Stores relationship between Teams and Tasks.
-- ============================================================
CREATE TABLE TeamToTask (
    contextID           VARCHAR(36) NOT NULL,
    teamID              VARCHAR(25) NOT NULL,
    taskID              VARCHAR(25) NOT NULL,

    CONSTRAINT PK_TeamToTask
        PRIMARY KEY (contextID, teamID, taskID),

    CONSTRAINT FK_TTT_TeamDefinition
        FOREIGN KEY (teamID, contextID)
        REFERENCES TeamDefinition (teamID, contextID),

    CONSTRAINT FK_TTT_TaskDefinition
        FOREIGN KEY (taskID, contextID)
        REFERENCES TaskDefinition (taskID, contextID)
);

CREATE INDEX IX_TTT_Team
    ON TeamToTask (contextID, teamID);

CREATE INDEX IX_TTT_Task
    ON TeamToTask (contextID, taskID);
