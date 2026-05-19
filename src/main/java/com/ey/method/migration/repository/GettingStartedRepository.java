package com.ey.method.migration.repository;

import com.ey.method.migration.model.GettingStarted;
import com.ey.method.migration.model.GettingStartedId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GettingStartedRepository extends JpaRepository<GettingStarted, GettingStartedId> {
}
