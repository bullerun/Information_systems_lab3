package com.example.information_systems_lab1.repository;

import com.example.information_systems_lab1.entity.Movie;
import com.example.information_systems_lab1.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {
    
}
