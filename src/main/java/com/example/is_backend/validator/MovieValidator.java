package com.example.is_backend.validator;

import com.example.is_backend.exception.CustomException;
import com.example.is_backend.request.MovieRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Set;


@Component
@RequiredArgsConstructor
public class MovieValidator {
    private final Validator validator;

    public void validateMovie(MovieRequest movie) {
        Set<ConstraintViolation<MovieRequest>> violations = validator.validate(movie);
        var errors = new HashMap<String, String>();
        if (!violations.isEmpty()) {
            for (ConstraintViolation<MovieRequest> violation : violations) {
                String fieldName = violation.getPropertyPath().toString();
                String errorMessage = violation.getMessage();
                errors.put(fieldName, errorMessage);
            }
        }
        if (!errors.isEmpty()) {
            throw new CustomException(errors);
        }
    }
}
