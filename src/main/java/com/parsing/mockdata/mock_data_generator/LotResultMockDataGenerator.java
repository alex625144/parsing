package com.parsing.mockdata.mock_data_generator;

import com.parsing.model.LotResult;
import com.parsing.model.Participant;
import com.parsing.model.Status;
import com.parsing.repository.LotResultRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Component
@RequiredArgsConstructor
public class LotResultMockDataGenerator {

    private final LotResultRepository lotResultRepository;
    private final ParticipantMockDataSource participantMockDataSource;
    private static final int MAX_PARTICIPANTS = 10;
    private final int LOT_RESULT_QUANTITY = 10;
    private final String URL_TEMPLATE = "https://parsing.com/";
    private final String DK = "009023";
    private int lotCounter = 0;

    @Transactional
    public List<LotResult> generate() {
        List<LotResult> lotResults = new ArrayList<>();

        for (int i = 0; i < LOT_RESULT_QUANTITY; i++) {
            LotResult lotResult = new LotResult();

            lotResult.setBuyer(participantMockDataSource.getNextParticipant());
            lotResult.setSeller(participantMockDataSource.getNextParticipant());
            lotResult.setDk(DK);
            lotResult.setParticipants(getMockParticipants());
            lotResult.setLotURL(URL_TEMPLATE + "/lot" + lotCounter++);
            lotResult.setPdfURL(URL_TEMPLATE + "/pdf" + lotCounter);
            lotResult.setStatus(randomStatusGenerator());
            lotResult.setDateModified(ZonedDateTime.now());

            if (lotResult.getStatus() == Status.PDF_SUCCESSFULL)
                lotResult.setLotTotalPrice(BigDecimal.valueOf(ThreadLocalRandom.current().nextInt(50_000, 1_000_000)));

            lotResults.add(lotResult);
        }

        return lotResultRepository.saveAll(lotResults);
    }

    private Status randomStatusGenerator() {
        int minValue = 0;
        int maxValue = 8;

        return switch (ThreadLocalRandom.current().nextInt(minValue, maxValue)) {
            case 0 -> Status.CREATED;
            case 1 -> Status.PARSED;
            case 2 -> Status.PROCESSED;
            case 3 -> Status.DOWNLOADED;
            case 4 -> Status.PDF_SUCCESSFULL;
            case 5 -> Status.PDF_FAILED;
            case 6 -> Status.MAPPED_TO_INFO_SUCCESSFULL;
            case 7 -> Status.MAPPED_TO_INFO_FAILED;
            default ->
                    throw new IllegalStateException("Unexpected value: " + ThreadLocalRandom.current().nextInt(minValue, maxValue));
        };
    }

    private List<Participant> getMockParticipants() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        int participantsQuantity = random.nextInt(0, MAX_PARTICIPANTS);
        List<Participant> participants = new ArrayList<>(participantsQuantity);

        for (int i = 0; i < participantsQuantity; i++) {
            participants.add(participantMockDataSource.getNextParticipant());
        }

        return participants;
    }
}
