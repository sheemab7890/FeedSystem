package com.sheemab.socialmedia.Feed.System.repository;

import com.sheemab.socialmedia.Feed.System.entity.Comments;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface CommentRepository extends ReactiveMongoRepository<Comments, String> {
    Flux<Comments> findByPostIdOrderByCommentedAtDesc(String postId);

    Mono<Long> countByPostId(String postId);

    Flux<Comments> findByPostId(String postId);
}
