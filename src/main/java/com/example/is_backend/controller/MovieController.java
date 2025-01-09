package com.example.is_backend.controller;

import com.example.is_backend.dto.MovieDTO;
import com.example.is_backend.entity.Movie;
import com.example.is_backend.exception.*;
import com.example.is_backend.request.MovieRequest;
import com.example.is_backend.service.FileProcessingService;
import com.example.is_backend.service.MovieService;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/movie")
public class MovieController {
    private final MovieService movieService;
    private final SimpMessagingTemplate messagingTemplate;
    private final FileProcessingService fileProcessingService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/add")
    public void addMovie(@RequestBody MovieRequest movie) throws PersonValidationException, NotFoundException {
        movieService.addMovie(movie);
    }

    @PatchMapping("/edit/{id}")
    public ResponseEntity<?> updateMovie(@PathVariable("id") Long id, @RequestBody MovieRequest updatedMovie) throws InsufficientEditingRightsException, NotFoundException, MovieNotFoundException {
        movieService.update(id, updatedMovie);
        messagingTemplate.convertAndSend("/topic/movie", Map.of("action", "update", "value", updatedMovie));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping
    public List<MovieDTO> getAllMovies(@RequestParam Integer page, @RequestParam Integer pageSize, @RequestParam @Pattern(regexp = "asc|desc", message = "sortDirection должен быть 'asc' или 'desc'") String sortDirection, @RequestParam(required = false) @Pattern(regexp = "id|name|goldenPalmCount|genre|length|coordinates|mpaaRating|owner_id|budget|oscarsCount|director|operator", message = "sortProperty должен быть 'id','name', 'goldenPalmCount' ,'genre', 'length', 'coordinates', 'mpaaRating', 'owner_id', 'budget', 'oscarsCount', 'director', 'operator'") String sortProperty) {
        sortProperty = sortProperty != null ? sortProperty : "id";
        return movieService.getAllMovies(page, pageSize, sortDirection, sortProperty);
    }

    @DeleteMapping
    public void deleteMovie(@RequestParam Long id) {
        movieService.deleteMovie(id);
        messagingTemplate.convertAndSend("/topic/movie", Map.of("action", "deleted", "value", id));
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFiles(@RequestParam("file") MultipartFile file) {
        try {
            String contentType = file.getContentType();
            if (contentType != null && !contentType.equals("application/json") && !contentType.equals("application/zip")) {
                return ResponseEntity.badRequest().body("Only JSON and ZIP files are allowed.");
            }
            fileProcessingService.processFile(file, Movie.class);
            return ResponseEntity.ok("File uploaded successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing the file: " + e.getMessage());
        }
    }
}
