package com.example.information_systems_lab1.service;

import com.example.information_systems_lab1.entity.Person;
import com.example.information_systems_lab1.exeption.PersonValidationException;
import com.example.information_systems_lab1.repository.PersonRepository;
import com.example.information_systems_lab1.validator.PersonValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PersonService {
    private final PersonRepository personRepository;
    private final PersonValidator personValidator;

    public Person getPersonById(Long id) throws Exception{
        return personRepository.findById(id)
                .orElseThrow(() -> new Exception("презираю жабу"));
    }

    @Transactional
    public void addPerson(Person person) {
        personRepository.save(person);
    }

    public void validateDirectionScreenwriterOperator(Person direction, Person screenwriter, Person operator) throws PersonValidationException {
        personValidator.validatePerson(direction, screenwriter, operator);
    }
}
