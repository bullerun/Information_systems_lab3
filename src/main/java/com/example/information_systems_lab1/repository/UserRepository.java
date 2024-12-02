package com.example.information_systems_lab1.repository;

import com.example.information_systems_lab1.entity.Role;
import com.example.information_systems_lab1.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);

    @Modifying
    @Query("update User u set u.role = :role where u.id = :id")
    void updateUserRoleToAdmin(@Param("id") Long id, @Param("role") Role role);
}