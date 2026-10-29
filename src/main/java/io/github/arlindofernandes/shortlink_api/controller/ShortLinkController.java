package io.github.arlindofernandes.shortlink_api.controller;

import org.springframework.beans.factory.annotation.Value;
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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@Tag(name = "Links curtos", description = "Operações para criar links curtos")
@RequestMapping("/api/v1/short-links")
public class ShortLinkController {

    private final ShortLinkService shortLinkService;
    private final String baseUrl;

    public ShortLinkController(ShortLinkService shortLinkService, @Value("${app.base-url}") String baseUrl) {
        this.shortLinkService = shortLinkService;
        this.baseUrl = baseUrl;
    }

    @Operation(summary = "Criar link curto", description = "Recebe uma URL original, gera um código curto "
            + "e retorna os dados do link criado.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Link curto criado com sucesso.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CreateShortLinkResponse.class))),
            @ApiResponse(responseCode = "400", description = "Requisição inválida ou URL original inválida.")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreateShortLinkResponse create(
            @Valid @RequestBody CreateShortLinkRequest request) {
        ShortLink shortLink = shortLinkService.create(request.originalUrl());

        String shortUrl = baseUrl.replaceAll("/+$", "")
                + "/" + shortLink.getShortCode();

        return new CreateShortLinkResponse(
                shortLink.getId(),
                shortLink.getOriginalUrl(),
                shortLink.getShortCode(),
                shortUrl,
                shortLink.getCreatedAt());
    }

}
