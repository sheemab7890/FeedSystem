package com.sheemab.socialmedia.Feed.System.entity;

import jdk.jfr.DataAmount;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;
import java.util.Set;

@Document(collection = "users")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class User {

    @Id
    private String id;
    private String username;
    private String email;

    @Builder.Default
    private Set<String> followers = new HashSet<>();

    @Builder.Default
    private Set<String> following = new HashSet<>();
    //By default, Lombok ignores these initial values when building the object, unless you explicitly tell it to keep them.


    // --- helper methods ---
    public void follow(String userId) {
        following.add(userId);
    }

    public void addFollower(String userId) {
        followers.add(userId);
    }

    public void unfollow(String userId) {
        following.remove(userId);
    }

    public void removeFollower(String userId) {
       followers.remove(userId);
    }
}
