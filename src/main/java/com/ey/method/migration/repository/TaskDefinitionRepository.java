package com.ey.method.migration.repository;

import com.ey.method.migration.model.TaskDefinition;
import com.ey.method.migration.model.TaskDefinitionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskDefinitionRepository extends JpaRepository<TaskDefinition, TaskDefinitionId> {
}
