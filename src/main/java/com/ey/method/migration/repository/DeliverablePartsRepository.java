package com.ey.method.migration.repository;

import com.ey.method.migration.model.DeliverableParts;
import com.ey.method.migration.model.DeliverablePartsId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeliverablePartsRepository extends JpaRepository<DeliverableParts, DeliverablePartsId> {
}
