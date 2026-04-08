package com.legacy.report.repository;

import com.legacy.report.model.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {

    List<Report> findByIsDeleted(Integer isDeleted);

    Optional<Report> findByIdAndIsDeleted(Long id, Integer isDeleted);
}
