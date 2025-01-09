package com.example.is_backend.validator;

import com.example.is_backend.entity.Person;
import com.example.is_backend.exception.PersonValidationException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor
@Component
public class PersonValidator {
    private final Validator validator;


    public void validatePerson(Person direction, Person screenwriter, Person operator) throws PersonValidationException {
        Map<String, Map<String, String>> errors = new HashMap<>();
        checkAndAddErrors(direction, "direction", errors);
        if (screenwriter != null) {
            checkAndAddErrors(screenwriter, "screenwriter", errors);
        }
        checkAndAddErrors(operator, "operator", errors);
        if (!errors.isEmpty()) throw new PersonValidationException(errors);
    }

    public void checkAndAddErrors(Person person, String str, Map<String, Map<String, String>> errors) {
        Set<ConstraintViolation<Person>> violations = validator.validate(person);
        for (ConstraintViolation<Person> violation : violations) {
            String fieldName = violation.getPropertyPath().toString();
            String errorMessage = violation.getMessage();
            if (!errors.containsKey(str)) {
                errors.put(str, new HashMap<>());
            }
            errors.get(str).put(fieldName, errorMessage);
        }
    }
}