# Migration Implementation Skills

This file contains specialized instructions for implementing the Method XML to MySQL Staging migration logic as defined in the `migration-logic-specification.md`.

## Skill: Initialize Migration Context
**Objective**: Ensure every migration run is isolated and traceable.
1. Generate a unique `contextID` (GUID).
2. Create a new record in the `Context` table.
3. Map root `<method>` attributes to the `DeliveryProcessDefinition` table, linking them to the new `contextID`.
4. Store the `contextID` in the service state for subsequent operations.

## Skill: Migrate Flat Methodology Definitions
**Objective**: Populate reference tables before hierarchical items to satisfy foreign key constraints.
1. **Order of Execution**:
   - Guidance Definitions
   - Work Product Definitions (Outcomes, Deliverables, Master)
   - Role Definitions
   - Task Definitions
   - Workstream Definitions
2. **Logic for each**:
   - Iterate through top-level collection tags (e.g., `<tasks>/<task>`).
   - Map XML attributes to JPA entity fields using composite keys `(ID, contextID)`.
   - Persist immediately after mapping.

## Skill: Migrate Hierarchical Process Items
**Objective**: Recursively traverse the `<process-item>` tree.
1. Start at the root "Delivery Process".
2. **For each node**:
   - Identify `type` (Activity, Phase, Iteration, Milestone, Task Descriptor).
   - Map to corresponding table (`Activity`, `MilestoneDefinition`, `TaskUsage`).
   - **WBS Entry**: Create a record in the `WBS` table linking `parentID` to `childID` with the correct `itemIndex`.
   - Recurse into children.

## Skill: Populate Multi-Entity Relationships
**Objective**: Build junction tables for many-to-many associations.
1. **Task-to-X**:
   - Map primary and additional performers to `TaskToRole`.
   - Map inputs and outputs to `TaskToWorkProduct`.
   - Map supporting guidance to `TaskToGuidance`.
2. **Role-to-Guidance**: Map nested `<guidance-id>` within roles.
3. **Guidance-to-Guidance**: Map related guidance IDs.
4. **Workstream-to-Task**: Map tasks within workstreams (ignoring FK constraints for placeholder IDs).

## Skill: Post-Migration Audit
**Objective**: Validate data integrity and relationship consistency.
1. Execute `LEFT JOIN` queries to identify orphaned relationships (referencing IDs that don't exist in the current context).
2. Specifically audit `TaskToWorkProduct` across three source tables (`WorkProductDefinition`, `DeliverableDefinition`, `OutcomeDefinition`).
3. Log warnings for any broken links found.
