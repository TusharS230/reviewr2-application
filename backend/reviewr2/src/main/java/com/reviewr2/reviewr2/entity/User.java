package com.reviewr2.reviewr2.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(updatable = false)
    private String passwordHash;

    private String role = "USER";

    private int credibilityScore = 0;

    private String rankTier = "NOVICE";

    private boolean openToWork = false;

    private int strikes = 0;

    private boolean isShadowbanned = false;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}
