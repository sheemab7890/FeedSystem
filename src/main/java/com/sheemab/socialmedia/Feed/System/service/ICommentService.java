package com.sheemab.socialmedia.Feed.System.service;

import com.sheemab.socialmedia.Feed.System.dto.CommentRequestDto;
import com.sheemab.socialmedia.Feed.System.dto.CommentResponseDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


public interface ICommentService {

        Mono<CommentResponseDto> addComment(String postId, CommentRequestDto commentRequestDto);

        Flux<CommentResponseDto> getCommentsByPost(String postId);

        Mono<Integer> getCommentCount(String postId);

        Mono<Void> deleteComment(String userId, String commentId);

}

