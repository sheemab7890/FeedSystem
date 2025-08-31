package com.sheemab.socialmedia.Feed.System.controller;

import com.sheemab.socialmedia.Feed.System.dto.LikeResponseDto;
import com.sheemab.socialmedia.Feed.System.service.Impl.LikeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/likes")
@RequiredArgsConstructor
@Tag(name = "Like API", description = "Endpoints for managing likes on posts")
public class LikeController {

    private final LikeService likeService;

    @Operation(summary = "Add a like to a post")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Like added successfully"),
            @ApiResponse(responseCode = "404", description = "Post or user not found")
    })
    @PostMapping("/{postId}/users/{userId}")
    public Mono<ResponseEntity<LikeResponseDto>> addLike(
            @PathVariable String postId,
            @PathVariable String userId
    ) {
        return likeService.addLike(postId, userId)
                .map(ResponseEntity::ok);
    }

    @Operation(summary = "Remove a like from a post by a user")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Like removed successfully"),
            @ApiResponse(responseCode = "404", description = "Like not found")
    })
    @DeleteMapping("/{postId}/users/{userId}")
    public Mono<ResponseEntity<Void>> removeLike(
            @PathVariable String postId,
            @PathVariable String userId
    ) {
        return likeService.removeLike(postId, userId)
                .thenReturn(ResponseEntity.noContent().build());
    }

    @Operation(summary = "Get all likes for a post")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of likes retrieved")
    })
    @GetMapping("/{postId}")
    public Flux<LikeResponseDto> getLikesByPost(
            @PathVariable String postId
    ) {
        return likeService.getLikesByPost(postId);
    }

    @Operation(summary = "Get total like count for a post")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Like count retrieved")
    })
    @GetMapping("/{postId}/count")
    public Mono<Integer> countLikes(
            @PathVariable String postId
    ) {
        return likeService.countLikes(postId);
    }

    @Operation(summary = "Check if a user has liked a post")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "True if user has liked the post")
    })
    @GetMapping("/{postId}/users/{userId}/has-liked")
    public Mono<Boolean> hasUserLiked(
            @PathVariable String postId,
            @PathVariable String userId
    ) {
        return likeService.hasUserLiked(postId, userId);
    }
}

