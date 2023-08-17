package com.parsing.repository;

import com.parsing.model.LotId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface LotIdRepository extends JpaRepository<LotId, String> {

    Optional<LotId> findByDateModified();
}
