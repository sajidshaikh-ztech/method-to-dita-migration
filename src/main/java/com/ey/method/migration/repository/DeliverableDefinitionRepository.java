package com.ey.method.migration.repository;

import com.ey.method.migration.model.DeliverableDefinition;
import com.ey.method.migration.model.DeliverableDefinitionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeliverableDefinitionRepository extends JpaRepository<DeliverableDefinition, DeliverableDefinitionId> {
}
