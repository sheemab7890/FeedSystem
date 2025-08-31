package com.sheemab.socialmedia.Feed.System.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LikeResponseDto {
    private String username;
    private LocalDateTime likedAt;
}

