package com.example.video2.controller;

import com.example.video2.model.Video;
import com.example.video2.service.VideoService;
import com.example.video2.service.dto.VideoMetaDataDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.util.List;

@RestController
@RequestMapping("/api/videos")
@RequiredArgsConstructor
@Validated
public class VideoController {

    private final VideoService videoService;

    private static final List<String> ALLOWED_VIDEO_TYPES = List.of("video/mp4", "video/avi", "video/mkv");

    @PostMapping
    public ResponseEntity<String> uploadVideo(@Valid @RequestParam String title,
                                              @Valid @RequestParam String description,
                                              @Valid @RequestParam Long assignedToUserId,
                                              @RequestParam("file") MultipartFile file) {
        try {
            if (!isValidVideoFile(file)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Invalid file type. Only video files are allowed.");
            }

            VideoMetaDataDTO metaData = new VideoMetaDataDTO();
            metaData.setTitle(title);
            metaData.setDescription(description);
            metaData.setAssignedToUserId(assignedToUserId);
            String videoId = videoService.uploadVideo(metaData, file);
            return ResponseEntity.ok("Video uploaded successfully with ID: " + videoId);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to upload video: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<Video>> fetchVideos() {
        List<Video> videos = videoService.fetchAllVideos();
        return ResponseEntity.ok(videos);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateVideo(@PathVariable Long id, @Valid @RequestBody VideoMetaDataDTO metaData) {
        try {
            videoService.updateVideo(id, metaData);
            return ResponseEntity.ok("Video updated successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to update video: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteVideo(@PathVariable Long id) {
        try {
            videoService.deleteVideo(id);
            return ResponseEntity.ok("Video deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to delete video: " + e.getMessage());
        }
    }

    @PostMapping("/{id}/assign")
    public ResponseEntity<String> assignVideoToUser(@PathVariable Long id, @RequestParam Long userId) {
        try {
            videoService.assignVideoToUser(id, userId);
            return ResponseEntity.ok("Video assigned to user successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to assign video: " + e.getMessage());
        }
    }

    private boolean isValidVideoFile(MultipartFile file) {
        String contentType = file.getContentType();
        return ALLOWED_VIDEO_TYPES.contains(contentType);
    }
}
