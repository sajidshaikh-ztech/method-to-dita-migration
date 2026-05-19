package com.ey.method.migration.repository;

import com.ey.method.migration.model.TaskToRole;
import com.ey.method.migration.model.TaskToRoleId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskToRoleRepository extends JpaRepository<TaskToRole, TaskToRoleId> {
}
