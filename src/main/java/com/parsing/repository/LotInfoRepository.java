package com.parsing.repository;

import com.parsing.model.LotInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LotInfoRepository extends JpaRepository<LotInfo, UUID> {
}
