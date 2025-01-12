package com.example.is_backend.service;

import com.example.is_backend.authentication.service.UserServices;
import com.example.is_backend.controller.WebSocketController;
import com.example.is_backend.dto.PersonDTO;
import com.example.is_backend.entity.Location;
import com.example.is_backend.entity.Person;
import com.example.is_backend.exception.*;
import com.example.is_backend.repository.PersonRepository;
import com.example.is_backend.validator.PersonValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class PersonService {
    private final PersonRepository personRepository;
    private final PersonValidator personValidator;
    private final UserServices userService;
    private final WebSocketController webSocketController;
    private final ConcurrentHashMap<Integer, Integer> inMemoryCache = new ConcurrentHashMap<>();
    private final ReentrantReadWriteLock cacheLock = new ReentrantReadWriteLock();

    public Person getPersonById(Long id) throws NotFoundException {
        return personRepository.findById(id).orElseThrow(() -> new NotFoundException("Человек с id " + id + " не найден."));
    }

    @Transactional
    public void addPerson(Person person) throws InsufficientEditingRightsException {
        int hash = person.hashCodeForInMemoryCache();

        cacheLock.writeLock().lock();
        try {
            if (inMemoryCache.containsKey(hash)) {
                throw new InsufficientEditingRightsException("такой человек уже создан");
            }
            inMemoryCache.put(hash, 0);
        } finally {
            cacheLock.writeLock().unlock();
        }

        try {
            // Устанавливаем владельца и сохраняем объект в репозитории.
            person.setOwnerId(userService.getCurrentUserId());
            personRepository.save(person);
        } catch (Exception e) {
            // Если произошла ошибка, удаляем кеш.
            cacheLock.writeLock().lock();
            try {
                inMemoryCache.remove(hash);
            } finally {
                cacheLock.writeLock().unlock();
            }
            throw e;
        }
    }

    private boolean isPersonUnique(Person person) {
        int hash = person.hashCodeForInMemoryCache();
        cacheLock.readLock().lock();
        try {
            return inMemoryCache.containsKey(hash);
        } finally {
            cacheLock.readLock().unlock();
        }
    }

    public boolean validatePerson(Person person) {
        if (!isPersonUnique(person)) {
            return false;
        }
        personValidator.validatePerson(person);
        return true;
    }

    @Transactional
    public void update(Long id, Person updatedPerson) throws NotFoundException, InsufficientEditingRightsException {
        cacheLock.readLock().lock();
        if (inMemoryCache.containsKey(updatedPerson.hashCodeForInMemoryCache())) {
            throw new PersistentException("Такой person уже существует");
        }
        cacheLock.readLock().unlock();

        Person person = getPersonById(id);
        int oldHash = person.hashCodeForInMemoryCache();
        updatePersonFields(person, updatedPerson, "person");

        cacheLock.writeLock().lock();
        inMemoryCache.remove(oldHash);
        inMemoryCache.put(person.hashCodeForInMemoryCache(), 0);
        cacheLock.writeLock().unlock();

        try {
            personRepository.save(person);
        }catch (Exception e) {
            cacheLock.writeLock().lock();
            inMemoryCache.remove(person.hashCodeForInMemoryCache());
            inMemoryCache.put(oldHash, 0);
            cacheLock.writeLock().unlock();
            throw e;
        }
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

        cacheLock.writeLock().lock();
        inMemoryCache.remove(person.hashCodeForInMemoryCache());
        cacheLock.writeLock().unlock();

        webSocketController.deletePerson(id);
    }

    @Transactional
    public void addPersons(List<Person> persons) {
        Long ownerId = userService.getCurrentUserId();
        List<Person> newUniqPerson = new ArrayList<>();
        for (Person person : persons) {
            if (validatePerson(person)) {
                person.setOwnerId(ownerId);
                newUniqPerson.add(person);
            }
        }


        cacheLock.writeLock().lock();
        newUniqPerson.forEach(person -> inMemoryCache.put(person.hashCodeForInMemoryCache(), 0));
        cacheLock.writeLock().unlock();


        try {
            personRepository.saveAll(newUniqPerson);
        } catch (Exception e) {
            cacheLock.writeLock().lock();
            newUniqPerson.forEach(person -> inMemoryCache.remove(person.hashCodeForInMemoryCache()));
            cacheLock.writeLock().unlock();

        }

    }

    public void validateDirectionScreenwriterOperator(Person direction, Person screenwriter, Person operator) throws PersonValidationException {
        personValidator.validateDirectorScreenwriterOperator(direction, screenwriter, operator);
    }
}
