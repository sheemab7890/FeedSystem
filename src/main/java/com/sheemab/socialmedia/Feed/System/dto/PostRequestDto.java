package com.sheemab.socialmedia.Feed.System.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostRequestDto {
    private String userId;
    private String content;
}

