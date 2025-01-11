package com.example.is_backend.controller;

import com.example.is_backend.dto.PersonDTO;
import com.example.is_backend.entity.Movie;
import com.example.is_backend.entity.Person;
import com.example.is_backend.exception.InsufficientEditingRightsException;
import com.example.is_backend.exception.NotFoundException;
import com.example.is_backend.exception.PersistentException;
import com.example.is_backend.service.FileProcessingService;
import com.example.is_backend.service.PersonService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;
import java.util.Map;


@RestController
@RequiredArgsConstructor
@RequestMapping("/person")
public class PersonController {
    private final PersonService personService;
    private final FileProcessingService fileProcessingService;

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
    public List<PersonDTO> getAllPersons(@RequestParam Integer page, @RequestParam Integer pageSize, @RequestParam @Pattern(regexp = "asc|desc", message = "sortDirection должен быть 'asc' или 'desc'") String sortDirection, @RequestParam(required = false) @Pattern(regexp = "id|name|eyeColor|hairColor|location|weight|nationality|owner_id", message = "sortProperty должен быть 'id', 'name', 'eyeColor', 'hairColor', 'location', 'weight', 'nationality', 'owner_id'") String sortProperty) {
        sortProperty = sortProperty != null ? sortProperty : "id";
        return personService.getAllPersons(page, pageSize, sortDirection, sortProperty);
    }

    @DeleteMapping("/delete")
    public void deletePerson(@RequestParam Long id, Principal principal) {
        try {
            personService.deletePerson(id, principal.getName());
        } catch (Exception ex) {
            throw new PersistentException("Удаление невозможно: запись связана с другими объектами.");
        }
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFiles(MultipartFile file) {
        try {
            String contentType = file.getContentType();
            if (contentType != null && !contentType.equals("application/json") && !contentType.equals("application/x-zip-compressed")) {
                return ResponseEntity.badRequest().body("Only JSON and ZIP files are allowed.");
            }
            fileProcessingService.processFile(file, Person.class);
            return ResponseEntity.ok().body(Map.of("message", "File uploaded successfully!"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing the file: " + e.getMessage());
        }
    }
}
