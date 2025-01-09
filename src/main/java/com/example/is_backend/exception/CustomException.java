package com.example.is_backend.exception;

import lombok.Getter;

import java.util.Map;
@Getter
public class CustomException extends RuntimeException {
  private final Map<String, String> exceptions;

  public CustomException(Map<String, String> errors) {
    exceptions = errors;
  }
}
