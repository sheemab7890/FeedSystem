package com.sheemab.socialmedia.Feed.System.service.Impl;

import com.sheemab.socialmedia.Feed.System.dto.CommentResponseDto;
import com.sheemab.socialmedia.Feed.System.dto.LikeResponseDto;
import com.sheemab.socialmedia.Feed.System.dto.PostResponseDto;
import com.sheemab.socialmedia.Feed.System.entity.Comments;
import com.sheemab.socialmedia.Feed.System.entity.Likes;
import com.sheemab.socialmedia.Feed.System.entity.User;
import com.sheemab.socialmedia.Feed.System.repository.CommentRepository;
import com.sheemab.socialmedia.Feed.System.repository.LikeRepository;
import com.sheemab.socialmedia.Feed.System.repository.PostRepository;
import com.sheemab.socialmedia.Feed.System.repository.UserRepository;
import com.sheemab.socialmedia.Feed.System.service.IFeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FeedService implements IFeedService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final LikeRepository likeRepository;
    private final CommentRepository commentRepository;


    public Flux<PostResponseDto> getUserFeed(String userId) {

        // Step 1: Find the user by ID (Mono<User>)
        return userRepository.findById(userId)

                // If no user is found, throw an error
                .switchIfEmpty(Mono.error(new RuntimeException("User not found: " + userId)))

                // Step 2: Once we get the user, move to processing their feed
                .flatMapMany(user -> {

                    // Get the list of user IDs that this user is following
                    Set<String> followingIds = user.getFollowing();

                    // If the user follows no one, return an empty feed
                    if (followingIds == null || followingIds.isEmpty()) {
                        return Flux.empty();
                    }

                    // Step 3: Fetch all posts created by users the current user follows
                    // Ordered by creation time descending
                    return postRepository.findByUserIdInOrderByCreatedAtDesc(followingIds)

                            // For each post, fetch likes and comments, and build the full PostResponseDto
                            .flatMap(post -> {

                                // Step 4a: Fetch all likes for the post as a list (Mono<List<Likes>>)
                                Mono<List<Likes>> likesMono = likeRepository.findByPostId(post.getId()).collectList();

                                // Step 4b: Fetch all comments for the post as a list (Mono<List<Comments>>)
                                Mono<List<Comments>> commentsMono = commentRepository.findByPostId(post.getId()).collectList();

                                // Step 5: Combine both likes and comments using Mono.zip
                                return Mono.zip(likesMono, commentsMono)

                                        // Once both likes and comments are available...
                                        .flatMap(tuple -> {
                                            List<Likes> likes = tuple.getT1();       // List of Likes for this post
                                            List<Comments> comments = tuple.getT2(); // List of Comments for this post

                                            // Step 6: Collect all userIds from likes and comments into a Set
                                            Set<String> userIds = new HashSet<>();
                                            likes.forEach(like -> userIds.add(like.getUserId()));
                                            comments.forEach(comment -> userIds.add(comment.getUserId()));

                                            // Step 7: Fetch all users involved in likes/comments in a single DB call
                                            return userRepository.findAllById(userIds)

                                                    // Step 8: Convert result to a Map<userId, username> for easy lookup
                                                    .collectMap(User::getId, User::getUsername)

                                                    // Step 9: Map likes/comments using the userId → username map
                                                    .map(userIdToUsernameMap -> {

                                                        // Step 9a: Build LikeResponseDto list
                                                        List<LikeResponseDto> likeDtos = likes.stream().map(like -> {
                                                            LikeResponseDto dto = new LikeResponseDto();
                                                            dto.setUsername(userIdToUsernameMap.getOrDefault(
                                                                    like.getUserId(), "Unknown"));
                                                            dto.setLikedAt(like.getLikedAt());
                                                            return dto;
                                                        }).collect(Collectors.toList());

                                                        // Step 9b: Build CommentResponseDto list
                                                        List<CommentResponseDto> commentDtos = comments.stream().map(comment -> {
                                                            CommentResponseDto dto = new CommentResponseDto();
                                                            dto.setUsername(userIdToUsernameMap.getOrDefault(
                                                                    comment.getUserId(), "Unknown"));
                                                            dto.setText(comment.getText());
                                                            dto.setCommentedAt(comment.getCommentedAt());
                                                            return dto;
                                                        }).collect(Collectors.toList());

                                                        // Step 10: Build the final PostResponseDto
                                                        PostResponseDto dto = new PostResponseDto();
                                                        dto.setUserId(post.getUserId());             // Creator of the post
                                                        dto.setContent(post.getContent());           // Post content
                                                        dto.setCreatedAt(post.getCreatedAt());       // Post creation time
                                                        dto.setLikes(likeDtos);                      // All likes with usernames
                                                        dto.setComments(commentDtos);                // All comments with usernames
                                                        dto.setLikeCount(likeDtos.size());           // Like count
                                                        dto.setCommentCount(commentDtos.size());     // Comment count

                                                        // Emit the completed DTO
                                                        return dto;
                                                    });
                                        });
                            });
                });
    }

}
