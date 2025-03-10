package com.example.is_backend.service;

import com.example.is_backend.authentication.service.UserServices;
import com.example.is_backend.entity.FileEnum;
import com.example.is_backend.entity.FileHistory;
import com.example.is_backend.entity.TransactionDesicion;
import com.example.is_backend.exception.InsufficientEditingRightsException;
import com.example.is_backend.exception.NotFoundException;
import com.example.is_backend.exception.PersonValidationException;
import com.example.is_backend.repository.FileHistoryRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
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
    //    private static final String HASH_FILE_PATH = "hashes.txt";
    private final ConcurrentHashMap<String, String> storedHashes = new ConcurrentHashMap<>();
    private final UserServices userServices;
    private final MinioService minioService;
    private final FileHistoryService fileHistoryService;

//    @PostConstruct
//    public void loadHashes() {
//        File hashFile = new File(HASH_FILE_PATH);
//        if (hashFile.exists()) {
//            try (BufferedReader reader = new BufferedReader(new FileReader(hashFile))) {
//                String line;
//                while ((line = reader.readLine()) != null) {
//                    String[] parts = line.split(":", 2);
//                    if (parts.length == 2) {
//                        storedHashes.put(parts[0], parts[1]);
//                    }
//                }
//                System.out.println("Хэши загружены: " + storedHashes);
//            } catch (IOException e) {
//                throw new RuntimeException("Ошибка при загрузке файла хэшей", e);
//            }
//        }
//    }

    @PostConstruct
    public void isProcessingTransactionHaveFile() {
        for (FileHistory fileHistory : fileHistoryRepository.findAllByStatus(FileEnum.PROCESSING)) {
            try {
                minioService.getFile(fileHistory.getFileNameInMinio());
            } catch (Exception e) {
                fileHistory.setStatus(FileEnum.ERROR);
                fileHistoryService.save(fileHistory);
            }
        }
    }

    @Transactional(rollbackFor = Exception.class, noRollbackFor = {
            IllegalArgumentException.class, PersonValidationException.class,
            NotFoundException.class, InsufficientEditingRightsException.class})
    public void processFile(MultipartFile file, Class<?> clazz, String fileNameInMinio, FileHistory fileHistory)
            throws IllegalArgumentException, IOException, PersonValidationException,
            NotFoundException, InterruptedException {

        try {
            processZipFile(file.getInputStream(), clazz);
        } catch (Exception e) {
            try {
                minioService.uploadFile(fileNameInMinio, file);
            } catch (Exception ex) {
                throw new RuntimeException("Чет Minio не работает и бд не работает");
            }
            throw e;
        }

        try {
            minioService.uploadFile(fileNameInMinio, file);
        } catch (Exception e) {
            fileHistoryService.updateStatus(fileHistory, FileEnum.ERROR);
            throw new RuntimeException("Чет Minio не работает");
        }

        Thread.sleep(10 * 1000);
    }

    @Transactional
    public void processUncommitedTransaction(Long fileHistoryId, Class<?> clazz, TransactionDesicion decision) throws Exception {
        FileHistory fileHistory = fileHistoryService.findById(fileHistoryId);
        if (fileHistory.getStatus() != FileEnum.PROCESSING) {
            throw new RuntimeException("руки прочь");
        }
        if (decision == TransactionDesicion.APPROVE) {
            InputStream stream = minioService.getFile(fileHistory.getFileNameInMinio());
            processZipFile(stream, clazz);
            fileHistoryService.updateStatus(fileHistoryId, FileEnum.COMPLETED);
        } else if (decision == TransactionDesicion.REJECTED) {
            minioService.deleteFile(fileHistory.getFileNameInMinio());
            fileHistoryService.updateStatus(fileHistoryId, FileEnum.REJECTED);
        } else {
            throw new RuntimeException("а я не понял что вы хотите хехе");
        }
    }
//    private void processJsonFile(MultipartFile file, Class<?> clazz) throws IOException {
//        String content = new String(file.getBytes());
//        System.out.println("Processing JSON file: " + file.getOriginalFilename());
//        System.out.println("Content: " + content);
//        var a = jsonService.parseJson(content, clazz);
//    }

    private void validateZipFile(InputStream file) throws IOException {
        int fileCount = 0;
        try (ZipInputStream zipInputStream = new ZipInputStream(file)) {
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

    private void processZipFile(InputStream file, Class<?> clazz) throws IOException, PersonValidationException, NotFoundException {
        var jsonList = new ArrayList<String>();
        try (ZipInputStream zipInputStream = new ZipInputStream(file)) {
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
            return userServices.getCurrentUsername() + hashString;
        } catch (NoSuchAlgorithmException | IOException e) {
            throw new RuntimeException("Ошибка при вычислении хэша", e);
        }
    }

    public void saveHash(MultipartFile file) {
        storedHashes.put(calculateHash(file), Objects.requireNonNull(file.getOriginalFilename()));
    }

    public void deleteHash(MultipartFile file) {
        storedHashes.remove(calculateHash(file));

    }

}
