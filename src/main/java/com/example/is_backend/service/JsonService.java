package com.example.is_backend.service;

import com.example.is_backend.entity.Person;
import com.example.is_backend.exception.NotFoundException;
import com.example.is_backend.exception.PersonValidationException;
import com.example.is_backend.request.MovieRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JsonService {
    private final MovieService movieService;
    private final ObjectMapper objectMapper;
    private final PersonService personService;

    public <T> List<T> parseJson(String json, Class<T> clazz) throws IOException {
        return objectMapper.readValue(json, objectMapper.getTypeFactory().constructCollectionType(List.class, clazz));
    }

    public <T> void validateEntities(List<T> entities, Class<T> clazz) throws PersonValidationException, NotFoundException {
        if (clazz == Person.class) {
            var persons = new ArrayList<Person>();
            for (T entity : entities) {
                persons.add((Person) entity);
            }
            personService.addPersons(persons);
        } else if (clazz == MovieRequest.class) {
            var movies = new ArrayList<MovieRequest>();
            for (T entity : entities) {
                movies.add((MovieRequest) entity);
            }
            movieService.addMovies(movies);
        }
    }

    public <T> void parseJsons(ArrayList<String> jsonArr, Class<T> clazz) throws IOException, PersonValidationException, NotFoundException {
        var allData = new ArrayList<T>();
        for (String json : jsonArr) {
            allData.addAll(parseJson(json, clazz));
        }
        validateEntities(allData, clazz);
    }
}
