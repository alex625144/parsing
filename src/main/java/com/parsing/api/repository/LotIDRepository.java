package com.parsing.api.repository;

import com.parsing.api.model.LotID;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LotIDRepository extends JpaRepository<LotID, UUID> {
}
