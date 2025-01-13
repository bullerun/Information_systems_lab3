package com.example.is_backend.repository;

import com.example.is_backend.dto.FileHistoryDTO;
import com.example.is_backend.entity.FileHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileHistoryRepository extends JpaRepository<FileHistory, Long> {

    List<FileHistory> getByOwnerId(Long currentUserId);
}
