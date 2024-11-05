package com.example.information_systems_lab1.validator;

import com.example.information_systems_lab1.entity.Person;
import com.example.information_systems_lab1.exeption.PersonValidationException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class PersonValidator {

    private final Validator validator;

    public PersonValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    public void validatePerson(Person direction, Person screenwriter, Person operator) throws PersonValidationException {
        Map<String, Map<String, String>> errors = new HashMap<>();
        checkAndAddErrors(direction, "direction", errors);
        if (screenwriter != null){checkAndAddErrors(screenwriter, "screenwriter", errors);}
        checkAndAddErrors(operator, "operator", errors);
        if (!errors.isEmpty()) throw new PersonValidationException(errors);
    }
    public void checkAndAddErrors(Person person, String str, Map<String, Map<String, String>> errors){
        Set<ConstraintViolation<Person>> violations = validator.validate(person);
        for (ConstraintViolation<Person> violation : violations) {
            String fieldName = violation.getPropertyPath().toString();
            String errorMessage = violation.getMessage();
            if (!errors.containsKey(str)){
                errors.put(str, new HashMap<>());
            }
            errors.get(str).put(fieldName, errorMessage);
        }
    }
}