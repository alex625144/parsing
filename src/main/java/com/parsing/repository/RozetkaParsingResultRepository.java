package com.parsing.repository;

import com.parsing.model.RozetkaParsingReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RozetkaParsingResultRepository extends JpaRepository<RozetkaParsingReport, UUID> {
}
