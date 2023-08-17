package com.parsing.repository;

import com.parsing.model.LotResult;
import com.parsing.model.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface LotResultRepository extends JpaRepository<LotResult, UUID> {

    List<LotResult> findAllByStatus(Status status);

    List<LotResult> findAllByStatusAndLotPDFResultIsNull(Status status);

    List<LotResult> findAllByStatusAndPdfURLNotNull(Status status);

    List<LotResult> findAllByStatusIn(List<Status> statuses);
}
