package com.parsing.service;

import com.parsing.model.LotInfo;
import com.parsing.repository.LotInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LotInfoService {

    private final LotInfoRepository lotInfoRepository;

    public List<LotInfo> saveAll(List<LotInfo> lotInfos) {
        return lotInfoRepository.saveAll(lotInfos);
    }
}
