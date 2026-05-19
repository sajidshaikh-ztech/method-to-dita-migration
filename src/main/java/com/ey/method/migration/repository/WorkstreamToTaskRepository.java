package com.ey.method.migration.repository;

import com.ey.method.migration.model.WorkstreamToTask;
import com.ey.method.migration.model.WorkstreamToTaskId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkstreamToTaskRepository extends JpaRepository<WorkstreamToTask, WorkstreamToTaskId> {
}
