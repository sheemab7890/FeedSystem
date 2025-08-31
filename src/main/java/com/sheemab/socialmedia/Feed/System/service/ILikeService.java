package com.sheemab.socialmedia.Feed.System.service;

import com.sheemab.socialmedia.Feed.System.dto.LikeResponseDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ILikeService {
    Mono<LikeResponseDto> addLike(String postId, String userId);

    Mono<Void> removeLike(String postId, String userId);

    Flux<LikeResponseDto> getLikesByPost(String postId);

    Mono<Integer> countLikes(String postId);

    Mono<Boolean> hasUserLiked(String postId, String userId);
}
