package com.ey.method.migration.repository;

import com.ey.method.migration.model.RoleToGuidance;
import com.ey.method.migration.model.RoleToGuidanceId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleToGuidanceRepository extends JpaRepository<RoleToGuidance, RoleToGuidanceId> {
}
