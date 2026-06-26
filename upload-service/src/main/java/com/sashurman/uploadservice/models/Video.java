package com.sashurman.uploadservice.models;

import com.sashurman.uploadservice.models.json.Chunk;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "videos")
@NoArgsConstructor
@Data
public class Video {
    @Id
    @GeneratedValue(strategy =  GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id")
    private UUID userId;

    @Column(length = 255)
    private String title;

    @Column(length = 1000)
    private String description;

    @Column(name = "original_path", length = 500)
    private String originalPath;

    @Column(name="file_size_bytes", columnDefinition = "BIGINT")
    private Long fileSizeBytes;

    @Column(length = 10, nullable = false)
    private String format;

    @Column(name = "duration_sec")
    private double duration_sec;

    @Column(nullable = false, length = 20)
    private String status;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name="chunks", columnDefinition = "jsonb")
    private List<Chunk> jsonProperty;
    @Column(name = "ai_description", columnDefinition = "TEXT")
    private String aiDescription;

    @Column(name = "ai_confidence")
    private double aiConfidence;

    @Column(name = "error_massage", columnDefinition = "TEXT")
    private String errorMassage;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @PrePersist
    void onCreate(){
        this.createdAt = LocalDateTime.now();
        this.status = "UPLOADED";
    }
    @PreUpdate
    void onUpdate(){
        this.updatedAt = LocalDateTime.now();
    }

    public Video(UUID userId,
                 String title,
                 String originalPath,
                 String format){
        this.title = title;
        this.userId = userId;
        this.originalPath = originalPath;
        this.format = format;
    }
    public Video(UUID userId,
                 String title,
                 String format){
        this.title = title;
        this.userId = userId;
        this.format = format;
    }
}
