package com.example.is_backend.service;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@Service
public class MinioService {
    private final MinioClient minioClient;

    public MinioService() {
        this.minioClient = MinioClient.builder()
                .endpoint("http://localhost:9000")
                .credentials("admin", "password")
                .build();
    }

    public void uploadFile(String bucketName, String objectName, MultipartFile file) throws Exception {
        try (InputStream inputStream = file.getInputStream()) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .stream(inputStream, file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );
        }
    }

    public void deleteFile(String bucketName, String objectName) throws Exception {
        minioClient.removeObject(
                RemoveObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .build()
        );
    }
}
