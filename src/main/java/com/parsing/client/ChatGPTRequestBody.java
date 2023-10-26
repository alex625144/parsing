package com.parsing.client;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ChatGPTRequestBody {

    private String model;
    private double temperature;
    private List<Message> messages;

    public void addRuleToPattern(Pattern rule) {
        Message message = new Message(Role.SYSTEM.getValue(), rule.getPattern());

        if (lastIsUsersMessage()) {
            messages.set(messages.size() - 1, message);
        } else {
            messages.add(message);
        }
    }

    private boolean lastIsUsersMessage() {
        return messages.get(messages.size() - 1).getRole().equals(Role.USER.getValue());
    }

    public void newPattern(Pattern pattern) {
        messages = new ArrayList<>();

        messages.add(new Message(Role.SYSTEM.getValue(), pattern.getPattern()));
    }

    public void newRequest(String prompt) {
        Message message = new Message(Role.USER.getValue(), prompt);

        if (lastIsUsersMessage()) {
            messages.set(messages.size() - 1, message);
        } else {
            messages.add(message);
        }
    }

    public void addResponse(String response) {
        Message message = new Message(Role.ASSISTANT.getValue(), response);
        messages.add(message);
    }
}