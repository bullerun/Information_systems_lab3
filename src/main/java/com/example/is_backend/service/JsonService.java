package com.example.is_backend.service;

import com.example.is_backend.entity.Person;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JsonService {
    private final PersonService personService;
    private final ObjectMapper objectMapper;

    public <T> List<T> parseJson(String json, Class<T> clazz) throws IOException {
        return objectMapper.readValue(json, objectMapper.getTypeFactory().constructCollectionType(List.class, clazz));
    }

    public <T> void validateEntities(List<T> entities, Class<T> clazz) {
        var persons = new ArrayList<Person>();
        if (clazz == Person.class) {
            for (T entity : entities) {
                if (personService.validatePerson((Person) entity)){
                    persons.add((Person) entity);
                }
            }
        }
        personService.addPersons(persons);
    }

    public <T> void parseJsons(ArrayList<String> jsonArr, Class<T> clazz) throws IOException {
        var allData = new ArrayList<T>();
        for (String json : jsonArr) {
            allData.addAll(parseJson(json, clazz));
        }
        validateEntities(allData, clazz);
    }
}
