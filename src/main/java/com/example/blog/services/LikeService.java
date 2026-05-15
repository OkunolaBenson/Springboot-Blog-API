package com.example.blog.services;

import com.example.blog.exceptions.ResourceNotFoundException;
import com.example.blog.models.Like;
import com.example.blog.models.Post;
import com.example.blog.models.User;
import com.example.blog.repositories.LikeRepository;
import com.example.blog.repositories.PostRepository;
import com.example.blog.repositories.UserRepository;
import lombok.Data;
import org.springframework.stereotype.Service;

@Service
@Data
public class LikeService {
    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public void likePost(Long postId, Long userId) {
        if (likeRepository.findByUserIdAndPostId(userId, postId).isPresent()) {
            throw new RuntimeException("You already liked this post");
        }

        Post post = postRepository.findById(postId).orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Like like = Like.builder()
                .post(post)
                .user(user)
                .build();

        likeRepository.save(like);
    }
}
