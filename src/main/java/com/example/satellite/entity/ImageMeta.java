package com.example.satellite.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "images")
@Data
public class ImageMeta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "filename")
    private String filename;

    @Column(name = "lat")
    private double lat;

    @Column(name = "lng")
    private double lng;

    @Column(name = "uploaded_at")
    private LocalDateTime uploadedAt;

    @Column(name = "thumbnail_generated")
    private boolean thumbnailGenerated;

    @PrePersist
    public void prePersist() { uploadedAt = LocalDateTime.now(); }

}
