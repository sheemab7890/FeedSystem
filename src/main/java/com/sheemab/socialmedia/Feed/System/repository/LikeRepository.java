package com.sheemab.socialmedia.Feed.System.repository;

import com.sheemab.socialmedia.Feed.System.entity.Likes;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface LikeRepository extends ReactiveMongoRepository<Likes, String> {
    Mono<Likes> findByPostIdAndUserId(String postId, String userId);

    Flux<Likes> findByPostId(String postId);

    Mono<Integer> countByPostId(String postId);

}
