package com.example.blog.controllers;

import com.example.blog.DTO.PostRequest;
import com.example.blog.DTO.PostResponse;
import com.example.blog.models.Post;
import com.example.blog.services.PostService;
import lombok.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public PostResponse createPost(@RequestParam Long userId, @RequestParam String title, @RequestParam String content, @RequestParam MultipartFile image) {
        PostRequest request = PostRequest.builder()
                .title(title)
                .content(content)
                .image(image)
                .build();

        return postService.createPost(request, userId);
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping
    public Page<PostResponse> getAllPosts(Pageable pageable) {
        return postService.getAllPosts(pageable);
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/{id}")
    public PostResponse getPostById(@PathVariable Long id) {
        return postService.getPostById(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public PostResponse updatePost(@PathVariable Long id, @RequestParam String title, @RequestParam String content, @RequestParam(required = false) MultipartFile image){
        PostRequest request = PostRequest.builder()
                .title(title)
                .content(content)
                .image(image)
                .build();

        return postService.updatePost(id, request);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public String deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return "Post Deleted Successfully";
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PostMapping("/{postId}/like")
    public String likePost(@PathVariable Long postId, @RequestParam Long userId) {
        postService.likePost(postId, userId);
        return "Post Liked Successfully";
    }
}
