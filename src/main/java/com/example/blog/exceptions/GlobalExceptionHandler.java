package com.example.blog.exceptions;

import com.example.blog.DTO.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex) {
    return ResponseEntity.status(404).body(
            new ErrorResponse(ex.getMessage(), LocalDateTime.now())
    );
  }

  @ExceptionHandler(DuplicateResourceException.class)
  public ResponseEntity<ErrorResponse> handleDuplicate(DuplicateResourceException ex) {
    return ResponseEntity.badRequest().body(
            new ErrorResponse(ex.getMessage(), LocalDateTime.now())
    );
  }
}
