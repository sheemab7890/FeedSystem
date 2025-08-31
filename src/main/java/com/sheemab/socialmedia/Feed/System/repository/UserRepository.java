package com.sheemab.socialmedia.Feed.System.repository;

import com.sheemab.socialmedia.Feed.System.entity.User;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Repository
public interface UserRepository extends ReactiveMongoRepository<User, String> {

    Mono<Boolean> existsByEmail(String email);
    Mono<User> findById(String userId);
    Flux<User> findAllById(Iterable<String> userIds);

}
