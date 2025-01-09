package com.example.is_backend.repository;

import com.example.is_backend.entity.Person;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {
    @NotNull Optional<Person> findById(@NotNull Long id);

    @NotNull
    Page<Person> findAll(@NotNull Pageable pageable);



    void deleteByIdAndOwnerIdIs(Long id, Long ownerId);
    void deleteById(@NotNull Long id);
}
