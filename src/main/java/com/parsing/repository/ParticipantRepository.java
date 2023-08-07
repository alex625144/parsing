package com.parsing.repository;

import com.parsing.model.LotResult;
import com.parsing.model.Participant;
import com.parsing.model.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ParticipantRepository extends JpaRepository<Participant, UUID> {

    Participant findByEdrpou(String edrpou);
}
