package com.ey.method.migration.repository;

import com.ey.method.migration.model.MilestoneDefinition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MilestoneDefinitionRepository extends JpaRepository<MilestoneDefinition, String> {
}
