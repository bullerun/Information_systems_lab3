package com.example.information_systems_lab1.controller;

import com.example.information_systems_lab1.entity.Person;
import com.example.information_systems_lab1.service.PersonService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/person")
public class PersonController {
    private final PersonService personService;

    @PostMapping("/add")
    @ResponseStatus(HttpStatus.CREATED)
    public void add(@Valid @RequestBody  Person person) {
        personService.addPerson(person);
    }
}
