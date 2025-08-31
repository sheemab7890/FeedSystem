package com.sheemab.socialmedia.Feed.System.service;

import com.sheemab.socialmedia.Feed.System.dto.CommentResponseDto;
import com.sheemab.socialmedia.Feed.System.dto.LikeResponseDto;
import com.sheemab.socialmedia.Feed.System.dto.PostRequestDto;
import com.sheemab.socialmedia.Feed.System.dto.PostResponseDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IPostService {
    Mono<PostResponseDto> createPost(PostRequestDto postRequestDto);

    Mono<PostResponseDto> getPostById(String postId);

    Flux<PostResponseDto> getAllPosts();

    Mono<Void> deletePost(String postId);

    Mono<Integer> getLikeCount(String postId);

    Mono<Integer> getCommentCount(String postId);

    Flux<LikeResponseDto> getUsersThatLikePost(String postId);

    Flux<CommentResponseDto> getUsersThatCommentsOnPOst(String postId);
}
