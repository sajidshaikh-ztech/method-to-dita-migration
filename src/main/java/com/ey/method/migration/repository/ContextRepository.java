package com.ey.method.migration.repository;

import com.ey.method.migration.model.Context;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContextRepository extends JpaRepository<Context, String> {
}
