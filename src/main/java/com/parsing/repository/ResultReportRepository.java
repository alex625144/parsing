package com.parsing.repository;

import com.parsing.model.ResultReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ResultReportRepository extends JpaRepository<ResultReport, UUID> {
}
