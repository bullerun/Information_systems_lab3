package com.example.is_backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
@RequiredArgsConstructor
public class FileProcessingService {
    private final JsonService jsonService;

    public void processFile(MultipartFile file, Class<?> clazz) throws IOException {
        String contentType = file.getContentType();

        if (contentType.equals("application/json")) {
            processJsonFile(file, clazz);
        } else if (contentType.equals("application/x-zip-compressed")) {
            processZipFile(file, clazz);
        }
    }

    private void processJsonFile(MultipartFile file, Class<?> clazz) throws IOException {
        String content = new String(file.getBytes());
        System.out.println("Processing JSON file: " + file.getOriginalFilename());
        System.out.println("Content: " + content);
        var a = jsonService.parseJson(content, clazz);
    }

    private void validateZipFile(MultipartFile file) throws IOException {
        int fileCount = 0;
        try (ZipInputStream zipInputStream = new ZipInputStream(file.getInputStream())) {
            ZipEntry entry;
            while ((entry = zipInputStream.getNextEntry()) != null) {
                if (!entry.getName().endsWith(".json")) {
                    throw new IllegalArgumentException("Только json файлы должны быть в зипке, не наркомань");
                }
                fileCount++;
                zipInputStream.closeEntry();
                if (fileCount > 10) {
                    throw new IllegalArgumentException("В зипке не может быть больше 10, с передозом не шути");
                }
            }
        }
    }

    private void processZipFile(MultipartFile file, Class<?> clazz) throws IOException {
        validateZipFile(file);
        var jsonList = new ArrayList<String>();
        try (ZipInputStream zipInputStream = new ZipInputStream(file.getInputStream())) {
            ZipEntry entry;
            while ((entry = zipInputStream.getNextEntry()) != null) {
                System.out.println("Processing file in ZIP: " + entry.getName());
                String content = new String(zipInputStream.readAllBytes());
                jsonList.add(content);
                System.out.println("Content: " + content);
                zipInputStream.closeEntry();
            }
        }
        jsonService.parseJsons(jsonList, clazz);
    }
}
