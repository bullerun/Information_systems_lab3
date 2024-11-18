package com.example.information_systems_lab1.service;

import com.example.information_systems_lab1.authentication.service.UserServices;
import com.example.information_systems_lab1.dto.PersonDTO;
import com.example.information_systems_lab1.entity.Location;
import com.example.information_systems_lab1.entity.Person;
import com.example.information_systems_lab1.exception.InsufficientEditingRightsException;
import com.example.information_systems_lab1.exception.PersonNotFoundException;
import com.example.information_systems_lab1.exception.PersonValidationException;
import com.example.information_systems_lab1.repository.PersonRepository;
import com.example.information_systems_lab1.validator.PersonValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PersonService {
    private final PersonRepository personRepository;
    private final PersonValidator personValidator;
    private final UserServices userService;

    public Person getPersonById(Long id) throws PersonNotFoundException {
        return personRepository.findById(id).orElseThrow(() -> new PersonNotFoundException("презираю жабу"));
    }

    @Transactional
    public void addPerson(Person person) {
        person.setOwnerId(userService.getCurrentUserId());
        personRepository.save(person);
    }

    public void validateDirectionScreenwriterOperator(Person direction, Person screenwriter, Person operator) throws PersonValidationException {
        personValidator.validatePerson(direction, screenwriter, operator);
    }

    public void update(Long id, Person personRequest) throws PersonNotFoundException, InsufficientEditingRightsException {
        Person person = getPersonById(id);
        updatePerson(person, personRequest, "person");
        personRepository.save(person);
    }

    public void updatePerson(Person person, Person personRequest, String s) throws InsufficientEditingRightsException {
        if (person == null && personRequest == null) {
            return;
        }
        if (!person.getOwnerId().equals(userService.getCurrentUserId())) {
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

    public List<PersonDTO> getAllPersons(Integer page, Integer pageSize, String sortDirection, String sortProperty) {
        Page<Person> a = personRepository.findAll(PageRequest.of(page, pageSize, Sort.by(Sort.Direction.fromString(sortDirection), sortProperty)));
        return toPersonDTO(a.getContent());
    }


    private List<PersonDTO> toPersonDTO(List<Person> persons) {
        List<PersonDTO> dtos = new ArrayList<>();
        for (Person person : persons) {
            dtos.add(toPersonDTO(person));
        }
        return dtos;
    }

    private PersonDTO toPersonDTO(Person person) {
        var dto = new PersonDTO();
        dto.setId(person.getId());
        dto.setName(person.getName());
        dto.setEyeColor(person.getEyeColor());
        dto.setHairColor(person.getHairColor());
        dto.setWeight(person.getWeight());
        dto.setNationality(person.getNationality());
        dto.setLocation(person.getLocation());
        dto.setOwner_id(person.getOwner().getId());
        return dto;
    }
}
