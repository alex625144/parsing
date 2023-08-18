package com.parsing.mockdata.mock_data_generator;

import com.parsing.model.Participant;
import com.parsing.repository.ParticipantRepository;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class ParticipantMockDataSource {

    String source = "src/main/resources/mock/seller-buyer.txt";
    private final List<Participant> participants;
    private int counter = 0;
    private int edrpouCounter = 0;

    private final ParticipantRepository participantRepository;

    public ParticipantMockDataSource(ParticipantRepository participantRepository) {
        this.participantRepository = participantRepository;
        this.participants = generate();
    }

    private List<Participant> generate() {
        List<Participant> generatedParticipants;
        Set<String> uniqueValues = new HashSet<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(source), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                uniqueValues.add(line);
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        generatedParticipants = new ArrayList<>(uniqueValues.stream()
                .map(name -> {
                    var participant = new Participant();
                    participant.setEdrpou(String.valueOf(++edrpouCounter));
                    participant.setName(name);
                    return participant;
                })
                .toList());
        Collections.shuffle(generatedParticipants);

        return participantRepository.saveAll(generatedParticipants);
    }

    public Participant getNextParticipant() {
        if (counter == participants.size()) {
            Collections.shuffle(participants);
            counter = 0;
        }

        return participants.get(counter++);
    }
}
