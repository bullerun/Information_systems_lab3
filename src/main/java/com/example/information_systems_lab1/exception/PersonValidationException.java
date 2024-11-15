package com.example.information_systems_lab1.exception;

import lombok.Getter;

import java.util.Map;

@Getter
public class PersonValidationException extends Exception {
    private final Map<String, Map<String, String>> exceptions;

    public PersonValidationException(Map<String, Map<String, String>> errors) {
        exceptions = errors;
    }
}
