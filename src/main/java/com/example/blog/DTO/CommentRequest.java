package com.example.blog.DTO;

import jakarta.validation.constraints.*;
import lombok.*;

import java.util.Scanner;

@Data
public class CommentRequest {
    @NotBlank
    private String content;
}
