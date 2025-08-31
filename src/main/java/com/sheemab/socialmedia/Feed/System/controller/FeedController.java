package com.sheemab.socialmedia.Feed.System.controller;


import com.sheemab.socialmedia.Feed.System.dto.PostResponseDto;
import com.sheemab.socialmedia.Feed.System.service.Impl.FeedService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/feed")
@RequiredArgsConstructor
@Tag(name = "Feed", description = "Endpoints for fetching user feed")
public class FeedController {

    private final FeedService feedService;

    @Operation(
            summary = "Get user feed",
            description = "Returns a list of posts from users followed by the given user."
    )
    @GetMapping(value = "/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<PostResponseDto> getUserFeed(
            @Parameter(description = "ID of the user whose feed is to be fetched")
            @PathVariable String userId) {
        return feedService.getUserFeed(userId);
    }
}

