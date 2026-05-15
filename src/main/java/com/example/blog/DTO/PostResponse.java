package com.example.blog.DTO;

import com.example.blog.enums.PostStatus;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
public class PostResponse {
    private Long id;
    private String title;
    private String content;
    private String imageUrl;
    private String author;
    private LocalDateTime createdAt;
    private PostStatus status;
    private int likes;
}
