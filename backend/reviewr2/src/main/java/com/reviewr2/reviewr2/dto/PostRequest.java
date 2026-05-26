package com.reviewr2.reviewr2.dto;

public record PostRequest(
        String title,
        String content,
        String code,        // nullable if it is just question
        String language,    // java, javascript, c, cpp
        String postType     // Snippet, Question
) {
}
