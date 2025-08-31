package com.sheemab.socialmedia.Feed.System.service;

import com.sheemab.socialmedia.Feed.System.dto.UserRequestDto;
import com.sheemab.socialmedia.Feed.System.dto.UserResponseDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Set;

public interface IUserService {

    Mono<UserResponseDto> createUser(UserRequestDto userRequestDto);

    Mono<Void> follow(String followerId, String followeeId);

    Mono<Void> unfollow(String followerId, String followeeId);

    Flux<UserResponseDto> getFollowing(String userId);

    Flux<UserResponseDto> getFollowers(String userId);

    Mono<Integer> getFollowersCount(String userId);

    Mono<Integer> getFollowingCount(String userId);

}
