package com.ey.method.migration.repository;

import com.ey.method.migration.model.GuidanceDefinition;
import com.ey.method.migration.model.GuidanceDefinitionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GuidanceDefinitionRepository extends JpaRepository<GuidanceDefinition, GuidanceDefinitionId> {
}
