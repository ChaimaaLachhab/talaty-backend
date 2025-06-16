package com.talaty.model;

import com.talaty.enums.DocumentType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DocumentType type;

    @Column(nullable = false)
    private String name;

    private String description;
    private boolean required = false;
    private boolean verified = false;

    // Processing Information
    private String processingNotes;
    private LocalDateTime processedAt;
    private Long processedBy;

    // Document Data Extraction (for scoring)
    @Column(columnDefinition = "TEXT")
    private String extractedData; // JSON format for extracted information

    private boolean dataExtracted = false;
    private LocalDateTime dataExtractedAt;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id")
    private List<Media> mediaFiles = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ekyc_id")
    private EKYC ekyc;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public void addMediaFile(Media media) {
        mediaFiles.add(media);
        media.setDocument(this);
    }

    public boolean hasMediaFiles() {
        return !mediaFiles.isEmpty();
    }
}
