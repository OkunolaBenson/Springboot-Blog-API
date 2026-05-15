package com.example.blog.repositories;

import com.example.blog.enums.PostStatus;
import com.example.blog.models.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findByStatus(PostStatus status, Pageable pageable);
}
