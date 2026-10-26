package io.github.arlindofernandes.shortlink_api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.github.arlindofernandes.shortlink_api.dto.CreateShortLinkRequest;
import io.github.arlindofernandes.shortlink_api.dto.CreateShortLinkResponse;
import io.github.arlindofernandes.shortlink_api.entity.ShortLink;
import io.github.arlindofernandes.shortlink_api.service.ShortLinkService;
import jakarta.validation.Valid;

@RestController 
@RequestMapping("/api/v1/short-links")
public class ShortLinkController {

    private final ShortLinkService shortLinkService;

    public ShortLinkController(ShortLinkService shortLinkService) {
        this.shortLinkService = shortLinkService;
    }

     @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreateShortLinkResponse create(
            @Valid @RequestBody CreateShortLinkRequest request
    ) {
        ShortLink shortLink = shortLinkService.create(request.originalUrl());

        String shortUrl = "http://localhost:8080/" + shortLink.getShortCode();

        return new CreateShortLinkResponse(
                shortLink.getId(),
                shortLink.getOriginalUrl(),
                shortLink.getShortCode(),
                shortUrl,
                shortLink.getCreatedAt()
        );
    }

}
