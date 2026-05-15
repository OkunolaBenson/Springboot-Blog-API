package com.example.blog.controllers;

import com.example.blog.services.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/likes")
@RequiredArgsConstructor
public class LikeController {
    private final LikeService likeService;

    @PostMapping("/{postId}/like")
    public String likePost(@PathVariable Long postId, @RequestParam Long userId) {
        likeService.likePost(postId, userId);
        return "Post liked successfully";
    }
}
