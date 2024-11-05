package com.example.information_systems_lab1.controller;

import com.example.information_systems_lab1.exeption.InsufficientEditingRightsException;
import com.example.information_systems_lab1.exeption.MovieNotFoundException;
import com.example.information_systems_lab1.exeption.PersonNotFoundException;
import com.example.information_systems_lab1.exeption.PersonValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        //TODO logs
        return new ResponseEntity<>("Invalid input: " + ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }

    @ExceptionHandler(PersonValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Map<String, String>> handlePersonValidationException(PersonValidationException ex) {
        return ex.getExceptions();
    }

    @ExceptionHandler(InsufficientEditingRightsException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String handlePersonValidationException(InsufficientEditingRightsException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(MovieNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handlePersonValidationException(MovieNotFoundException ex) {
        return ex.getMessage();
    }
    @ExceptionHandler(PersonNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handlePersonValidationException(PersonNotFoundException ex) {
        return ex.getMessage();
    }
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handlePersonValidationException(MethodArgumentTypeMismatchException ex) {
        return ex.getMessage();
    }
}