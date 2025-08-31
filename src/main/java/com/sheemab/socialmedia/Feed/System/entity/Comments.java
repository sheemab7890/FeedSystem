package com.sheemab.socialmedia.Feed.System.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "comments")
public class Comments {

    @Id
    private String commentId;     // Unique ID for the comment

    private String userId;               // Who commented
    private String text;                 // The comment content
    private String postId;               // ID of the post being commented on
    private LocalDateTime commentedAt;  // When it was commented
}

