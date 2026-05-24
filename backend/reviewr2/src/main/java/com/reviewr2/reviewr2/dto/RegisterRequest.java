package com.reviewr2.reviewr2.dto;

public record RegisterRequest(String username,
                              String email,
                              String password) {
}
