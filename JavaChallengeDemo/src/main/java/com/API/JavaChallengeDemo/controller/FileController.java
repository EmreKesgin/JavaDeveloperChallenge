package com.API.JavaChallengeDemo.controller;

import com.API.JavaChallengeDemo.entity.FileEntity;
import com.API.JavaChallengeDemo.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/files")
public class FileController {

    private FileService service;

    @Autowired
    public FileController(FileService fileService) {
        this.service = fileService;
    }

    public static final String UPLOAD_DIR = "C:\\Users\\emre_\\OneDrive\\Desktop\\Storage";

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {

        String uploadFile = String.valueOf(service.uploadFile(file));
        return ResponseEntity.status(HttpStatus.OK).body(uploadFile);
    }

    @GetMapping("/list")
    public List<FileEntity> findAll() {
        return service.findAll();
    }

    @GetMapping("/list/{fileId}")
    public FileEntity getFile(@PathVariable long fileId) {

        FileEntity theFileEntity = service.findById(fileId);
        return theFileEntity;
    }

    @DeleteMapping("/delete/{fileId}")
    public String deleteFile(@PathVariable long fileId) throws IOException{

        FileEntity theFileEntity=service.findById(fileId);
        service.deleteById(fileId);
        return "Deleted file id - " + fileId;
    }

    @PutMapping("/update/{fileId}")
    public ResponseEntity<?> updateFile(@PathVariable long fileId, @RequestParam("file") MultipartFile file) throws IOException {

        FileEntity existingFile = service.findById(fileId);

        if (existingFile == null) {
            return ResponseEntity.notFound().build();
        }

        ResponseEntity<String> validationResponse = service.validateFile(file);

        if (validationResponse != null) {
            // File validation failed
            return validationResponse;
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
        existingFile.setFileName(file.getOriginalFilename());
        existingFile.setFileExtension(file.getContentType());
        existingFile.setFilePath(uploadDirectory);
        existingFile.setFileSize(file.getSize());
        existingFile.setFileData(file.getBytes());

        FileEntity updatedFile = service.updateFile(existingFile);

        if (updatedFile != null) {
            return ResponseEntity.status(HttpStatus.OK).body("File successfully updated: " + file.getOriginalFilename());
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update the file.");
    }
}