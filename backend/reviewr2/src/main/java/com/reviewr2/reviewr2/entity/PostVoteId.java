package com.reviewr2.reviewr2.entity;

import jakarta.persistence.Embeddable;
import jakarta.persistence.SequenceGenerator;
import lombok.*;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class PostVoteId {

    private Long userId;
    private Long postId;
}
