package com.example.is_backend.controller;

import com.example.is_backend.entity.Person;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@RequiredArgsConstructor
@Controller
public class WebSocketController {
    private final SimpMessagingTemplate messagingTemplate;


    public void updatePerson(Person person) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String jsonString = mapper.writeValueAsString(person);
            messagingTemplate.convertAndSend("/topic/person", jsonString);
        } catch (JsonProcessingException e) {
            System.out.println("I'm not suicidal");
        }
    }

    public void deletePerson(Long id) {
        messagingTemplate.convertAndSend("/topic/person", "{id=" + id.intValue() + "}");
    }
}
