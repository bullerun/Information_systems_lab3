package com.example.information_systems_lab1.exeption;
//TODO сделать перехватчик
public class InsufficientEditingRightsException extends Exception {
    public InsufficientEditingRightsException(String s) {
        super(s);
    }
}
