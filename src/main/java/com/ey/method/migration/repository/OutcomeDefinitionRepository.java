package com.ey.method.migration.repository;

import com.ey.method.migration.model.OutcomeDefinition;
import com.ey.method.migration.model.OutcomeDefinitionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OutcomeDefinitionRepository extends JpaRepository<OutcomeDefinition, OutcomeDefinitionId> {
}
