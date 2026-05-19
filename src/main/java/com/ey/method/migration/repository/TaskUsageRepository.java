package com.ey.method.migration.repository;

import com.ey.method.migration.model.TaskUsage;
import com.ey.method.migration.model.TaskUsageId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskUsageRepository extends JpaRepository<TaskUsage, TaskUsageId> {
}
