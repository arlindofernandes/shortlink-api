package io.github.arlindofernandes.shortlink_api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record  CreateShortLinkRequest (
    @NotBlank(message = "O URL original não pode ser nulo ou vazio.")
    @Size (max = 2048, message = "O URL original não pode exceder 2048 caracteres.")
    @Pattern (regexp = "^(https?://).+", message = "O URL original deve começar com http:// ou https://")
    String originalUrl
) {
}
