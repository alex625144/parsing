package com.parsing.repository;

import com.parsing.model.ResultReportItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ResultReportItemRepository extends JpaRepository<ResultReportItem, UUID> {
}
