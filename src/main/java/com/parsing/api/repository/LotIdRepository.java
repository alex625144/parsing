package com.parsing.api.repository;

import com.parsing.api.model.LotId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LotIdRepository extends JpaRepository<LotId, UUID> {
}