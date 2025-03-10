package com.example.is_backend.dto;


import com.example.is_backend.entity.FileEnum;
import lombok.Data;


@Data
public class FileHistoryDTO {
    private Long id;
    private String fileName;
    private FileEnum status;
    private Long ownerId;
}

