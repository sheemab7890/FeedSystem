package com.sheemab.socialmedia.Feed.System.service.Impl;

import com.sheemab.socialmedia.Feed.System.dto.CommentResponseDto;
import com.sheemab.socialmedia.Feed.System.dto.LikeResponseDto;
import com.sheemab.socialmedia.Feed.System.dto.PostRequestDto;
import com.sheemab.socialmedia.Feed.System.dto.PostResponseDto;
import com.sheemab.socialmedia.Feed.System.entity.Post;
import com.sheemab.socialmedia.Feed.System.repository.PostRepository;
import com.sheemab.socialmedia.Feed.System.repository.UserRepository;
import com.sheemab.socialmedia.Feed.System.service.IPostService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PostService implements IPostService {

    private final PostRepository postRepository;  // Reactive repository for Post entity
    private final ModelMapper modelMapper;        // Used to map entities <-> DTOs
    private final UserRepository userRepository;  // Repository to fetch User details

    /**
     * Create a new Post.
     * Steps:
     * 1. Build a Post object from PostRequestDto
     * 2. Save it in DB
     * 3. Map the saved entity to PostResponseDto
     */
    @Override
    public Mono<PostResponseDto> createPost(PostRequestDto postRequestDto) {
        Post post = Post.builder()
                .userId(postRequestDto.getUserId())
                .content(postRequestDto.getContent())
                .createdAt(LocalDateTime.now()) // explicitly set created time
                .build();

        // save() returns Mono<Post>
        return postRepository.save(post)
                // map() = transform Post into PostResponseDto (synchronous transformation)
                .map(saved -> modelMapper.map(saved, PostResponseDto.class));
    }

    /**
     * Fetch a post by ID.
     * - If not found, throw an error.
     * - Otherwise, map entity to DTO.
     */
    @Override
    public Mono<PostResponseDto> getPostById(String postId) {
        return postRepository.findById(postId) // returns Mono<Post>
                // If empty (no Post found), throw exception
                .switchIfEmpty(Mono.error(new RuntimeException("Post not found: " + postId)))
                // map() is used because we already have Post inside Mono.
                .map(post -> modelMapper.map(post, PostResponseDto.class));
    }

    /**
     * Fetch all posts.
     * - findAll() returns Flux<Post>
     * - map each Post to PostResponseDto
     */
    @Override
    public Flux<PostResponseDto> getAllPosts() {
        return postRepository.findAll()
                .map(post -> modelMapper.map(post, PostResponseDto.class));
    }

    /**
     * Delete a post by ID.
     * - First, check if the post exists (findById).
     * - If not found, throw error.
     * - If found, delete it.
     */
    @Override
    public Mono<Void> deletePost(String postId) {
        return postRepository.findById(postId)
                .switchIfEmpty(Mono.error(new RuntimeException("Post not found: " + postId)))
                // flatMap() = used when the next operation also returns a Mono/Flux
                // Here delete() returns Mono<Void>, so we use flatMap.
                .flatMap(existing -> postRepository.delete(existing));
    }

    /**
     * Get like count of a post.
     */
    @Override
    public Mono<Integer> getLikeCount(String postId) {
        return postRepository.findById(postId)
                .switchIfEmpty(Mono.error(new RuntimeException("Post not found: " + postId)))
                // map() is fine here since post.getLikes().size() is a direct value, not reactive.
                .map(post -> post.getLikes().size());
    }

    /**
     * Get comment count of a post.
     */
    @Override
    public Mono<Integer> getCommentCount(String postId) {
        return postRepository.findById(postId)
                .switchIfEmpty(Mono.error(new RuntimeException("Post not found: " + postId)))
                .map(post -> post.getComments().size());
    }

    /**
     * Get all users who liked a post.
     * Steps:
     * 1. Find Post by ID
     * 2. Convert its List<Like> into Flux<Like> using flatMapMany()
     * 3. For each Like, fetch User from UserRepository (returns Mono<User>)
     * 4. Use flatMap to convert each User into LikeResponseDto
     */
    @Override
    public Flux<LikeResponseDto> getUsersThatLikePost(String postId) {
        return postRepository.findById(postId)
                .switchIfEmpty(Mono.error(new RuntimeException("Post not found: " + postId)))
                // flatMapMany() = convert Mono<Post> into Flux<Like>
                // because post.getLikes() is a List<Like>, we convert it into Flux
                .flatMapMany(post -> Flux.fromIterable(post.getLikes()))
                // flatMap() = because findById returns Mono<User>
                .flatMap(like ->
                        userRepository.findById(like.getUserId())
                                .map(user -> LikeResponseDto.builder()
                                        .username(user.getUsername())
                                        .build()
                                )
                );
    }

    /**
     * Get all comments of a post with commenter details.
     * Steps:
     * 1. Find Post by ID
     * 2. Convert its List<Comment> into Flux<Comment>
     * 3. For each Comment, fetch the User who made it
     * 4. Build CommentResponseDto with username + comment text
     */
    @Override
    public Flux<CommentResponseDto> getUsersThatCommentsOnPOst(String postId) {
        return postRepository.findById(postId)
                .switchIfEmpty(Mono.error(new RuntimeException("Post not found: " + postId)))
                // flatMapMany() because comments is a List<Comment>
                .flatMapMany(post -> Flux.fromIterable(post.getComments()))
                // flatMap() because findById returns Mono<User>
                .flatMap(comment ->
                        userRepository.findById(comment.getUserId())
                                .map(user -> CommentResponseDto.builder()
                                        .username(user.getUsername())
                                        .text(comment.getText())
                                        .commentedAt(comment.getCommentedAt())
                                        .build()
                                )
                );
    }
}
