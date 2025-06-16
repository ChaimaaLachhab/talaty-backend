package com.talaty.model;

import com.talaty.enums.MediaType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Media {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String mediaUrl;

    private String mediaId;
    private String originalFileName;
    private String mimeType;
    private Long fileSize;

    @Enumerated(EnumType.STRING)
    private MediaType mediaType;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id")
    private Document document;

    @JsonIgnore
    @OneToOne(mappedBy = "userPhoto", fetch = FetchType.LAZY)
    private User user;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime uploadedAt;

    public void determineTypeFromMimeType() {
        if (mimeType == null) {
            this.mediaType = MediaType.OTHER;
            return;
        }

        String lowerMimeType = mimeType.toLowerCase();
        if (lowerMimeType.startsWith("image/")) {
            this.mediaType = MediaType.IMAGE;
        } else if (lowerMimeType.equals("application/pdf")) {
            this.mediaType = MediaType.PDF;
        } else {
            this.mediaType = MediaType.OTHER;
        }
    }
}
