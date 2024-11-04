package com.example.information_systems_lab1.service;

import com.example.information_systems_lab1.entity.Person;
import com.example.information_systems_lab1.repository.LocationRepository;
import com.example.information_systems_lab1.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PersonService {
    private final PersonRepository personRepository;
    private final LocationRepository locationRepository;

    @Transactional
    public void addPerson(Person person)  {
            personRepository.save(person);
    }
}
