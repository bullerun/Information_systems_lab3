package com.example.information_systems_lab1.controller;

import com.example.information_systems_lab1.entity.Movie;
import com.example.information_systems_lab1.request.MovieRequest;
import com.example.information_systems_lab1.service.MovieService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/movie")
public class MovieController {
    private final MovieService movieService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/add")
    public void addMovie(@Valid @RequestBody MovieRequest movie)  {
        try {
            movieService.addMovie(movie);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
