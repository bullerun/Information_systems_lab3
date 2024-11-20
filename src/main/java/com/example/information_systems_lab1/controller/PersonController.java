package com.example.information_systems_lab1.controller;

import com.example.information_systems_lab1.dto.PersonDTO;
import com.example.information_systems_lab1.entity.Person;
import com.example.information_systems_lab1.exception.InsufficientEditingRightsException;
import com.example.information_systems_lab1.exception.NotFoundException;
import com.example.information_systems_lab1.service.PersonService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;


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

    @PatchMapping("/edit")
    public ResponseEntity<?> updatePerson(@RequestParam Long id, @Valid @RequestBody Person updatedPerson) throws InsufficientEditingRightsException {
        try {
            personService.update(id, updatedPerson);
            return ResponseEntity.ok().build();
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public List<PersonDTO> getAllPersons(
            @RequestParam Integer page,
            @RequestParam Integer pageSize,
            @RequestParam @Pattern(regexp = "asc|desc", message = "sortDirection должен быть 'asc' или 'desc'") String sortDirection,
            @RequestParam(required = false) @Pattern(regexp = "id|name|eyeColor|hairColor|location|weight|nationality|owner_id", message = "sortProperty должен быть 'id', 'name', 'eyeColor', 'hairColor', 'location', 'weight', 'nationality', 'owner_id'") String sortProperty
    ) {
        sortProperty = sortProperty != null ? sortProperty : "id";
        return personService.getAllPersons(page, pageSize, sortDirection, sortProperty);
    }

    @DeleteMapping("/delete")
    public void deletePerson(@RequestParam Long id, Principal principal) throws NotFoundException {
        personService.deletePerson(id, principal.getName());
    }
}
