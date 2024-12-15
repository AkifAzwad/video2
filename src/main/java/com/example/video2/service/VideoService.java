package com.example.video2.service;


import com.example.video2.model.ActivityAction;
import com.example.video2.model.User;
import com.example.video2.model.Video;
import com.example.video2.repository.UserRepository;
import com.example.video2.repository.VideoRepository;
import com.example.video2.service.dto.VideoMetaDataDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VideoService {

    private final VideoRepository videoRepository;
    private final UserRepository userRepository;
    private final ActivityLogService activityLogService;

    @Transactional
    public String uploadVideo(VideoMetaDataDTO metaData, MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be empty");
        }

        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        // Directory for uploaded videos
        String uploadDir = "src/main/resources/static/uploads/videos";
        Path filePath = Paths.get(uploadDir, fileName);
        Files.createDirectories(filePath.getParent());
        Files.copy(file.getInputStream(), filePath);

        Video video = new Video();
        video.setTitle(metaData.getTitle());
        video.setDescription(metaData.getDescription());
        video.setVideoUrl(filePath.toString());
        video.setAssignedToUser(userRepository.findById(metaData.getAssignedToUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found")));
        videoRepository.save(video);

        return video.getId().toString();
    }

    public List<Video> fetchAllVideos() {
        activityLogService.logActivity(getCurrentUserId(), ActivityAction.VIEWED);
        return videoRepository.findAll();
    }

    @Transactional
    public void updateVideo(Long videoId, VideoMetaDataDTO metaData) {
        activityLogService.logActivity(getCurrentUserId(), ActivityAction.UPDATED);
        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new IllegalArgumentException("Video not found"));
        video.setTitle(metaData.getTitle());
        video.setDescription(metaData.getDescription());
        videoRepository.save(video);
    }

    @Transactional
    public void deleteVideo(Long videoId) {
        if (!videoRepository.existsById(videoId)) {
            throw new IllegalArgumentException("Video not found");
        }
        videoRepository.deleteById(videoId);
    }

    @Transactional
    public void assignVideoToUser(Long videoId, Long userId) {
        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new IllegalArgumentException("Video not found"));

        video.setAssignedToUser(userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found")));
        videoRepository.save(video);
    }

    public Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByUsername(authentication.getName())
                .map(User::getId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
