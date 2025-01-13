package com.example.is_backend.service;

import com.example.is_backend.authentication.service.UserServices;
import com.example.is_backend.controller.WebSocketController;
import com.example.is_backend.dto.PersonDTO;
import com.example.is_backend.entity.Location;
import com.example.is_backend.entity.Person;
import com.example.is_backend.exception.InsufficientEditingRightsException;
import com.example.is_backend.exception.NotFoundException;
import com.example.is_backend.exception.PersistentException;
import com.example.is_backend.exception.PersonValidationException;
import com.example.is_backend.repository.PersonRepository;
import com.example.is_backend.validator.PersonValidator;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class PersonService {
    private final PersonRepository personRepository;
    private final PersonValidator personValidator;
    private final UserServices userService;
    private final WebSocketController webSocketController;
    private final ConcurrentHashMap<Integer, Object> inMemoryCache = new ConcurrentHashMap<>();
    private static final Object DUMMY_VALUE = new Object();

    public Person getPersonById(Long id) throws NotFoundException {
        return personRepository.findById(id).orElseThrow(() -> new NotFoundException("Человек с id " + id + " не найден."));
    }

    private Integer generatePersonKey(Person person) {
        return Objects.hash(person.getName(), person.getEyeColor(), person.getHairColor(), person.getWeight(), person.getNationality());
    }

    @PostConstruct
    private void loadMoviesToCache() {
        personRepository.findAll().forEach(person -> inMemoryCache.put(generatePersonKey(person), DUMMY_VALUE));
    }


    @Transactional
    public Person addPerson(Person person) throws InsufficientEditingRightsException {
        int hash = generatePersonKey(person);


        if (inMemoryCache.containsKey(hash)) {
            throw new InsufficientEditingRightsException("такой человек уже создан");
        }
        inMemoryCache.put(hash, DUMMY_VALUE);

        try {
            // Устанавливаем владельца и сохраняем объект в репозитории.
            person.setOwnerId(userService.getCurrentUserId());
            return personRepository.save(person);
        } catch (Exception e) {
            // Если произошла ошибка, удаляем кеш.

            inMemoryCache.remove(hash);

            throw e;
        }
    }

    private boolean isPersonUnique(Person person) {
        return inMemoryCache.containsKey(generatePersonKey(person));
    }

    public boolean validatePerson(Person person) {
        personValidator.validatePerson(person);
        return true;
    }

    @Transactional
    public void update(Long id, Person updatedPerson) throws NotFoundException, InsufficientEditingRightsException {

        var newHash = generatePersonKey(updatedPerson);
        if (inMemoryCache.containsKey(generatePersonKey(updatedPerson))) {
            throw new PersistentException("Такой person уже существует");
        }


        Person person = getPersonById(id);
        var oldHash = generatePersonKey(person);
        updatePersonFields(person, updatedPerson, "person");


        // помечаю новый кэш как занятый, без удаления старого, чтобы не было момента когда update не прошел а старый кэш удален
        inMemoryCache.put(newHash, DUMMY_VALUE);


        try {
            personRepository.save(person);
        } catch (Exception e) {
            inMemoryCache.remove(newHash);
            throw e;
        }
        inMemoryCache.remove(oldHash);
        webSocketController.updatePerson(person);
    }

    protected void updatePersonFields(Person person, Person updatedPerson, String personName) throws InsufficientEditingRightsException {
        if (!person.getOwnerId().equals(userService.getCurrentUserId())) {
            throw new InsufficientEditingRightsException("Недостаточно прав для изменения данных." + personName);
        }
        person.setName(updatedPerson.getName());
        person.setEyeColor(updatedPerson.getEyeColor());
        person.setHairColor(updatedPerson.getHairColor());
        person.setWeight(updatedPerson.getWeight());
        person.setNationality(updatedPerson.getNationality());
        updateLocation(person.getLocation(), updatedPerson.getLocation());
    }

    private void updateLocation(Location oldLocation, Location newLocation) {
        oldLocation.setName(newLocation.getName());
        oldLocation.setX(newLocation.getX());
        oldLocation.setY(newLocation.getY());
        oldLocation.setZ(newLocation.getZ());
    }

    public List<PersonDTO> getAllPersons(Integer page, Integer pageSize, String sortDirection, String sortProperty) {
        Page<Person> persons = personRepository.findAll(PageRequest.of(page, pageSize, Sort.by(Sort.Direction.fromString(sortDirection), sortProperty)));
        return persons.stream().map(this::toPersonDTO).collect(Collectors.toList());
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
    public void deletePerson(Long id) {
        Long ownerId = userService.getCurrentUserId();
        Person person = personRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Person не найден"));

        if (!person.getOwnerId().equals(ownerId)) {
            throw new PersistentException("Person принадлежит не вам");
        }

        personRepository.deleteByIdAndOwnerIdIs(id, ownerId);

        inMemoryCache.remove(generatePersonKey(person));

        webSocketController.deletePerson(id);
    }

    @Transactional
    public void addPersons(List<Person> persons) {
        Long ownerId = userService.getCurrentUserId();
        List<Person> newUniqPerson = new ArrayList<>();
        for (Person person : persons) {
            if (validatePerson(person) && !isPersonUnique(person)) {
                person.setOwnerId(ownerId);
                newUniqPerson.add(person);
                inMemoryCache.put(generatePersonKey(person), DUMMY_VALUE);
            }
        }

        try {
            personRepository.saveAll(newUniqPerson);
        } catch (Exception e) {
            newUniqPerson.forEach(person -> inMemoryCache.remove(generatePersonKey(person)));
        }
    }

    public void validateDirectionScreenwriterOperator(Person direction, Person screenwriter, Person operator) throws PersonValidationException {
        personValidator.validateDirectorScreenwriterOperator(direction, screenwriter, operator);
    }
}
