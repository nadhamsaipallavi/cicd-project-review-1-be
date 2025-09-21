package com.propertymanagement.service;

import org.springframework.web.multipart.MultipartFile;

public interface CloudinaryService {
    
    /**
     * Upload an image to Cloudinary
     * 
     * @param base64Image The base64 encoded image data
     * @param folder Optional folder to store the image in
     * @return The public URL of the uploaded image
     */
    String uploadImage(String base64Image, String folder);
    
    /**
     * Upload a file to Cloudinary
     * 
     * @param file The file to upload
     * @param folder Optional folder to store the file in
     * @return The public URL of the uploaded file
     */
    String uploadFile(MultipartFile file, String folder);
    
    /**
     * Delete an image from Cloudinary
     * 
     * @param publicId The public ID of the image to delete
     * @return True if delete was successful, false otherwise
     */
    boolean deleteImage(String publicId);
    
    /**
     * Extract the public ID from a Cloudinary URL
     * 
     * @param url The Cloudinary URL
     * @return The public ID
     */
    String getPublicIdFromUrl(String url);
    
    /**
     * Check if Cloudinary service is available
     * 
     * @return True if Cloudinary service is available, false otherwise
     */
    boolean isCloudinaryAvailable();
} 