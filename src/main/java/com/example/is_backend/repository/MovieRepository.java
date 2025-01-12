package com.example.is_backend.repository;

import com.example.is_backend.entity.Movie;
import com.example.is_backend.entity.MovieGenre;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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


    @Modifying
    @Query("DELETE Movie m WHERE  m.id = :movieID and m.ownerId = :owner")
    void deleteById(@NotNull Long movieID, @NotNull Long owner);

    boolean existsByNameAndLength(@jakarta.validation.constraints.NotNull(message = "Поле name не может быть null") @Size(min = 1, message = "Строка не может быть пустой") String name, @jakarta.validation.constraints.NotNull(message = "Поле length не может быть null") @Min(value = 1, message = "Значение поля length должно быть больше 0") Integer length);

    Boolean findByNameAndLength(@jakarta.validation.constraints.NotNull(message = "Поле name не может быть null") @Size(min = 1, message = "Строка не может быть пустой") String name, @jakarta.validation.constraints.NotNull(message = "Поле length не может быть null") @Min(value = 1, message = "Значение поля length должно быть больше 0") Integer length);
}
