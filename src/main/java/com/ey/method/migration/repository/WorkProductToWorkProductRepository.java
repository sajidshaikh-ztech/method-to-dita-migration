package com.ey.method.migration.repository;

import com.ey.method.migration.model.WorkProductToWorkProduct;
import com.ey.method.migration.model.WorkProductToWorkProductId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkProductToWorkProductRepository extends JpaRepository<WorkProductToWorkProduct, WorkProductToWorkProductId> {
}
