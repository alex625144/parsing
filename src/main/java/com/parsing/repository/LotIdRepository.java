package com.parsing.repository;

import com.parsing.model.LotId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LotIdRepository extends JpaRepository<LotId, String> {
}
