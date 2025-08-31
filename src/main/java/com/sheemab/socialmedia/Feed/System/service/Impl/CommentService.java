package com.sheemab.socialmedia.Feed.System.service.Impl;

import com.sheemab.socialmedia.Feed.System.dto.CommentRequestDto;
import com.sheemab.socialmedia.Feed.System.dto.CommentResponseDto;
import com.sheemab.socialmedia.Feed.System.entity.Comments;
import com.sheemab.socialmedia.Feed.System.entity.Post;
import com.sheemab.socialmedia.Feed.System.entity.User;
import com.sheemab.socialmedia.Feed.System.repository.CommentRepository;
import com.sheemab.socialmedia.Feed.System.repository.PostRepository;
import com.sheemab.socialmedia.Feed.System.repository.UserRepository;
import com.sheemab.socialmedia.Feed.System.service.ICommentService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
public class CommentService implements ICommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    /**
     * Add a new comment to a post
     * Flow:
     * 1. First check if the post exists
     * 2. Then check if the user exists
     * 3. Create comment object and save to DB
     * 4. Map the saved comment + user info into a response DTO
     */
    @Override
    public Mono<CommentResponseDto> addComment(String postId, CommentRequestDto dto) {
        // Step 1: Fetch the post from DB (if not found, return error)
        Mono<Post> postMono = postRepository.findById(postId)
                .switchIfEmpty(Mono.error(new RuntimeException("Post not found: " + postId)));

        // Step 2: Fetch the user from DB (if not found, return error)
        Mono<User> userMono = userRepository.findById(dto.getUserId())
                .switchIfEmpty(Mono.error(new RuntimeException("User not found: " + dto.getUserId())));

        // Step 3: Combine both Monos (post + user) into one using zip
        // - Mono.zip waits until BOTH Monos emit their values
        // - It gives a Tuple (pair of values) => tuple.getT1() = Post, tuple.getT2() = User
        return Mono.zip(postMono, userMono)
                .flatMap(tuple -> {
                    // Extract user (we don't actually use Post further, but it's validated by now)
                    User user = tuple.getT2();

                    // Step 4: Build the new Comment object
                    Comments comment = new Comments();
                    comment.setUserId(dto.getUserId());        // link to user
                    comment.setText(dto.getText());            // actual comment text
                    comment.setCommentedAt(LocalDateTime.now());// timestamp when comment is created
                    comment.setPostId(postId);                 // link to post

                    // Step 5: Save the comment to DB
                    return commentRepository.save(comment)
                            // Step 6: After saving, map the saved entity to a DTO for response
                            .map(saved -> new CommentResponseDto(
                                    user.getUsername(),        // take username from User
                                    saved.getText(),           // take text from saved comment
                                    saved.getCommentedAt()     // take timestamp from saved comment
                            ));
                });
    }

    /**
     * Fetch all comments for a given post, sorted by time (latest first)
     * Flow:
     * 1. Get all comments for post (Flux<Comments>)
     * 2. For each comment, fetch user info
     * 3. Build CommentResponseDto for each
     */
    @Override
    public Flux<CommentResponseDto> getCommentsByPost(String postId) {
        return commentRepository.findByPostIdOrderByCommentedAtDesc(postId) // Flux<Comments>
                .flatMap(comment -> // flatMapMany is not needed since findByPostId already returns Flux
                        userRepository.findById(comment.getUserId()) // get user for each comment
                                .map(user -> { // map -> just transforming data
                                    CommentResponseDto dto = new CommentResponseDto();
                                    dto.setUsername(user.getUsername());
                                    dto.setText(comment.getText());
                                    dto.setCommentedAt(comment.getCommentedAt());
                                    return dto;
                                })
                );
    }

    /**
     * Get total number of comments on a post
     * Mono<Long> -> map to Mono<Integer>
     */
    @Override
    public Mono<Integer> getCommentCount(String postId) {
        return commentRepository.countByPostId(postId) // Mono<Long>
                .map(Long::intValue); // map -> transform Long to Integer
    }

    /**
     * Delete a comment, but only if the requesting user is the owner
     */
    @Override
    public Mono<Void> deleteComment(String userId, String commentId) {
        return commentRepository.findById(commentId) // find comment
                .switchIfEmpty(Mono.error(new RuntimeException("Comment not found: " + commentId)))
                .flatMap(comment -> {
                    // Validate ownership
                    if (!comment.getUserId().equals(userId)) {
                        return Mono.error(new RuntimeException("You can only delete your own comments!"));
                    }
                    // Delete the comment
                    return commentRepository.delete(comment);
                });
    }
}



