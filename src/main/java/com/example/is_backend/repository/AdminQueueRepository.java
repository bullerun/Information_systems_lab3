package com.example.is_backend.repository;

import com.example.is_backend.entity.AdminQueue;
import com.example.is_backend.entity.StatusInAdminQueue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdminQueueRepository extends JpaRepository<AdminQueue, Long> {
    @Query("select a from AdminQueue a where a.status = :status")
    List<AdminQueue> findAllWithStatusPending(@Param("status")StatusInAdminQueue status);


    void deleteByOwnerId(Long id);



}
