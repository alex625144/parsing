package com.parsing.repository;

import com.parsing.model.LotResult;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface LotResultRepository extends JpaRepository<LotResult, UUID> {
}
