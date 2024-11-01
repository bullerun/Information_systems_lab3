package com.example.information_systems_lab1.repository;

import com.example.information_systems_lab1.entity.Hell; // Обратите внимание на правильный импорт
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HellRepository extends JpaRepository<Hell, Long> {
    Hell findByUsername(String username);
}
