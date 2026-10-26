package io.github.arlindofernandes.shortlink_api.dto;

import java.time.Instant;

public record CreateShortLinkResponse(
    Long id,
    String originalUrl,
    String shortCode,
    String shortUrl,
    Instant createdAt
) {
}
