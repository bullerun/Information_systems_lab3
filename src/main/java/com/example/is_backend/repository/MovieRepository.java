package com.example.is_backend.repository;

import com.example.is_backend.entity.Movie;
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
