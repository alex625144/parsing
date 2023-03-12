package com.parsing.repository;

import com.parsing.model.Lot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LotRepository extends JpaRepository<Lot, UUID> {
}
