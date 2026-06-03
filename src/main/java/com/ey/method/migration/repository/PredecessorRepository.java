package com.ey.method.migration.repository;

import com.ey.method.migration.model.Predecessor;
import com.ey.method.migration.model.PredecessorId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PredecessorRepository extends JpaRepository<Predecessor, PredecessorId> {
}
