package com.sheemab.socialmedia.Feed.System.service.Impl;

import com.sheemab.socialmedia.Feed.System.dto.LikeResponseDto;
import com.sheemab.socialmedia.Feed.System.entity.Likes;
import com.sheemab.socialmedia.Feed.System.entity.Post;
import com.sheemab.socialmedia.Feed.System.entity.User;
import com.sheemab.socialmedia.Feed.System.repository.LikeRepository;
import com.sheemab.socialmedia.Feed.System.repository.PostRepository;
import com.sheemab.socialmedia.Feed.System.repository.UserRepository;
import com.sheemab.socialmedia.Feed.System.service.ILikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class LikeService implements ILikeService {

    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;


    /**
     * Add a like to a post by a user
     */
    @Override
    public Mono<LikeResponseDto> addLike(String postId, String userId) {
        // 1️ Check if the post exists
        Mono<Post> postMono = postRepository.findById(postId)
                .switchIfEmpty(Mono.error(new RuntimeException("Post not found: " + postId)));

        // 2️ Check if the user exists
        Mono<User> userMono = userRepository.findById(userId)
                .switchIfEmpty(Mono.error(new RuntimeException("User not found: " + userId)));

        // 3️ Combine both checks using zip
        return Mono.zip(postMono, userMono)
                .flatMap(tuple -> {
                    User user = tuple.getT2();

                    // 4️ Create Like entity
                    Likes like = new Likes();
                    like.setUserId(userId);
                    like.setPostId(postId);
                    like.setLikedAt(LocalDateTime.now());

                    // 5️ Save Like and convert to DTO
                    return likeRepository.save(like)
                            .map(saved -> {
                                LikeResponseDto dto = new LikeResponseDto();
                                dto.setUsername(user.getUsername());
                                dto.setLikedAt(saved.getLikedAt());
                                return dto;
                            });
                });
    }

    /**
     * Remove a like by a user on a post
     */
    @Override
    public Mono<Void> removeLike(String postId, String userId) {
        return likeRepository.findByPostIdAndUserId(postId, userId)
                .switchIfEmpty(Mono.error(new RuntimeException("Like not found for post: " + postId)))
                .flatMap(likeRepository::delete); // flatMap because delete() returns Mono<Void>
    }

    /**
     * Get all likes for a post
     */
    @Override
    public Flux<LikeResponseDto> getLikesByPost(String postId) {
        return likeRepository.findByPostId(postId)
                .flatMap(like ->
                        userRepository.findById(like.getUserId())
                                .map(user -> {
                                    LikeResponseDto dto = new LikeResponseDto();
                                    dto.setUsername(user.getUsername());
                                    dto.setLikedAt(like.getLikedAt());
                                    return dto;
                                })
                );
    }

    /**
     * Get the count of likes for a post
     */
    @Override
    public Mono<Integer> countLikes(String postId) {
        return likeRepository.countByPostId(postId);
    }

    /**
     * Check if a user has liked a post
     */
    @Override
    public Mono<Boolean> hasUserLiked(String postId, String userId) {
        return likeRepository.findByPostIdAndUserId(postId, userId)
                .map(like -> true)     // If found → true
                .defaultIfEmpty(false); // If empty → false
    }
}

