package com.example.blog.services;

import com.example.blog.DTO.PostRequest;
import com.example.blog.DTO.PostResponse;
import com.example.blog.enums.PostStatus;
import com.example.blog.exceptions.ResourceNotFoundException;
import com.example.blog.models.Post;
import com.example.blog.models.User;
import com.example.blog.repositories.PostRepository;
import com.example.blog.repositories.UserRepository;
import lombok.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public PostResponse createPost(PostRequest request, Long userId) {
        try {
            String fileName = UUID.randomUUID() + "_" + request.getImage().getOriginalFilename();
            Path path = Paths.get("uploads/" + fileName);
            Files.copy(request.getImage().getInputStream(), path);
            User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
            Post post = Post.builder()
                    .title(request.getTitle())
                    .content(request.getContent())
                    .imageUrl(fileName)
                    .status(PostStatus.PUBLISHED)
                    .createdAt(LocalDateTime.now())
                    .user(user)
                    .build();
            Post savedPost = postRepository.save(post);
            return mapToResponse(savedPost);
        } catch (IOException e) {
            throw new RuntimeException("Image Upload Failed");
        }
    }

    public Page<PostResponse> getAllPosts(Pageable pageable) {
        return postRepository.findAll(pageable).map(this::mapToResponse);
    }

    public PostResponse getPostById(Long id) {
        Post post = postRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Post not found"));
        return mapToResponse(post);
    }

    public PostResponse updatePost(Long id, PostRequest postRequest) {
        try {
            Post post = postRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Post not found"));

            post.setTitle(postRequest.getTitle());
            post.setContent(postRequest.getContent());
            post.setUpdatedAt(LocalDateTime.now());

            if(postRequest.getImage() != null && !postRequest.getImage().isEmpty()) {
                String fileName = saveImage(postRequest);
                post.setImageUrl(fileName);
            }

            Post updatedPost = postRepository.save(post);
            return mapToResponse(updatedPost);
        } catch (IOException e) {
            throw new RuntimeException("Image Upload Failed");
        }
    }

    public void deletePost(Long id) {
        Post post = postRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Post not found"));
        postRepository.delete(post);
    }

    public void likePost(Long id, Long userId) {
        Post post = postRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Post not found"));
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        if (!post.getLikedByUsers().contains(user)) {
            post.getLikedByUsers().add(user);
        }

        postRepository.save(post);
    }
    
    private String saveImage(PostRequest request) throws IOException {
        String fileName = UUID.randomUUID() + "_" + request.getImage().getOriginalFilename();
        Path path = Paths.get("uploads");
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }

        Path filePath = path.resolve(fileName);
        Files.copy(
                request.getImage().getInputStream(),
                filePath,
                StandardCopyOption.REPLACE_EXISTING
        );

        return fileName;
    }

    public PostResponse mapToResponse(Post post) {
        return PostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .imageUrl(post.getImageUrl())
                .author(post.getUser() != null ? post.getUser().getName() : null)
                .createdAt(post.getCreatedAt())
                .status(post.getStatus())
                .likes(post.getLikedByUsers().size())
                .build();
    }
    

}
