package com.reviewr2.reviewr2.dto;

public record AuthenticationResponse(String token,
                                     String username,
                                     String rankTier) {
}
