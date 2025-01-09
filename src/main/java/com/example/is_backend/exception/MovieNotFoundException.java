package com.example.is_backend.exception;

public class MovieNotFoundException extends Exception{
    public MovieNotFoundException(String s) {
        super(s);
    }
}
