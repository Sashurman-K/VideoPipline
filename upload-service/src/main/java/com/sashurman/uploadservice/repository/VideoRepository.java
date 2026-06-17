package com.sashurman.uploadservice.repository;

import com.sashurman.uploadservice.models.Video;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface VideoRepository extends JpaRepository<Video, UUID> {
}
