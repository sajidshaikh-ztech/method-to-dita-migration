package com.ey.method.migration.repository;

import com.ey.method.migration.model.WBS;
import com.ey.method.migration.model.WbsId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WbsRepository extends JpaRepository<WBS, WbsId> {
}
