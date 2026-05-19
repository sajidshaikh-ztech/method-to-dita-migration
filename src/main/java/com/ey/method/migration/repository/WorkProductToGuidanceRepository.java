package com.ey.method.migration.repository;

import com.ey.method.migration.model.WorkProductToGuidance;
import com.ey.method.migration.model.WorkProductToGuidanceId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkProductToGuidanceRepository extends JpaRepository<WorkProductToGuidance, WorkProductToGuidanceId> {
}
