package com.example.information_systems_lab1.repository;

import com.example.information_systems_lab1.entity.Hell; // Обратите внимание на правильный импорт
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HellRepository extends JpaRepository<Hell, Long> {
    // Здесь вы можете добавить свои собственные методы, если это необходимо
    // Например, метод для поиска пользователя по имени:
    Hell findByUsername(String username);
}
