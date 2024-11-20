package com.example.information_systems_lab1.repository;

import com.example.information_systems_lab1.entity.Movie;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {
    @NotNull
    Page<Movie> findAll(@NotNull Pageable pageable);

}
