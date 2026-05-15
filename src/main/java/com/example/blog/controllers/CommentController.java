package com.example.blog.controllers;

import com.example.blog.DTO.CommentRequest;
import com.example.blog.models.Comment;
import com.example.blog.services.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @PostMapping("/{postId}")
    public Comment addComment(@RequestBody CommentRequest request, @RequestParam Long userId, @PathVariable Long postId) {
        return commentService.addComment(postId, userId, request);
    }
}
