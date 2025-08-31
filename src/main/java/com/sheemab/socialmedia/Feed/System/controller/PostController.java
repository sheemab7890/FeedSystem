package com.sheemab.socialmedia.Feed.System.controller;

import com.sheemab.socialmedia.Feed.System.dto.CommentResponseDto;
import com.sheemab.socialmedia.Feed.System.dto.LikeResponseDto;
import com.sheemab.socialmedia.Feed.System.dto.PostRequestDto;
import com.sheemab.socialmedia.Feed.System.dto.PostResponseDto;
import com.sheemab.socialmedia.Feed.System.service.IPostService;
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
@RequestMapping("/api/posts")
@RequiredArgsConstructor
@Tag(name = "Post API", description = "Operations related to creating, viewing, and managing posts")
public class PostController {

    private final IPostService postService;

    @Operation(summary = "Create a new post")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Post created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PostMapping
    public Mono<ResponseEntity<PostResponseDto>> createPost(@RequestBody PostRequestDto postRequestDto) {
        return postService.createPost(postRequestDto)
                .map(ResponseEntity::ok);
    }

    @Operation(summary = "Get a post by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Post found"),
            @ApiResponse(responseCode = "404", description = "Post not found")
    })
    @GetMapping("/{postId}")
    public Mono<ResponseEntity<PostResponseDto>> getPostById(@PathVariable String postId) {
        return postService.getPostById(postId)
                .map(ResponseEntity::ok);
    }

    @Operation(summary = "Get all posts")
    @ApiResponse(responseCode = "200", description = "Posts retrieved successfully")
    @GetMapping
    public Flux<PostResponseDto> getAllPosts() {
        return postService.getAllPosts();
    }

    @Operation(summary = "Delete a post by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Post deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Post not found")
    })
    @DeleteMapping("/{postId}")
    public Mono<ResponseEntity<Void>> deletePost(@PathVariable String postId) {
        return postService.deletePost(postId)
                .thenReturn(ResponseEntity.noContent().build());
    }

    @Operation(summary = "Get the like count of a post")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Like count retrieved"),
            @ApiResponse(responseCode = "404", description = "Post not found")
    })
    @GetMapping("/{postId}/likes/count")
    public Mono<Integer> getLikeCount(@PathVariable String postId) {
        return postService.getLikeCount(postId);
    }

    @Operation(summary = "Get the comment count of a post")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Comment count retrieved"),
            @ApiResponse(responseCode = "404", description = "Post not found")
    })
    @GetMapping("/{postId}/comments/count")
    public Mono<Integer> getCommentCount(@PathVariable String postId) {
        return postService.getCommentCount(postId);
    }

    @Operation(summary = "Get users who liked a post")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Users who liked the post retrieved"),
            @ApiResponse(responseCode = "404", description = "Post not found")
    })
    @GetMapping("/{postId}/likes/users")
    public Flux<LikeResponseDto> getLikedUsers(@PathVariable String postId) {
        return postService.getUsersThatLikePost(postId);
    }

    @Operation(summary = "Get users who commented on a post")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Comments retrieved"),
            @ApiResponse(responseCode = "404", description = "Post not found")
    })
    @GetMapping("/{postId}/comments/users")
    public Flux<CommentResponseDto> getCommentingUsers(@PathVariable String postId) {
        return postService.getUsersThatCommentsOnPOst(postId);
    }
}

