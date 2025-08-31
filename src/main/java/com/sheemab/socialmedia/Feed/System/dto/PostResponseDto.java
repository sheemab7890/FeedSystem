package com.sheemab.socialmedia.Feed.System.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PostResponseDto {
    private String userId;   // who created this post
    private String content;  // text, image URL etc
    private LocalDateTime createdAt;
    private List<LikeResponseDto> likes ;  // number of likes
    private List<CommentResponseDto> comments ;   // number of comments
    private int likeCount;
    private int commentCount;
}
