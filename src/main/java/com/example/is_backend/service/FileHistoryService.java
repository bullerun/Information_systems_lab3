package com.example.is_backend.service;

import com.example.is_backend.entity.FileEnum;
import com.example.is_backend.entity.FileHistory;
import com.example.is_backend.exception.NotFoundException;
import com.example.is_backend.repository.FileHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class FileHistoryService {

    private final FileHistoryRepository fileHistoryRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public FileHistory save(FileHistory fileHistory) {
        return fileHistoryRepository.save(fileHistory);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public FileHistory findById(Long id) throws NotFoundException {
        return fileHistoryRepository.findById(id).orElseThrow(
                () -> new NotFoundException("не найдено")
        );
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateStatus(FileHistory fileHistory, FileEnum status) {
        fileHistory.setStatus(status);
        fileHistoryRepository.save(fileHistory);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateStatus(Long fileHistoryId, FileEnum status) throws NotFoundException {
        var fileHistory = fileHistoryRepository.findById(fileHistoryId).orElseThrow(
                () -> new NotFoundException("Чет нельзя с файлом поработать")
        );
        fileHistory.setStatus(status);
        fileHistoryRepository.save(fileHistory);
    }
}
