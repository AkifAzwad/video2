package com.example.video2.repository;


import com.example.video2.model.Video;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VideoRepository extends JpaRepository<Video, Long> {
  List<Video> findByAssignedToUserId(Long userId);
}