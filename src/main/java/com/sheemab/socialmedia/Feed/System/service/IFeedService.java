package com.sheemab.socialmedia.Feed.System.service;

import com.sheemab.socialmedia.Feed.System.dto.PostResponseDto;
import reactor.core.publisher.Flux;

public interface IFeedService {
    Flux<PostResponseDto> getUserFeed(String userId);
}
