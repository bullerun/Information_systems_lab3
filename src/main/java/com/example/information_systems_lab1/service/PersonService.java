package com.example.information_systems_lab1.service;

import com.example.information_systems_lab1.authentication.service.UserServices;
import com.example.information_systems_lab1.entity.Location;
import com.example.information_systems_lab1.entity.Person;
import com.example.information_systems_lab1.exeption.InsufficientEditingRightsException;
import com.example.information_systems_lab1.exeption.PersonNotFoundException;
import com.example.information_systems_lab1.exeption.PersonValidationException;
import com.example.information_systems_lab1.repository.PersonRepository;
import com.example.information_systems_lab1.validator.PersonValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PersonService {
    private final PersonRepository personRepository;
    private final PersonValidator personValidator;
    private final UserServices userService;
    public Person getPersonById(Long id) throws Exception {
        return personRepository.findById(id).orElseThrow(() -> new Exception("презираю жабу"));
    }

    @Transactional
    public void addPerson(Person person) {
        personRepository.save(person);
    }

    public void validateDirectionScreenwriterOperator(Person direction, Person screenwriter, Person operator) throws PersonValidationException {
        personValidator.validatePerson(direction, screenwriter, operator);
    }

    public void update(Long id, Person personRequest) throws PersonNotFoundException, InsufficientEditingRightsException {
        Person person = personRepository.findById(id).orElseThrow(() -> new PersonNotFoundException("челик не найден" + id));
        updatePerson(person, personRequest, "person");
        personRepository.save(person);
    }

    public void updatePerson(Person person, Person personRequest, String s) throws InsufficientEditingRightsException {
        if (!person.getOwnerId().equals(userService.getCurrentUserId())){
            throw new InsufficientEditingRightsException("недостаточно прав чтобы изменить" + s);
        }
        person.setName(personRequest.getName());
        person.setEyeColor(personRequest.getEyeColor());
        person.setHairColor(personRequest.getHairColor());
        if (!person.getLocation().equals(personRequest.getLocation())) {
            updateLocation(person.getLocation(), personRequest.getLocation());
        }
        person.setWeight(personRequest.getWeight());
        person.setNationality(personRequest.getNationality());
    }

    private void updateLocation(Location oldLocation, Location newLocation) {
        oldLocation.setName(newLocation.getName());
        oldLocation.setX(newLocation.getX());
        oldLocation.setY(newLocation.getY());
        oldLocation.setZ(newLocation.getZ());
    }
}
