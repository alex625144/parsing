package com.parsing.repository;

import com.parsing.model.LaptopItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LaptopItemRepository extends JpaRepository<LaptopItem, UUID> {
}
