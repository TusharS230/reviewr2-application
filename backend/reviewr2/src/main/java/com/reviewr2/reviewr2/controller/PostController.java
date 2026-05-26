package com.reviewr2.reviewr2.controller;

import com.reviewr2.reviewr2.dto.PostRequest;
import com.reviewr2.reviewr2.dto.PostResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public class PostController {
    private final PostService postService;

    // POST /api/posts
    @PostMapping
    public ResponseEntity<PostResponse> createPost(@RequestBody PostRequestrequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(postService.createPost(request));
    }

    // GET  /api/posts
    @GetMapping
    public ResponseEntity<List<PostResponse>> getAllPosts() {
        return ResponseEntity.ok(postService.getAllPosts());
    }
}
