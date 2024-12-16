package com.example.video2.service;

import com.example.video2.model.ActivityAction;
import com.example.video2.model.User;
import com.example.video2.model.Video;
import com.example.video2.repository.UserRepository;
import com.example.video2.repository.VideoRepository;
import com.example.video2.service.dto.VideoMetaDataDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${app.upload-dir}")
    private String uploadDir;

    @Value("${app.allowed-video-types}")
    private String[] allowedVideoTypes;

    @Transactional
    public String uploadVideo(VideoMetaDataDTO metaData, MultipartFile file) throws IOException {
        validateFile(file);

        String fileName = generateUniqueFileName(file.getOriginalFilename());
        Path filePath = saveFile(file, fileName);

        Video video = new Video();
        video.setTitle(metaData.getTitle());
        video.setDescription(metaData.getDescription());
        video.setVideoUrl(filePath.toString());
        video.setAssignedToUser(findUserById(metaData.getAssignedToUserId()));

        videoRepository.save(video);
        activityLogService.logActivityAction(ActivityAction.UPLOADED);

        return video.getId().toString();
    }

    public List<Video> fetchAllVideos() {
        activityLogService.logActivityAction(ActivityAction.VIEWED);
        return videoRepository.findAll();
    }

    @Transactional
    public void updateVideo(Long videoId, VideoMetaDataDTO metaData) {
        Video video = findVideoById(videoId);
        video.setTitle(metaData.getTitle());
        video.setDescription(metaData.getDescription());
        videoRepository.save(video);

        activityLogService.logActivityAction(ActivityAction.UPDATED);
    }

    @Transactional
    public void deleteVideo(Long videoId) {
        Video video = findVideoById(videoId);
        videoRepository.delete(video);

        activityLogService.logActivityAction(ActivityAction.DELETED);
    }

    @Transactional
    public void assignVideoToUser(Long videoId, Long userId) {
        Video video = findVideoById(videoId);
        User user = findUserById(userId);

        video.setAssignedToUser(user);
        videoRepository.save(video);

        activityLogService.logActivityAction(ActivityAction.ASSIGNED);
    }

    private Path saveFile(MultipartFile file, String fileName) throws IOException {
        Path filePath = Paths.get(uploadDir, fileName);
        Files.createDirectories(filePath.getParent());
        Files.copy(file.getInputStream(), filePath);
        return filePath;
    }

    private String generateUniqueFileName(String originalFilename) {
        return UUID.randomUUID() + "_" + originalFilename;
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty() || !isValidVideoFile(file)) {
            throw new IllegalArgumentException("Invalid file type. Only video files are allowed.");
        }
    }

    private Video findVideoById(Long videoId) {
        return videoRepository.findById(videoId)
                .orElseThrow(() -> new IllegalArgumentException("Video with ID " + videoId + " not found"));
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User with ID " + userId + " not found"));
    }

    private boolean isValidVideoFile(MultipartFile file) {
        String contentType = file.getContentType();
        return List.of(allowedVideoTypes).contains(contentType);
    }

}
