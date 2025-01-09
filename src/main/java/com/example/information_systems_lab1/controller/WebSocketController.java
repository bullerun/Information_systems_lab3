package com.example.information_systems_lab1.controller;

import com.example.information_systems_lab1.entity.Person;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.Map;

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
