package com.ey.method.migration.repository;

import com.ey.method.migration.model.ProjectPlan;
import com.ey.method.migration.model.ProjectPlanId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectPlanRepository extends JpaRepository<ProjectPlan, ProjectPlanId> {
}
