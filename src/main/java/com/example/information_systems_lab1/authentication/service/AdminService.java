package com.example.information_systems_lab1.authentication.service;

import com.example.information_systems_lab1.entity.AdminQueue;
import com.example.information_systems_lab1.entity.StatusInAdminQueue;
import com.example.information_systems_lab1.repository.AdminQueueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final AdminQueueRepository adminQueueRepository;
    private final UserServices userServices;

    public List<AdminQueue> getAllAdminQueues() {
        return adminQueueRepository.findAllWithStatusPending(StatusInAdminQueue.PENDING);
    }
    public void add(Long id) {
        AdminQueue newAdminQueue = new AdminQueue();
        newAdminQueue.setOwnerId(id);
        adminQueueRepository.save(newAdminQueue);
    }
    @Transactional
    public void setAdmin(Long id) {
        userServices.setAdmin(id);
        adminQueueRepository.deleteByOwnerId(id);
    }
    @Transactional
    public void reject(Long id) {
        adminQueueRepository.deleteByOwnerId(id);
    }
}
