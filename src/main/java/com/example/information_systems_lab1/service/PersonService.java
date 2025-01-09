package com.example.information_systems_lab1.service;

import com.example.information_systems_lab1.authentication.service.UserServices;
import com.example.information_systems_lab1.controller.WebSocketController;
import com.example.information_systems_lab1.dto.PersonDTO;
import com.example.information_systems_lab1.entity.Location;
import com.example.information_systems_lab1.entity.Person;
import com.example.information_systems_lab1.entity.User;
import com.example.information_systems_lab1.exception.InsufficientEditingRightsException;
import com.example.information_systems_lab1.exception.NotFoundException;
import com.example.information_systems_lab1.exception.PersistentException;
import com.example.information_systems_lab1.exception.PersonValidationException;
import com.example.information_systems_lab1.repository.PersonRepository;
import com.example.information_systems_lab1.repository.UserRepository;
import com.example.information_systems_lab1.validator.PersonValidator;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PersonService {
    private final PersonRepository personRepository;
    private final PersonValidator personValidator;
    private final UserServices userService;
    private final UserRepository userRepository;
    private final WebSocketController webSocketController;

    public Person getPersonById(Long id) throws NotFoundException {
        return personRepository.findById(id).orElseThrow(() -> new NotFoundException("презираю жабу"));
    }


    public void addPerson(Person person) {
        person.setOwnerId(userService.getCurrentUserId());
        personRepository.save(person);
    }

    public void validateDirectionScreenwriterOperator(Person direction, Person screenwriter, Person operator) throws PersonValidationException {
        personValidator.validatePerson(direction, screenwriter, operator);
    }

    public void update(Long id, Person updatedPerson) throws NotFoundException, InsufficientEditingRightsException {
        Person person = getPersonById(id);
        updatePerson(person, updatedPerson, "person");
        personRepository.save(person);
        webSocketController.updatePerson(person);

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

    public PersonDTO toPersonDTO(Person person) {
        var dto = new PersonDTO();
        dto.setId(person.getId());
        dto.setName(person.getName());
        dto.setEyeColor(person.getEyeColor());
        dto.setHairColor(person.getHairColor());
        dto.setWeight(person.getWeight());
        dto.setNationality(person.getNationality());
        dto.setLocation(person.getLocation());
        dto.setOwner_id(person.getOwnerId());
        return dto;
    }

    @Transactional
    public void deletePerson(Long id, String username) throws NotFoundException {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new NotFoundException("пользователь не найдет"));
        try {
            personRepository.deleteByIdAndOwnerIdIs(id, user.getId());
            webSocketController.deletePerson(id);
        } catch (Exception ex) {
            throw new PersistentException("Удаление невозможно: запись связана с другими объектами.");
        }

    }

}
