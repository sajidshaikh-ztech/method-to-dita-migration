package com.ey.method.migration.repository;

import com.ey.method.migration.model.WorkstreamDefinition;
import com.ey.method.migration.model.WorkstreamDefinitionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkstreamDefinitionRepository extends JpaRepository<WorkstreamDefinition, WorkstreamDefinitionId> {
}
