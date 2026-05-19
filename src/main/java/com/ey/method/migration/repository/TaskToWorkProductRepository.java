package com.ey.method.migration.repository;

import com.ey.method.migration.model.TaskToWorkProduct;
import com.ey.method.migration.model.TaskToWorkProductId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskToWorkProductRepository extends JpaRepository<TaskToWorkProduct, TaskToWorkProductId> {
}
