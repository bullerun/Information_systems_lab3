package com.example.information_systems_lab1.controller;

import com.example.information_systems_lab1.dto.MovieDTO;
import com.example.information_systems_lab1.exception.*;
import com.example.information_systems_lab1.request.MovieRequest;
import com.example.information_systems_lab1.service.MovieService;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/movie")
public class MovieController {
    private final MovieService movieService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/add")
    public void addMovie(@RequestBody MovieRequest movie) throws PersonValidationException, NotFoundException {
        movieService.addMovie(movie);
    }

    @PatchMapping("/edit/{id}")
    public ResponseEntity<?> updateMovie(@PathVariable("id") Long id, @RequestBody MovieRequest updatedMovie) throws InsufficientEditingRightsException, NotFoundException, MovieNotFoundException {
        movieService.update(id, updatedMovie);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping
    public List<MovieDTO> getAllMovies(
            @RequestParam Integer page,
            @RequestParam Integer pageSize,
            @RequestParam @Pattern(regexp = "asc|desc", message = "sortDirection должен быть 'asc' или 'desc'") String sortDirection,
            @RequestParam(required = false) @Pattern(regexp = "id|name|goldenPalmCount|genre|length|coordinates|mpaaRating|owner_id|budget|oscarsCount|director|operator", message = "sortProperty должен быть 'id','name', 'goldenPalmCount' ,'genre', 'length', 'coordinates', 'mpaaRating', 'owner_id', 'budget', 'oscarsCount', 'director', 'operator'") String sortProperty
    ) {
        sortProperty = sortProperty != null ? sortProperty : "id";
        return movieService.getAllMovies(page, pageSize, sortDirection, sortProperty);
    }

    @DeleteMapping
    public void deleteMovie(@RequestParam Long id) {
        movieService.deleteMovie(id);
    }
}
