package com.sheemab.socialmedia.Feed.System.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Document(collection = "posts")
public class Post {

    @Id
    private String id;
    private String userId;   // who created this post
    private String content;  // text, image URL etc

    @CreatedDate
    private LocalDateTime createdAt;

    private List<Likes> likes;  // number of likes

    private List<Comments> comments;   // number of comments

    private int likeCount;
    private int commentCount;

}
