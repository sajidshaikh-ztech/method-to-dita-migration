package com.ey.method.migration.repository;

import com.ey.method.migration.model.ReleaseInformation;
import com.ey.method.migration.model.ReleaseInformationId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReleaseInformationRepository extends JpaRepository<ReleaseInformation, ReleaseInformationId> {
}
