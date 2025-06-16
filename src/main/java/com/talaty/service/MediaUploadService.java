package com.talaty.service;

import com.talaty.dto.response.CloudinaryResponse;
import com.talaty.exception.FileUploadException;
import com.talaty.exception.UnsupportedEntityException;
import com.talaty.model.Document;
import com.talaty.model.Media;
import com.talaty.model.User;
import com.talaty.repository.MediaRepository;
import com.talaty.util.FileUploadUtil;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MediaUploadService {
    private final CloudinaryService cloudinaryService;
    private final MediaRepository mediaRepository;

    @Autowired
    public MediaUploadService(CloudinaryService cloudinaryService, MediaRepository mediaRepository) {
        this.cloudinaryService = cloudinaryService;
        this.mediaRepository = mediaRepository;
    }

    @Transactional
    public Media handleMediaUpload(MultipartFile file, Object entity) {
        try {
            FileUploadUtil.assertAllowed(file, FileUploadUtil.ALL_MEDIA_PATTERN);
            String fileName = FileUploadUtil.getFileName(file.getOriginalFilename());
            CloudinaryResponse response = cloudinaryService.uploadFile(file, fileName, "auto");

            Media media = createMediaFromFile(file, response);
            associateWithEntity(media, entity);

            return media;
        } catch (Exception e) {
            throw new FileUploadException("Failed to handle media upload: " + e.getMessage(), e);
        }
    }

    public List<Media> handleMultipleMediaUpload(List<MultipartFile> files, Document document) {
        List<Media> uploadedMedia = new ArrayList<>();
        List<String> uploadedCloudinaryIds = new ArrayList<>();

        try {
            for (MultipartFile file : files) {
                if (file != null && !file.isEmpty()) {
                    FileUploadUtil.assertAllowed(file, FileUploadUtil.ALL_MEDIA_PATTERN);
                    String fileName = FileUploadUtil.getFileName(file.getOriginalFilename());
                    CloudinaryResponse response = cloudinaryService.uploadFile(file, fileName, "auto");

                    uploadedCloudinaryIds.add(response.getPublicId());

                    Media media = createMediaFromFile(file, response);
                    media.setDocument(document);
                    uploadedMedia.add(media);
                }
            }
            return uploadedMedia;
        } catch (Exception e) {
            // Rollback uploaded files on error
            rollbackCloudinaryUploads(uploadedCloudinaryIds);
            throw new FileUploadException("Failed to upload multiple media files: " + e.getMessage(), e);
        }
    }

    public boolean deleteMediaFile(Media media) {
        try {
            if (media.getMediaId() != null && !media.getMediaId().trim().isEmpty()) {
                return cloudinaryService.deleteFile(media.getMediaId());
            }
            return true;
        } catch (Exception e) {
            System.err.println("Failed to delete media from Cloudinary: " + media.getMediaId() + " - " + e.getMessage());
            return false;
        }
    }

    public boolean deleteMultipleMediaFiles(List<Media> mediaList) {
        try {
            List<String> publicIds = mediaList.stream()
                    .map(Media::getMediaId)
                    .filter(id -> id != null && !id.trim().isEmpty())
                    .collect(Collectors.toList());

            if (publicIds.isEmpty()) {
                return true;
            }

            return cloudinaryService.deleteMultipleFiles(publicIds);
        } catch (Exception e) {
            System.err.println("Failed to delete multiple media files from Cloudinary: " + e.getMessage());
            return false;
        }
    }

    private Media createMediaFromFile(MultipartFile file, CloudinaryResponse response) {
        Media media = new Media();
        media.setMediaUrl(response.getUrl());
        media.setMediaId(response.getPublicId());
        media.setOriginalFileName(file.getOriginalFilename());
        media.setMimeType(file.getContentType());
        media.setFileSize(file.getSize());
        media.setMediaType(FileUploadUtil.determineFileType(file.getContentType()));
        return media;
    }

    private void associateWithEntity(Media media, Object entity) {
        if (entity instanceof Document) {
            media.setDocument((Document) entity);
        } else if (entity instanceof User) {
            media.setUser((User) entity);
        } else {
            throw new UnsupportedEntityException("Unsupported entity type: " + entity.getClass().getSimpleName());
        }
    }

    private void rollbackCloudinaryUploads(List<String> publicIds) {
        for (String publicId : publicIds) {
            try {
                cloudinaryService.deleteFile(publicId);
            } catch (Exception e) {
                System.err.println("Failed to rollback Cloudinary file: " + publicId);
            }
        }
    }
}
