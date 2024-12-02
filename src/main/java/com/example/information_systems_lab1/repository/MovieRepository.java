package com.example.information_systems_lab1.repository;

import com.example.information_systems_lab1.entity.Movie;
import com.example.information_systems_lab1.entity.MovieGenre;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {
    @NotNull
    Page<Movie> findAll(@NotNull Pageable pageable);



    @Query("SELECT m FROM Movie m WHERE m.genre = :genre and m.director.id = :directorID")
    List<Movie> findMoviesByGenre(@Param("genre") MovieGenre genre, @Param("directorID") Long directorID);
}
