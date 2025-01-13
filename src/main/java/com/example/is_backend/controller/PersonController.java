package com.example.is_backend.controller;

import com.example.is_backend.authentication.service.UserServices;
import com.example.is_backend.dto.FileHistoryDTO;
import com.example.is_backend.dto.PersonDTO;
import com.example.is_backend.entity.FileHistory;
import com.example.is_backend.entity.Person;
import com.example.is_backend.exception.InsufficientEditingRightsException;
import com.example.is_backend.exception.NotFoundException;
import com.example.is_backend.exception.PersistentException;
import com.example.is_backend.exception.PersonValidationException;
import com.example.is_backend.repository.FileHistoryRepository;
import com.example.is_backend.repository.UserRepository;
import com.example.is_backend.service.FileProcessingService;
import com.example.is_backend.service.PersonService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@RestController
@RequiredArgsConstructor
@RequestMapping("/person")
public class PersonController {
    private final PersonService personService;
    private final FileProcessingService fileProcessingService;
    private final FileHistoryRepository fileHistoryRepository;
    private final UserRepository userRepository;
    private final UserServices userServices;

    @PostMapping("/add")
    @ResponseStatus(HttpStatus.CREATED)
    public void createPerson(@Valid @RequestBody Person person) throws InsufficientEditingRightsException {
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
    public void deletePerson(@RequestParam Long id) {
        try {
            personService.deletePerson(id);
        } catch (Exception ex) {
            throw new PersistentException("Удаление невозможно: запись связана с другими объектами.");
        }
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFiles(MultipartFile file) throws PersonValidationException, NotFoundException, IOException {

        String contentType = file.getContentType();
        if (contentType != null && !contentType.equals("application/json") && !contentType.equals("application/x-zip-compressed")) {
            return ResponseEntity.badRequest().body("Only JSON and ZIP files are allowed.");
        }
        fileProcessingService.processFile(file, Person.class);
        return ResponseEntity.ok().body(Map.of("message", "File uploaded successfully!"));
    }
    @GetMapping("/fileHistory")
    public List<FileHistoryDTO> getHistory() {
        List<FileHistoryDTO> fileHistoryListDTO = new ArrayList<>();
        List<FileHistory> file = fileHistoryRepository.getByOwnerId(userServices.getCurrentUserId());
        for (FileHistory fileHistory : file) {
           fileHistoryListDTO.add(fileHistoryTODTO(fileHistory));
        }
        return fileHistoryListDTO;
    }

    private FileHistoryDTO fileHistoryTODTO(FileHistory fileHistory) {
        FileHistoryDTO fileHistoryDTO = new FileHistoryDTO();
        fileHistoryDTO.setFileName(fileHistory.getFileName());
        fileHistoryDTO.setOwnerId(fileHistory.getOwnerId());
        fileHistoryDTO.setStatus(fileHistory.getStatus());
        return fileHistoryDTO;
    }
//    public List<FileHistoryDTO> getHistoryAdmin() {
//        return fileHistoryRepository.findAll();
//    }
}
