package com.ey.method.migration.repository;

import com.ey.method.migration.model.WorkProductDefinition;
import com.ey.method.migration.model.WorkProductDefinitionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkProductDefinitionRepository extends JpaRepository<WorkProductDefinition, WorkProductDefinitionId> {
}
