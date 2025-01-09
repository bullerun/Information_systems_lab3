package com.example.information_systems_lab1.controller;

import com.example.information_systems_lab1.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
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
        return new ResponseEntity<>("Неверные данные: " + ex.getMessage(), HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(PersistentException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Map<String, String> handlePersistentException(PersistentException ex) {
        Map<String, String> m = new HashMap<>();
        m.put("error", "А он занять НЕ ТРОГАТЬ");
        return m;
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
    public Map<String, String> handlePersonValidationException(InsufficientEditingRightsException ex) {
        Map<String, String> m = new HashMap<>();
        m.put("error", ex.getMessage());
        return m;
    }

    @ExceptionHandler(MovieNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handlePersonValidationException(MovieNotFoundException ex) {
        Map<String, String> m = new HashMap<>();
        m.put("error", ex.getMessage());
        return m;
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handlePersonValidationException(NotFoundException ex) {
        Map<String, String> m = new HashMap<>();
        m.put("error", ex.getMessage());
        return m;
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handlePersonValidationException(MethodArgumentTypeMismatchException ex) {
        Map<String, String> m = new HashMap<>();
        m.put("error", ex.getMessage());
        return m;
    }

    @ExceptionHandler({IllegalArgumentException.class, MissingServletRequestParameterException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> badRequest(Exception ex) {
        Map<String, String> m = new HashMap<>();
        m.put("error", ex.getMessage());
        return m;
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String internalServerError(Exception ex) {
        return "Непредвиденная ошибка";
    }
}