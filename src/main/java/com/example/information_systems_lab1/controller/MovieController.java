package com.example.information_systems_lab1.controller;

import com.example.information_systems_lab1.entity.Movie;
import com.example.information_systems_lab1.exeption.InsufficientEditingRightsException;
import com.example.information_systems_lab1.exeption.MovieNotFoundException;
import com.example.information_systems_lab1.request.MovieRequest;
import com.example.information_systems_lab1.service.MovieService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/movie")
public class MovieController {
    private final MovieService movieService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/add")
    public void addMovie(@Valid @RequestBody MovieRequest movie) throws Exception {
        movieService.addMovie(movie);
    }

    @PatchMapping("/edit/{id}")
    public ResponseEntity<?> updateMovie(@PathVariable("id") Long id, @RequestBody MovieRequest updatedMovie) throws InsufficientEditingRightsException {
        try {
            movieService.update(id, updatedMovie);
            return ResponseEntity.ok().build();
        } catch (MovieNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
