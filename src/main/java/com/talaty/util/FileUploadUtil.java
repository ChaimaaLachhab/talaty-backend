package com.talaty.util;

import com.talaty.enums.MediaType;
import lombok.experimental.UtilityClass;
import org.springframework.web.multipart.MultipartFile;

import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@UtilityClass
public class FileUploadUtil {

    public static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
    public static final long MAX_VIDEO_SIZE = 50 * 1024 * 1024; // 50MB for videos

    // Separate patterns for different file types
    public static final String IMAGE_PATTERN = "([^\\s]+(\\.(?i)(jpg|jpeg|png|gif|bmp|tif|tiff|webp|svg))$)";
    public static final String VIDEO_PATTERN = "([^\\s]+(\\.(?i)(mp4|avi|mov|mkv|webm|flv|wmv))$)";
    public static final String DOCUMENT_PATTERN = "([^\\s]+(\\.(?i)(pdf|doc|docx|xls|xlsx|ppt|pptx|txt|rtf))$)";

    // Combined pattern for all allowed media types
    public static final String ALL_MEDIA_PATTERN = IMAGE_PATTERN + "|" + VIDEO_PATTERN + "|" + DOCUMENT_PATTERN;

    public static final String DATE_FORMAT = "yyyyMMddHHmmss";
    public static final String FILE_NAME_FORMAT = "%s_%s";

    public static boolean isAllowedExtension(final String fileName, final String pattern) {
        if (fileName == null || pattern == null) {
            return false;
        }
        Matcher matcher = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE).matcher(fileName);
        return matcher.matches();
    }

    public static void assertAllowed(MultipartFile file, String pattern) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be null or empty");
        }

        // Determine max size based on file type
        long maxSize = isVideoFile(file) ? MAX_VIDEO_SIZE : MAX_FILE_SIZE;

        if (file.getSize() > maxSize) {
            throw new RuntimeException(String.format("File size exceeds maximum allowed size of %s",
                    formatFileSize(maxSize)));
        }

        if (!isAllowedExtension(file.getOriginalFilename(), pattern)) {
            throw new RuntimeException("Invalid file type. Allowed types: images, videos, documents");
        }
    }

    public static String getFileName(final String originalFileName) {
        if (originalFileName == null) {
            return String.format(FILE_NAME_FORMAT, "file", new SimpleDateFormat(DATE_FORMAT).format(System.currentTimeMillis()));
        }

        String nameWithoutExtension = originalFileName.contains(".")
                ? originalFileName.substring(0, originalFileName.lastIndexOf('.'))
                : originalFileName;

        String extension = originalFileName.contains(".")
                ? originalFileName.substring(originalFileName.lastIndexOf('.'))
                : "";

        return String.format(FILE_NAME_FORMAT, nameWithoutExtension,
                new SimpleDateFormat(DATE_FORMAT).format(System.currentTimeMillis())) + extension;
    }

    public static MediaType determineFileType(String mimeType) {
        if (mimeType == null) {
            return MediaType.OTHER;
        }

        String lowerMimeType = mimeType.toLowerCase();
        if (lowerMimeType.startsWith("image/")) {
            return MediaType.IMAGE;
        } else if (lowerMimeType.equals("application/pdf")) {
            return MediaType.PDF;
        } else {
            return MediaType.OTHER;
        }
    }

    private static boolean isVideoFile(MultipartFile file) {
        String mimeType = file.getContentType();
        return mimeType != null && mimeType.startsWith("video/");
    }

    private static String formatFileSize(long size) {
        return size / (1024 * 1024) + "MB";
    }
}