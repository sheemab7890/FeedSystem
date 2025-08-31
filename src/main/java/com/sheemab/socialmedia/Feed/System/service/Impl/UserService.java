package com.sheemab.socialmedia.Feed.System.service.Impl;

import com.sheemab.socialmedia.Feed.System.dto.UserRequestDto;
import com.sheemab.socialmedia.Feed.System.dto.UserResponseDto;
import com.sheemab.socialmedia.Feed.System.entity.User;
import com.sheemab.socialmedia.Feed.System.repository.UserRepository;
import com.sheemab.socialmedia.Feed.System.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {

    private final UserRepository userRepository;  // Reactive repository for User
    private final ModelMapper modelMapper;        // Used to convert entity <-> DTO

    /**
     * Create a new user.
     * Steps:
     * 1. Check if user already exists by email
     * 2. If exists -> throw error
     * 3. If not -> save user and return UserResponseDto
     */
    @Override
    public Mono<UserResponseDto> createUser(UserRequestDto userRequestDto) {
        return userRepository.existsByEmail(userRequestDto.getEmail()) // returns Mono<Boolean>
                .doOnNext(val -> System.out.println("existsByEmail("
                        + userRequestDto.getEmail() + ") => " + val)) // for debugging/logging
                // flatMap() because we need to *conditionally* call another reactive method
                .flatMap(exists -> {
                    if (exists) {
                        // If email already exists, return an error
                        return Mono.error(new ResponseStatusException(
                                HttpStatus.CONFLICT, "User already exists with this email"));
                    }
                    // Otherwise, save the user
                    return userRepository.save(User.builder()
                                    .email(userRequestDto.getEmail())
                                    .username(userRequestDto.getUsername())
                                    .build()
                            )
                            // map() because we already have savedUser inside Mono and just want to transform it
                            .map(savedUser -> modelMapper.map(savedUser, UserResponseDto.class));
                });
    }

    /**
     * Follow another user.
     * - Prevent following yourself
     * - Fetch both users (follower + followee) using Mono.zip
     * - Update both sides (following + followers)
     * - Save both users
     */
    @Override
    public Mono<Void> follow(String followerId, String followeeId) {

        // Self-follow not allowed
        if (followerId.equals(followeeId)) {
            return Mono.error(new RuntimeException("You cannot follow yourself"));
        }

        // Mono.zip() -> Combine results of 2 Monos into a Tuple2 (like (follower, followee))
        return Mono.zip(getUser(followerId), getUser(followeeId))
                .flatMap(tuple -> {
                    User follower = tuple.getT1(); // first user
                    User followee = tuple.getT2(); // second user

                    // Prevent duplicate follow
                    if (follower.getFollowing().contains(followee.getId())) {
                        return Mono.empty(); // Already following, do nothing
                    }

                    // Add followee to follower's "following" list
                    follower.follow(followeeId);

                    // Add follower to followee's "followers" list
                    followee.addFollower(followerId);

                    // Save both users
                    // then() → used to ignore the result of save and move to next operation
                    return userRepository.save(follower)
                            .then(userRepository.save(followee))
                            .then(); // return Mono<Void>
                })
                .then(); // Final Mono<Void>
    }

    /**
     * Unfollow a user.
     * - Fetch both users (follower + followee)
     * - Remove links from both sides
     * - Save both
     */
    @Override
    public Mono<Void> unfollow(String followerId, String followeeId) {
        return Mono.zip(getUser(followerId), getUser(followeeId))
                .flatMap(tuple -> {
                    User follower = tuple.getT1();
                    User followee = tuple.getT2();

                    // Remove followee from follower's following list
                    follower.unfollow(followeeId);

                    // Remove follower from followee's followers list
                    followee.removeFollower(followerId);

                    // Save both
                    return userRepository.save(follower)
                            .then(userRepository.save(followee))
                            .then();
                })
                .then();
    }

    /**
     * Get all users that the given user is following.
     * Steps:
     * 1. Find the user by ID
     * 2. Extract their followingIds (Set<String>)
     * 3. Fetch those users by IDs -> returns Flux<User>
     * 4. Map to UserResponseDto
     */
    @Override
    public Flux<UserResponseDto> getFollowing(String userId) {
        return getUser(userId)
                .switchIfEmpty(Mono.error(new RuntimeException("User not found")))
                // flatMapMany() because following is a Set<String> (multiple values) -> convert to Flux
                .flatMapMany(user -> {
                    Set<String> followingIds = user.getFollowing();
                    return userRepository.findAllById(followingIds); // returns Flux<User>
                })
                .map(user -> modelMapper.map(user, UserResponseDto.class)); // map User -> DTO
    }

    /**
     * Get all followers of a user.
     * Very similar to getFollowing()
     */
    @Override
    public Flux<UserResponseDto> getFollowers(String userId) {
        return getUser(userId)
                .switchIfEmpty(Mono.error(new RuntimeException("User not found")))
                .flatMapMany(user -> {
                    Set<String> followingIds = user.getFollowers();
                    return userRepository.findAllById(followingIds);
                })
                .map(user -> modelMapper.map(user, UserResponseDto.class));
    }

    /**
     * Get follower count of a user.
     * map() is enough since we're just transforming User -> Integer
     */
    @Override
    public Mono<Integer> getFollowersCount(String userId) {
        return getUser(userId)
                .map(user -> user.getFollowers().size());
    }

    /**
     * Get following count of a user.
     */
    @Override
    public Mono<Integer> getFollowingCount(String userId) {
        return getUser(userId)
                .map(user -> user.getFollowing().size());
    }

    /**
     * Fetch user by ID.
     * If not found, throw error.
     */
    public Mono<User> getUser(String userId) {
        return userRepository.findById(userId)
                .switchIfEmpty(Mono.error(new RuntimeException("User not found: " + userId)));
    }
}

