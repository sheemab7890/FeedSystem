package com.sheemab.socialmedia.Feed.System.controller;

import com.sheemab.socialmedia.Feed.System.dto.UserRequestDto;
import com.sheemab.socialmedia.Feed.System.dto.UserResponseDto;
import com.sheemab.socialmedia.Feed.System.service.Impl.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User API", description = "Operations related to users and following relationships")
public class UserController {

    private final UserService userService;

    @Operation(summary = "Create a new user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User created successfully"),
            @ApiResponse(responseCode = "409", description = "User already exists")
    })
    @PostMapping
    public Mono<ResponseEntity<UserResponseDto>> createUser(
            @RequestBody @Parameter(description = "User details to create") UserRequestDto requestDto) {
        return userService.createUser(requestDto)
                .map(ResponseEntity::ok);
    }

    @Operation(summary = "Follow a user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Followed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid follow request")
    })
    @PostMapping("/{followerId}/follow/{followeeId}")
    public Mono<ResponseEntity<Void>> followUser(
            @PathVariable String followerId,
            @PathVariable String followeeId) {
        return userService.follow(followerId, followeeId)
                .thenReturn(ResponseEntity.ok().build());
    }

    @Operation(summary = "Unfollow a user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Unfollowed successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PostMapping("/{followerId}/unfollow/{followeeId}")
    public Mono<ResponseEntity<Void>> unfollowUser(
            @PathVariable String followerId,
            @PathVariable String followeeId) {
        return userService.unfollow(followerId, followeeId)
                .thenReturn(ResponseEntity.ok().build());
    }

    @Operation(summary = "Get following of this user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of following users")
    })
    @GetMapping("/{userId}/following")
    public Flux<UserResponseDto> getFollowing(@PathVariable String userId) {
        return userService.getFollowing(userId);
    }

    @Operation(summary = "Get followers of this user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of followers")
    })
    @GetMapping("/{userId}/followers")
    public Flux<UserResponseDto> getFollowers(@PathVariable String userId) {
        return userService.getFollowers(userId);
    }

    @Operation(summary = "Get count of followers")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Number of followers")
    })
    @GetMapping("/{userId}/followers/count")
    public Mono<Integer> getFollowersCount(@PathVariable String userId) {
        return userService.getFollowersCount(userId);
    }

    @Operation(summary = "Get count of following")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Number of users being followed")
    })
    @GetMapping("/{userId}/following/count")
    public Mono<Integer> getFollowingCount(@PathVariable String userId) {
        return userService.getFollowingCount(userId);
    }
}

