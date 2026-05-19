package com.ey.method.migration.repository;

import com.ey.method.migration.model.GuidanceToGuidance;
import com.ey.method.migration.model.GuidanceToGuidanceId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GuidanceToGuidanceRepository extends JpaRepository<GuidanceToGuidance, GuidanceToGuidanceId> {
}
