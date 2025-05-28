package com.example.Project_Jobhunter.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.Project_Jobhunter.dto.response.ResUploadFileDTO;
import com.example.Project_Jobhunter.service.FileService;
import com.example.Project_Jobhunter.util.annotation.ApiMessage;
import com.example.Project_Jobhunter.util.exception.StorageException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
@RequestMapping("/api/v1")
public class FileController {

    private final FileService fileService;

    @Value("${project.upload-file.base-uri}")
    private String baseURI;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("/files")
    @ApiMessage("Upload file thành công!")
    public ResponseEntity<ResUploadFileDTO> uploadFile(
            @RequestParam(name = "file", required = false) MultipartFile file,
            @RequestParam("folder") String folder)
            throws URISyntaxException, IOException, StorageException {

        if (file == null || file.isEmpty()) {
            throw new StorageException("Chưa tải file lên!");
        }
        String fileName = file.getOriginalFilename();
        List<String> allowedExtensions = Arrays.asList("pdf", "jpg", "jpeg", "png", "doc", "docx");

        boolean isValid = allowedExtensions.stream().anyMatch(item -> fileName.toLowerCase().endsWith(item));

        if (!isValid) {
            throw new StorageException(
                    "File tải lên không đúng định dạng! Chỉ cho phép " + allowedExtensions.toString());
        }

        this.fileService.handleCreateDirectory(baseURI + folder);

        String uploadFileName = this.fileService.handleStoreFile(file, folder);

        ResUploadFileDTO resUploadFileDTO = new ResUploadFileDTO(uploadFileName, Instant.now());

        return ResponseEntity.ok(resUploadFileDTO);
    }

}
