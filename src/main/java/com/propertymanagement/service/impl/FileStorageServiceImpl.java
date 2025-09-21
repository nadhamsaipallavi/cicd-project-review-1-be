package com.propertymanagement.service.impl;

import com.propertymanagement.service.FileStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageServiceImpl implements FileStorageService {
    
    @Value("${file.upload-dir:uploads}")
    private String uploadDir;
    
    @Override
    public String storeFile(MultipartFile file, String subDirectory) {
        try {
            if (file.isEmpty()) {
                throw new RuntimeException("Failed to store empty file");
            }
            
            String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf('.'));
            String filename = UUID.randomUUID().toString() + fileExtension;
            
            Path targetLocation;
            if (subDirectory != null && !subDirectory.isEmpty()) {
                Path subDirPath = Paths.get(uploadDir, subDirectory);
                Files.createDirectories(subDirPath);
                targetLocation = subDirPath.resolve(filename);
            } else {
                Path rootPath = Paths.get(uploadDir);
                Files.createDirectories(rootPath);
                targetLocation = rootPath.resolve(filename);
            }
            
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            
            if (subDirectory != null && !subDirectory.isEmpty()) {
                return subDirectory + "/" + filename;
            } else {
                return filename;
            }
            
        } catch (IOException ex) {
            throw new RuntimeException("Failed to store file", ex);
        }
    }
    
    @Override
    public byte[] getFile(String filePath) throws IOException {
        Path path = Paths.get(uploadDir, filePath);
        return Files.readAllBytes(path);
    }
    
    @Override
    public boolean deleteFile(String filePath) {
        try {
            Path path = Paths.get(uploadDir, filePath);
            return Files.deleteIfExists(path);
        } catch (IOException ex) {
            return false;
        }
    }
} 