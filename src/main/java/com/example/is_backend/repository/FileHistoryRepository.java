package com.example.is_backend.repository;

import com.example.is_backend.entity.FileEnum;
import com.example.is_backend.entity.FileHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileHistoryRepository extends JpaRepository<FileHistory, Long> {

    List<FileHistory> getByOwnerId(Long currentUserId);

    @Query("select f from FileHistory f where f.status = :status")
    List<FileHistory> findAllByStatus(@Param("status") FileEnum status);
}
