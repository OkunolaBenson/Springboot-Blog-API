package com.example.blog.DTO;

import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
public class PostRequest {
    @NotBlank
    private String title;

    @NotBlank
    private String content;

    @NotBlank
    private MultipartFile image;


}
