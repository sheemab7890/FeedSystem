package com.sheemab.socialmedia.Feed.System.repository;

import com.sheemab.socialmedia.Feed.System.entity.Post;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

import java.util.Set;

public interface PostRepository extends ReactiveMongoRepository<Post, String> {
    // fetch all posts for given users (followers)
    Flux<Post> findByUserIdInOrderByCreatedAtDesc(Set<String> userIds);
}
