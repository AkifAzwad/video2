package com.example.video2.controller;

import com.example.video2.model.ActivityLog;
import com.example.video2.service.ActivityLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ActivityLogController {

    private final ActivityLogService activityLogService;

    // Endpoint for admin to view all activity logs
    @GetMapping("/activity-log")
    public ResponseEntity<List<ActivityLog>> getAllActivityLogs() {
        List<ActivityLog> logs = activityLogService.getAllActivityLogs();
        return ResponseEntity.ok(logs);
    }
}
