package com.ey.method.migration.repository;

import com.ey.method.migration.model.RoleDefinition;
import com.ey.method.migration.model.RoleDefinitionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleDefinitionRepository extends JpaRepository<RoleDefinition, RoleDefinitionId> {
}
