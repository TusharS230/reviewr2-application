package com.reviewr2.reviewr2.dto;

import java.time.LocalDateTime;

public record PostResponse(
        Long id,
        String title,
        String content,
        String code,
        String language,
        String postType,
        String authorUsername,      // we extract this from the User entity
        LocalDateTime createdAt
) {
}
