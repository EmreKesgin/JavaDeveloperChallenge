package com.API.JavaChallengeDemo.service;

import com.API.JavaChallengeDemo.entity.FileEntity;
import com.API.JavaChallengeDemo.exception.FileNotFoundException;
import com.API.JavaChallengeDemo.repository.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import static com.API.JavaChallengeDemo.controller.FileController.UPLOAD_DIR;

@Service
public class FileService {

    @Autowired
    private FileRepository repository;

    public ResponseEntity<String> validateFile(MultipartFile file) {

        long maxSize = 5 * 1024 * 1024; // 5 MB
        if (file.getSize() > maxSize) {
            return ResponseEntity.badRequest().body("File size exceeds the maximum limit of 5 MB.");
        }

        String allowedExtensions = "^.*\\.(png|jpeg|jpg|docx|pdf|xlsx)$";
        if (!file.getOriginalFilename().matches(allowedExtensions)) {
            return ResponseEntity.badRequest().body("Invalid file extension.");
        }

        String fileName = file.getOriginalFilename();
        if (checkIfFileExists(fileName)) {
            return ResponseEntity.badRequest().body("This file is already registered in the system.");
        }
        return null;
    }

    private boolean checkIfFileExists(String fileName) {
        Optional<FileEntity> fileEntityOptional = repository.findByFileName(fileName);
        return fileEntityOptional.isPresent();
    }

    public String uploadFile(MultipartFile file) throws IOException {

        ResponseEntity<String> validationResponse = validateFile(file);

        if (validationResponse != null) {
            // File validation failed
            return validationResponse.getBody();
        }

        // ------------- Directory
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        // Save the file to the specified directory
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        Files.copy(file.getInputStream(), uploadPath.resolve(fileName));
        // -------------

        String uploadDirectory = UPLOAD_DIR + "/" + file.getOriginalFilename();
        FileEntity fileEntity= repository.save(FileEntity.builder()
                .fileName(file.getOriginalFilename())
                .fileExtension(file.getContentType())
                .filePath(uploadDirectory)
                .fileSize(file.getSize())
                .fileData(file.getBytes()).build());

        if(fileEntity!=null){
            return "File successfully uploaded: " + file.getOriginalFilename();
        }
        return null;
    }

    public List<FileEntity> findAll() {
        return repository.findAll();
    }

    public FileEntity findById(long theId) {

        Optional<FileEntity> result =  repository.findById(theId);
        FileEntity theFileEntity = null;

        if(result.isPresent()) {
            theFileEntity = result.get();
        }
        else {
            return result.orElseThrow(() -> new FileNotFoundException("Did not find file id - " + theId));
        }
        return theFileEntity;
    }

    public void deleteById(long theId) {
        repository.deleteById(theId);
    }

    public FileEntity updateFile(FileEntity fileEntity) {
        return repository.save(fileEntity);
    }
}