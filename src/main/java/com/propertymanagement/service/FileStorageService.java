package com.propertymanagement.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileStorageService {
    
    /**
     * Store a file and return the stored file path
     * 
     * @param file The file to store
     * @param subDirectory Optional subdirectory where the file should be stored
     * @return The path where the file is stored
     */
    String storeFile(MultipartFile file, String subDirectory);
    
    /**
     * Get a file as resource
     * 
     * @param filePath The path of the file to retrieve
     * @return The file as a resource
     */
    byte[] getFile(String filePath) throws IOException;
    
    /**
     * Delete a file
     * 
     * @param filePath The path of the file to delete
     * @return true if the file was deleted, false otherwise
     */
    boolean deleteFile(String filePath);
} 