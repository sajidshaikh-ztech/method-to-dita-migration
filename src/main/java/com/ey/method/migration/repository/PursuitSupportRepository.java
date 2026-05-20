package com.ey.method.migration.repository;

import com.ey.method.migration.model.PursuitSupport;
import com.ey.method.migration.model.PursuitSupportId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PursuitSupportRepository extends JpaRepository<PursuitSupport, PursuitSupportId> {
}
