package com.example.blog.DTO;

import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ErrorResponse {
    private String message;
    private LocalDateTime timeStamp;
}
