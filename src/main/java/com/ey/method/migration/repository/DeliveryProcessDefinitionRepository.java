package com.ey.method.migration.repository;

import com.ey.method.migration.model.DeliveryProcessDefinition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeliveryProcessDefinitionRepository extends JpaRepository<DeliveryProcessDefinition, String> {
}
