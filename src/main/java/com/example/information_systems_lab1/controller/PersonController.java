package com.example.information_systems_lab1.controller;

import com.example.information_systems_lab1.entity.Person;
import com.example.information_systems_lab1.exeption.PersonNotFoundException;
import com.example.information_systems_lab1.service.PersonService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/person")
public class PersonController {
    private final PersonService personService;

    @PostMapping("/add")
    @ResponseStatus(HttpStatus.CREATED)
    public void createOrUpdateEmployee(@Valid @RequestBody Person person) {
        personService.addPerson(person);
    }


    @PatchMapping("/edit/{id}")
    public ResponseEntity<?> updateMovie(@PathVariable("id") Long id, @RequestBody Person updatedMovie) {
        try {
            personService.update(id, updatedMovie);
            return ResponseEntity.ok().build();
        } catch (PersonNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
