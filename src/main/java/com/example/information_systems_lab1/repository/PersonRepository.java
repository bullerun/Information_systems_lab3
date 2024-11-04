package com.example.information_systems_lab1.repository;

import com.example.information_systems_lab1.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {
    Person findByName(String name);


    Optional<Person> findById(Long id);
}
