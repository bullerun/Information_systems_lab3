package com.example.information_systems_lab1.repository;

import com.example.information_systems_lab1.entity.Person;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {


    @NotNull Optional<Person> findById(@NotNull Long id);

}
