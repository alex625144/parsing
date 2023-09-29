package com.parsing.service;

import com.parsing.model.LotResult;
import com.parsing.model.Status;
import com.parsing.repository.LotResultRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LotResultService {

    private final LotResultRepository lotResultRepository;

    @Transactional
    public List<LotResult> findAllPDFParserLots() {
        return lotResultRepository.findAllByStatusIn(List.of(Status.PDF_FAILED, Status.PDF_SUCCESSFULL));
    }

    public List<LotResult> saveAll(List<LotResult> lotResults) {
        return lotResultRepository.saveAll(lotResults);
    }

    public List<LotResult> findAllByStatus(Status status) {
        return lotResultRepository.findAllByStatus(status);
    }
}
