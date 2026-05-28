package com.ey.method.migration.repository;

import com.ey.method.migration.model.PlanAttachments;
import com.ey.method.migration.model.PlanAttachmentsId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlanAttachmentsRepository extends JpaRepository<PlanAttachments, PlanAttachmentsId> {
}
