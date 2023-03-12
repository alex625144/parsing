package com.parsing.repository;

import com.parsing.model.LotPDFResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LotPDFResultRepository extends JpaRepository<LotPDFResult, UUID> {
}
