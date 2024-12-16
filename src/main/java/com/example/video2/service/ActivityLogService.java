package com.example.video2.service;

import com.example.video2.model.ActivityAction;
import com.example.video2.model.ActivityLog;
import com.example.video2.model.User;
import com.example.video2.repository.ActivityLogRepository;
import com.example.video2.repository.UserRepository;
import com.example.video2.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ActivityLogService {

    private final ActivityLogRepository activityLogRepository;
    private final UserRepository userRepository;
    private final VideoRepository videoRepository;

    public void logActivityAction(ActivityAction action) {
        logActivity(getCurrentUserId(), action);
    }

    private void logActivity(Long userId, ActivityAction action) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        ActivityLog log = ActivityLog.builder()
                .user(user)
                .action(action)
                .timestamp(LocalDateTime.now())
                .build();

        activityLogRepository.save(log);
    }

    public List<ActivityLog> getAllActivityLogs() {
        return activityLogRepository.findAllByOrderByTimestampDesc();
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            throw new UsernameNotFoundException("Authentication information not available");
        }
        return userRepository.findByUsername(authentication.getName())
                .map(User::getId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + authentication.getName()));
    }

}
