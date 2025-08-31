package com.sheemab.socialmedia.Feed.System.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "likes")
public class Likes {
    @Id
    private String likeId;        // Unique ID for the like (Mongo will auto-generate if null)

    private String postId;        // Post that was liked
    private String userId;        // Who liked the post

    @CreatedDate
    private LocalDateTime likedAt;// When it was liked
}
