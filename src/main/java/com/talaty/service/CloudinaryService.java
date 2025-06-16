package com.talaty.service;

import com.talaty.dto.response.CloudinaryResponse;
import com.talaty.exception.FileUploadException;
import com.cloudinary.Cloudinary;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Service
public class CloudinaryService {
    private final Cloudinary cloudinary;

    @Autowired
    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    @Transactional
    public CloudinaryResponse uploadFile(MultipartFile file, String fileName, String resourceType) {
        try {
            Map<String, Object> result = cloudinary.uploader().upload(file.getBytes(), Map.of(
                    "public_id", "nhndev/product/" + fileName,
                    "resource_type", resourceType
            ));

            String url = (String) result.get("secure_url");
            String publicId = (String) result.get("public_id");

            if (url == null || publicId == null) {
                throw new FileUploadException("Cloudinary upload did not return expected results");
            }

            return CloudinaryResponse.builder().publicId(publicId).url(url).build();
        } catch (Exception e) {
            throw new FileUploadException("Failed to upload file to Cloudinary", e);
        }
    }

    @Transactional
    public boolean deleteFile(String publicId) {
        try {
            if (publicId == null || publicId.trim().isEmpty()) {
                return false;
            }

            Map<String, Object> result = cloudinary.uploader().destroy(publicId, Map.of());
            String deleteResult = (String) result.get("result");

            return "ok".equals(deleteResult) || "not found".equals(deleteResult);
        } catch (Exception e) {
            throw new FileUploadException("Failed to delete file from Cloudinary: " + publicId, e);
        }
    }

    @Transactional
    public boolean deleteMultipleFiles(List<String> publicIds) {
        try {
            if (publicIds == null || publicIds.isEmpty()) {
                return true;
            }

            Map<String, Object> result = cloudinary.api().deleteResources(publicIds, Map.of());
            return result != null;
        } catch (Exception e) {
            throw new FileUploadException("Failed to delete multiple files from Cloudinary", e);
        }
    }
}