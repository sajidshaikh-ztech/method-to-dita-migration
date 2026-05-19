# Migration Logic Specification: Method XML to MySQL Staging

This document outlines the logic, source XML elements, and attribute mappings used to populate the **EYMethods** staging database from the Rational `method.xml` source file.

## 1. Core Orchestration Logic

### Context Generation (`Context` Table)
*   **Trigger**: Every time the migration application starts, a new entry is created in the `Context` table.
*   **Logic**: A unique GUID (`contextID`) is generated. This ID acts as the relational "glue" for all other records migrated during the same run, allowing for versioning and grouping.
*   **Primary Key**: `contextID` (VARCHAR 36).

### Global Metadata (`DeliveryProcessDefinition` Table)
*   **Source Tag**: Root `<method>` element.
*   **Mapping**:
    *   `external-id` -> `externalID`
    *   `name` -> `name`
    *   `description` -> `mainDescription`
    *   `sponsor`, `steward`, `publish`, `foundation`, etc. -> Respective columns.

---

## 2. Hierarchical Process Items

These tables are populated by a **recursive traversal** of the `<process-item>` tree starting from the root "Delivery Process".

### Activities (`Activity` Table)
*   **Logic**: We search for all `<process-item>` tags that have the type "Activity", "Phase", or "Iteration". Because these items are organized in a parent-child tree structure in the XML, we look through every level of that tree to find and extract them.
*   **Mapping**:
    *   `id` -> `activityID`
    *   `name` -> `name`
    *   `brief-description` -> `briefDescription`
    *   `type` -> `type` (Will correctly store "Activity", "Phase", or "Iteration")

### Milestones (`MilestoneDefinition` Table)
*   **Logic**: We search for all `<process-item>` tags that have the type "Milestone". Just like activities, we look through the entire nested tree structure in the XML to find every milestone.
*   **Mapping**:
    *   `id` -> `milestoneID`
    *   `name` -> `name`
    *   `brief-description` -> `mainDescription`

### Task Usages (`TaskUsage` Table)
*   **Logic**: We search for all `<process-item>` tags that have the type "Task Descriptor" (or "Task Description"). This represents the usage of a specific task within the process hierarchy.
*   **Mapping**:
    *   `id` -> `descriptorID`
    *   `related-task` -> `taskID` (Foreign Key referencing `TaskDefinition`)

### Work Breakdown Structure (`WBS` Table)
*   **Logic**: A junction table intended to represent the hierarchical parent-child relationships between Process Items (Delivery Process, Activities, Phases, Iterations, Milestones, and Task Usages). 
*   **Constraints**: Intentionally does not use database-level foreign keys for `parentID` and `childID` to support polymorphic associations natively.
*   **Mapping**:
    *   Parent element `id` -> `parentID`
    *   Child element `id` -> `childID`
    *   Ordered sequence index -> `index` (Mapped as `itemIndex` in Java)

---

## 3. Flat Methodology Definitions

These tables are populated by iterating through top-level collection tags in the XML.

### Tasks (`TaskDefinition` Table)
*   **Source Tag**: `<tasks>/<task>`
*   **Mapping**:
    *   `id` -> `taskID`
    *   `name` -> `name`
    *   `name` -> `presentationName`
    *   `brief-description` -> `briefDescription`
    *   `variabilityBasedOnElement` -> `variabilityBasedOn`

### Task Relationships
#### Role Associations (`TaskToRole` Table)
*   **Logic**: Each task links to one or more roles responsible for its execution.
*   **Constraints**: Physical Foreign Keys to `TaskDefinition` and `RoleDefinition`.
*   **Mapping**:
    *   Parent `id` -> `taskID`
    *   `<method-roles>/<method-role-id>` -> `roleID` (Rel: `primary performer`)
    *   `<add-method-roles>/<method-role-id>` -> `roleID` (Rel: `additional performer`)

#### Work Product Associations (`TaskToWorkProduct` Table)
*   **Logic**: Tasks have input and output work products.
*   **Mapping**:
    *   Parent `id` -> `taskID`
    *   `<input-work-products>/<wp-id>` -> `workProductID` (Rel: `Input`)
    *   `<output-work-products>/<wp-id>` -> `workProductID` (Rel: `Output`)
*   **Audit Logic**: Since work products are split across three tables (`WorkProductDefinition`, `DeliverableDefinition`, `OutcomeDefinition`), a complex `LEFT JOIN` audit is performed to ensure every relationship ID exists in at least one of these tables.

#### Guidance Associations (`TaskToGuidance` Table)
*   **Logic**: Tasks refer to supporting guidance artifacts (Templates, Policies, etc.).
*   **Constraints**: Physical Foreign Keys to `TaskDefinition` and `GuidanceDefinition`.
*   **Mapping**:
    *   Parent `id` -> `taskID`
    *   `<guidances>/<guidance-id>` -> `guidanceID`

### Roles (`RoleDefinition` Table)
*   **Source Tag**: `<method-roles>/<method-role>`
*   **Mapping**:
    *   `id` -> `roleID`
    *   `name` -> `name`
    *   `name` -> `presentationName`
    *   `brief-description` -> `briefDescription`

### Role Relationships (`RoleToGuidance` Table)
*   **Logic**: While processing each role, we look for a nested `<guidances>` tag. For every `<guidance-id>` found inside it, we create a relationship row linking the role to the guidance.
*   **Constraints**: This table uses physical **Foreign Key constraints** linking to `RoleDefinition` and `GuidanceDefinition`.
*   **Performance**: Indexes `IX_RTG_Role` and `IX_RTG_Guidance` are applied to optimize lookups.
*   **Mapping**:
    *   Parent `id` -> `roleID`
    *   Child `<guidance-id>` content -> `guidanceID`
    *   `variabilityBasedOnElement` -> `variabilityBasedOn`

### Workstreams (`WorkstreamDefinition` Table)
*   **Source Tag**: `<workstreams>/<workstream>`
*   **Mapping**:
    *   `id` -> `workstreamID`
    *   `name` -> `name`
    *   `description` -> `briefDescription`
*   **Secondary XML Sourcing**:
    *   Find the detailed XML file under `src/main/resources/input/xml/` using the HTML base name from the `method-link` attribute with the prefix `CapabilityPattern.`. E.g., `CapabilityPattern.org_chng_mgmt_training_wkstrm_2016F615.xml`.
    *   `presentationName` -> `presentationName`
    *   `purpose` -> `purpose`
    *   `mainDescription` -> `mainDescription`
    *   `keyConsiderations` -> `keyConsiderations`
    *   `usageNotes` -> `usageNotes`
    *   `changeDate` -> `changeDate` (Date)
    *   `changeDescription` -> `changeDescription` (History)
    *   `originatingProcess` -> extracted from the URL path of the detailed XML

#### Workstream Associations (`WorkstreamToTask` Table)
*   **Logic**: Captures the task IDs nested within a workstream. In this specific XML, these IDs often reflect the workstream ID rather than standard task definitions, pointing to a placeholder structure.
*   **Constraints**: Physical Foreign Key only to `WorkstreamDefinition`. The Foreign Key to `TaskDefinition` is intentionally **omitted** because the placeholder IDs do not exist in the Task table.
*   **Mapping**:
    *   Parent `id` -> `workstreamID`
    *   `<tasks>/<tasks-id>` -> `taskID`

### Work Product Definitions (`WorkProductDefinition` Table)
*   **Source Tag**: `<work-products>/<work-product>`
*   **Filter**: `type="Work Product"`
*   **Mapping**:
    *   `id` -> `workProductID`
    *   `name` -> `name`
    *   `name` -> `presentationName`
    *   `brief-description` -> `briefDescription`
    *   `variabilityBasedOnElement` -> `variabilityBasedOn`
*   **Secondary XML Sourcing**:
    *   Extracts the value of `method-link` attribute from the `<work-product>` tag to determine the name of the secondary XML file. The base name (extracted from `.html` portion) is prefixed with `WorkProductDescriptor.` (with a fallback option of `Artifact.`) and appended with `.xml` (e.g., `#core.i_t.common.base/workproducts/form_comp_7A2B46ED.html` maps to file `WorkProductDescriptor.form_comp_7A2B46ED.xml`).
    *   Sourced properties from the secondary XML file:
        *   `purpose` -> `purpose`
        *   `mainDescription` -> `mainDescription`
        *   `keyConsiderations` -> `keyConsiderations`
        *   `briefOutline` -> `briefOutline`
        *   `selectedRepresentation` -> `selectedRepresentation`
        *   `impactOfNotHaving` -> `impactOfNotHaving`
        *   `reasonsForNotNeeding` -> `reasonsForNotNeeding`
        *   `representationOptions` -> `representationOptions`
        *   `methodSpecificInformation` -> `methodSpecificInformation`
        *   `synonym` -> `synonym`
        *   `externalId` -> `externalID`
        *   `changeDate` -> `changeDate` (parsed from `EEE MMM dd HH:mm:ss zzz yyyy` string format to standard Date)
        *   `changeDescription` -> `changeDescription`
        *   `url` -> `originatingProcess` (extracted from the prefix of the URL path before the first slash)

### Outcome Definitions (`OutcomeDefinition` Table)
*   **Source Tag**: `<work-products>/<work-product>`
*   **Filter**: `type="Outcome"`
*   **Mapping**:
    *   `id` -> `workProductID`
    *   `name` -> `name`
    *   `brief-description` -> `briefDescription`
*   **Secondary XML Sourcing**:
    *   Extracts the value of `method-link` attribute from the `<work-product>` tag to determine the name of the secondary XML file. The base name (extracted from `.html` portion) is prefixed with `Outcome.` and appended with `.xml` (e.g., `#core.mgmt.common.base/workproducts/team_resources_released_E84F59A9.html` maps to file `Outcome.team_resources_released_E84F59A9.xml`).
    *   Sourced properties from the secondary XML file:
        *   `purpose` -> `purpose`
        *   `mainDescription` -> `mainDescription`
        *   `keyConsiderations` -> `keyConsiderations`
        *   `impactOfNotHaving` -> `impactOfNotHaving`
        *   `reasonsForNotNeeding` -> `reasonsForNotNeeding`
        *   `changeDate` -> `changeDate` (parsed from `EEE MMM dd HH:mm:ss zzz yyyy` string format to standard Date)
        *   `changeDescription` -> `changeHistory`
        *   `url` -> `originatingProcess` (extracted from the prefix of the URL path before the first slash)

### Deliverable Definitions (`DeliverableDefinition` Table)
*   **Source Tag**: `<work-products>/<work-product>`
*   **Filter**: `type="Deliverable"`
*   **Mapping**:
    *   `id` -> `workProductID`
    *   `name` -> `name`
    *   `name` -> `presentationName`
    *   `brief-description` -> `briefDescription`
*   **Secondary XML Sourcing**:
    *   Extracts the value of `method-link` attribute from the `<work-product>` tag to determine the name of the secondary XML file. The base name (extracted from `.html` portion) is prefixed with `Deliverable.` and appended with `.xml` (e.g., `#process.adv.eya_oracle.base/workproducts/master_ricefw_inv_9148E646.html` maps to file `Deliverable.master_config_workbook_9152398B.xml` pattern or specific base file).
    *   Sourced properties from the secondary XML file:
        *   `purpose` -> `clientValue`
        *   `mainDescription` -> `internalDescription`
        *   `externalDescription` -> `externalDescription`
        *   `packagingGuidance` -> `packagingGuidance`
        *   `changeDate` -> `changeDate` (parsed from `EEE MMM dd HH:mm:ss zzz yyyy` string format to standard Date)
        *   `changeDescription` -> `changeDescription`
        *   `url` -> `originating_process` (extracted from the prefix of the URL path before the first slash)

### Guidance Definitions (`GuidanceDefinition` Table)
*   **Source Tag**: `<guidances>/<guidance>`
*   **Filter**: None (Migrates all guidance types)
*   **Mapping**:
    *   `id` -> `guidanceID`
    *   `type` -> `type`
    *   `name` -> `name`
    *   `name` -> `presentationName`
    *   `brief-description` -> `briefDescription`
*   **Secondary XML Sourcing**:
    *   Extracts the value of `method-link` attribute from the `<guidance>` tag to determine the name of the secondary XML file. The base name (extracted from the `.html` portion) is prefixed with `[Type].` (e.g., if `type="Concept"`, it prefixes with `Concept.what_is_proj_mgmt_1141E546.xml` pattern) and appended with `.xml`.
    *   Sourced properties from the secondary XML file:
        *   `mainDescription` -> `mainDescription`
        *   `sourceType` -> `sourceType`
        *   `sourcePath` -> `sourcePath`
        *   `changeDate` -> `changeDate` (parsed from `EEE MMM dd HH:mm:ss zzz yyyy` string format to standard Date)
        *   `changeDescription` -> `changeHistory`
        *   `url` -> `originatingProcess` (extracted from the prefix of the URL path before the first slash)

### Guidance Relationships (`GuidanceToGuidance` Table)
*   **Logic**: While processing each guidance, we look for a nested `<guidances>` tag. For every `<guidance-id>` found inside it, we create a relationship row linking the parent guidance to the related guidance.
*   **Implementation Strategy**: This is done in a **single pass**; the main guidance is persisted first, followed immediately by its nested relationships.
*   **Mapping**:
    *   Parent `id` -> `guidanceID`
    *   Child `<guidance-id>` content -> `relatedGuidance`
    *   `relationshipType` -> Set to "Related" by default.

### Project Plans (`ProjectPlan` Table)
*   **Source Tag**: `<guidances>/<guidance>`
*   **Filter**: `type="Project Plan"`
*   **Mapping**:
    *   `id` -> `guidanceID`
    *   `name` -> `name`
    *   `name` -> `presentationName`
    *   `brief-description` -> `mainDescription`

---

## 4. Technical Constraints
1.  **Execution Order**: To satisfy database foreign key requirements, flat definitions (Tasks, Roles, Work Products, etc.) are parsed and persisted *before* hierarchical elements (Task Usages) that depend on them.
2.  **Composite Primary Keys**: Most definition tables use a composite key of `(ID, contextID)` to ensure data integrity across multiple migration runs.
3.  **Data Types**: 
    *   `TEXT` or `LONGTEXT` is used for large content fields (like `instructions` or `mainDescription`) to prevent truncation.
    *   `VARCHAR(255)` is used for names to accommodate actual XML data lengths.
4.  **Physical Naming**: The application uses `PhysicalNamingStrategyStandardImpl` to preserve exact case-sensitive table names as defined in the EYMethods schema.
