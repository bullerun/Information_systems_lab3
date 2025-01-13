package com.example.is_backend.service;

import com.example.is_backend.authentication.service.UserServices;
import com.example.is_backend.entity.FileEnum;
import com.example.is_backend.entity.FileHistory;
import com.example.is_backend.exception.NotFoundException;
import com.example.is_backend.exception.PersonValidationException;
import com.example.is_backend.repository.FileHistoryRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
@RequiredArgsConstructor
public class FileProcessingService {
    private final FileHistoryRepository fileHistoryRepository;
    private final JsonService jsonService;
    private static final String HASH_FILE_PATH = "hashes.txt";
    private final ConcurrentHashMap<String, String> storedHashes = new ConcurrentHashMap<>();
    private final UserServices userServices;

    @PostConstruct
    public void loadHashes() {
        // Загружаем хэши из файла при старте
        File hashFile = new File(HASH_FILE_PATH);
        if (hashFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(hashFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(":", 2);
                    if (parts.length == 2) {
                        storedHashes.put(parts[0], parts[1]);
                    }
                }
                System.out.println("Хэши загружены: " + storedHashes);
            } catch (IOException e) {
                throw new RuntimeException("Ошибка при загрузке файла хэшей", e);
            }
        }
    }

    public void processFile(MultipartFile file, Class<?> clazz) throws IllegalArgumentException, IOException, PersonValidationException, NotFoundException {
        String contentType = file.getContentType();
        FileHistory fileHistory = new FileHistory();
        fileHistory.setFileName(file.getOriginalFilename());
        fileHistory.setOwnerId(userServices.getCurrentUserId());
        fileHistoryRepository.save(fileHistory);
        if (Objects.equals(contentType, "application/x-zip-compressed")) {
                String hash = calculateHash(file);
            if (storedHashes.containsKey(hash)) {
                fileHistory.setStatus(FileEnum.DUPLICATE);
                fileHistoryRepository.save(fileHistory);
                throw new IllegalArgumentException("Этот ZIP-файл уже был обработан");
            }
            processZipFile(file, clazz);
            fileHistory.setStatus(FileEnum.COMPLETED);
            fileHistoryRepository.save(fileHistory);

            saveHash(file.getOriginalFilename(), hash);
        } else {
            throw new RuntimeException("Я ХОЧУ ЗИПКУ");
        }
    }

//    private void processJsonFile(MultipartFile file, Class<?> clazz) throws IOException {
//        String content = new String(file.getBytes());
//        System.out.println("Processing JSON file: " + file.getOriginalFilename());
//        System.out.println("Content: " + content);
//        var a = jsonService.parseJson(content, clazz);
//    }

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

    private void processZipFile(MultipartFile file, Class<?> clazz) throws IOException, PersonValidationException, NotFoundException {
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

    private String calculateHash(MultipartFile file) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] buffer = new byte[1024];
            int bytesRead;
            try (InputStream inputStream = file.getInputStream()) {
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    digest.update(buffer, 0, bytesRead);
                }
            }
            byte[] hashBytes = digest.digest();
            StringBuilder hashString = new StringBuilder();
            for (byte b : hashBytes) {
                hashString.append(String.format("%02x", b));
            }
            return hashString.toString();
        } catch (NoSuchAlgorithmException | IOException e) {
            throw new RuntimeException("Ошибка при вычислении хэша", e);
        }
    }

    private void saveHash(String fileName, String hash) {
        storedHashes.put(hash, fileName);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(HASH_FILE_PATH, true))) {
            writer.write(hash + ":" + fileName);
            writer.newLine();
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при записи хэша в файл", e);
        }
    }

}
