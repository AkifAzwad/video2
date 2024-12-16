package com.example.video2.controller;

import com.example.video2.model.Video;
import com.example.video2.service.VideoService;
import com.example.video2.service.dto.VideoMetaDataDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;



@RestController
@RequestMapping("/api/videos")
@RequiredArgsConstructor
@Validated
public class VideoController {

    private final VideoService videoService;

    @PostMapping
    public ResponseEntity<String> uploadVideo(@Valid @RequestParam String title,
                                              @Valid @RequestParam String description,
                                              @Valid @RequestParam Long assignedToUserId,
                                              @RequestParam("file") MultipartFile file) throws IOException {
        VideoMetaDataDTO metaData = new VideoMetaDataDTO();
        metaData.setTitle(title);
        metaData.setDescription(description);
        metaData.setAssignedToUserId(assignedToUserId);
        String videoId = videoService.uploadVideo(metaData, file);
        return ResponseEntity.ok("Video uploaded successfully with ID: " + videoId);
    }

    @GetMapping
    public ResponseEntity<List<Video>> fetchVideos() {
        List<Video> videos = videoService.fetchAllVideos();
        return ResponseEntity.ok(videos);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateVideo(@PathVariable Long id, @Valid @RequestBody VideoMetaDataDTO metaData) {
        videoService.updateVideo(id, metaData);
        return ResponseEntity.ok("Video updated successfully");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteVideo(@PathVariable Long id) {
        videoService.deleteVideo(id);
        return ResponseEntity.ok("Video deleted successfully");
    }

    @PostMapping("/{id}/assign")
    public ResponseEntity<String> assignVideoToUser(@PathVariable Long id, @RequestParam Long userId) {
        videoService.assignVideoToUser(id, userId);
        return ResponseEntity.ok("Video assigned to user successfully");
    }
}
