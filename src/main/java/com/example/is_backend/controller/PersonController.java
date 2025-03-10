package com.example.is_backend.controller;

import com.example.is_backend.authentication.service.UserServices;
import com.example.is_backend.dto.FileHistoryDTO;
import com.example.is_backend.dto.PersonDTO;
import com.example.is_backend.dto.TransactionRequest;
import com.example.is_backend.entity.FileEnum;
import com.example.is_backend.entity.FileHistory;
import com.example.is_backend.entity.Person;
import com.example.is_backend.exception.InsufficientEditingRightsException;
import com.example.is_backend.exception.NotFoundException;
import com.example.is_backend.exception.PersistentException;
import com.example.is_backend.exception.PersonValidationException;
import com.example.is_backend.repository.FileHistoryRepository;
import com.example.is_backend.service.FileHistoryService;
import com.example.is_backend.service.FileProcessingService;
import com.example.is_backend.service.MinioService;
import com.example.is_backend.service.PersonService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/person")
public class PersonController {
    private final PersonService personService;
    private final FileProcessingService fileProcessingService;
    private final FileHistoryRepository fileHistoryRepository;
    private final UserServices userServices;
    private final MinioService minioService;
    private final FileHistoryService fileHistoryService;

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
    public ResponseEntity<?> uploadFiles(MultipartFile file) throws PersonValidationException, NotFoundException, IllegalArgumentException, IOException, InterruptedException {

        String contentType = file.getContentType();
        System.out.println(contentType);
        if (contentType != null && !contentType.equals("application/x-zip-compressed")) {
            return ResponseEntity.badRequest().body("Only ZIP files are allowed.");
        }
        String fileNameInMinio = file.getOriginalFilename() + "_" + UUID.randomUUID();
        FileHistory fileHistory = new FileHistory();
        fileHistory.setFileNameInMinio(fileNameInMinio);
        fileHistory.setFileName(file.getOriginalFilename());
        fileHistory.setOwnerId(userServices.getCurrentUserId());
        fileHistory.setStatus(FileEnum.PROCESSING);
        try {
            fileHistory = fileHistoryService.save(fileHistory);
            fileProcessingService.processFile(file, Person.class, fileNameInMinio, fileHistory);
            fileProcessingService.saveHash(file);
            fileHistoryService.updateStatus(fileHistory, FileEnum.COMPLETED);

        } catch (Exception e) {
            if (fileHistory.getStatus() != FileEnum.DUPLICATE) {
                fileHistoryService.updateStatus(fileHistory, FileEnum.ERROR);
            }
            throw e;
        }
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

    @GetMapping("file/{id}")
    public ResponseEntity<InputStreamResource> downloadFile(@PathVariable Long id) throws  NotFoundException {
        FileHistory fileName = fileHistoryRepository.findById(id).orElseThrow(() -> new NotFoundException("файл не найден"));
        try {
            InputStream inputStream = minioService.getFile(fileName.getFileNameInMinio());
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM).header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"").body(new InputStreamResource(inputStream));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/transaction/{id}")
    public ResponseEntity<Map<String, String>> handleTransaction(@PathVariable Long id,
                                                    @RequestBody TransactionRequest request) throws Exception {
        fileProcessingService.processUncommitedTransaction(id, Person.class, request.getDecision());
        return ResponseEntity.ok(Collections.singletonMap("123", "Transaction processed successfully"));
    }

    private FileHistoryDTO fileHistoryTODTO(FileHistory fileHistory) {
        FileHistoryDTO fileHistoryDTO = new FileHistoryDTO();
        fileHistoryDTO.setId(fileHistory.getId());
        fileHistoryDTO.setFileName(fileHistory.getFileName());
        fileHistoryDTO.setOwnerId(fileHistory.getOwnerId());
        fileHistoryDTO.setStatus(fileHistory.getStatus());
        return fileHistoryDTO;
    }
//    public List<FileHistoryDTO> getHistoryAdmin() {
//        return fileHistoryRepository.findAll();
//    }
}
