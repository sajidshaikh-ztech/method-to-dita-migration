package com.ey.method.migration.repository;

import com.ey.method.migration.model.TaskToGuidance;
import com.ey.method.migration.model.TaskToGuidanceId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskToGuidanceRepository extends JpaRepository<TaskToGuidance, TaskToGuidanceId> {
}
