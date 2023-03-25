package com.parsing.repository;

import com.parsing.model.RozetkaParsingReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RozetkaParsingReportRepository extends JpaRepository<RozetkaParsingReport, UUID> {
}
