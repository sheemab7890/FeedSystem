package com.sheemab.socialmedia.Feed.System.controller;

import com.sheemab.socialmedia.Feed.System.dto.CommentRequestDto;
import com.sheemab.socialmedia.Feed.System.dto.CommentResponseDto;
import com.sheemab.socialmedia.Feed.System.service.Impl.CommentService;
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
@RequestMapping("/api/comments")
@RequiredArgsConstructor
@Tag(name = "Comment API", description = "Operations related to commenting on posts")
public class CommentController {

    private final CommentService commentService;

    @Operation(summary = "Add a comment to a post")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Comment added successfully"),
            @ApiResponse(responseCode = "404", description = "Post or User not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping("/{postId}")
    public Mono<ResponseEntity<CommentResponseDto>> addComment(
            @PathVariable String postId,
            @RequestBody CommentRequestDto commentRequestDto
    ) {
        return commentService.addComment(postId, commentRequestDto)
                .map(ResponseEntity::ok);
    }

    @Operation(summary = "Get all comments on a post")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Comments retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Post not found")
    })
    @GetMapping("/{postId}")
    public Flux<CommentResponseDto> getCommentsByPost(@PathVariable String postId) {
        return commentService.getCommentsByPost(postId);
    }

    @Operation(summary = "Get comment count on a post")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Comment count retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Post not found")
    })
    @GetMapping("/{postId}/count")
    public Mono<Integer> getCommentCount(@PathVariable String postId) {
        return commentService.getCommentCount(postId);
    }

    @Operation(summary = "Delete a comment (only by owner)")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Comment deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Comment not found"),
            @ApiResponse(responseCode = "403", description = "User not authorized to delete this comment")
    })
    @DeleteMapping("/{commentId}")
    public Mono<ResponseEntity<Void>> deleteComment(
            @RequestParam String userId,
            @PathVariable String commentId
    ) {
        return commentService.deleteComment(userId, commentId)
                .thenReturn(ResponseEntity.noContent().build());
    }
}

