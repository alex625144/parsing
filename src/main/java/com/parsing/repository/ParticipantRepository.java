package com.parsing.repository;

import com.parsing.model.LotResult;
import com.parsing.model.Participant;
import com.parsing.model.Status;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ParticipantRepository extends JpaRepository<Participant, UUID> {

    Optional<Participant> findByEdrpou(String edrpou);
}
