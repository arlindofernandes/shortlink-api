
package io.github.arlindofernandes.shortlink_api.controller;

import java.net.URI;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import io.github.arlindofernandes.shortlink_api.entity.ShortLink;
import io.github.arlindofernandes.shortlink_api.service.ShortLinkService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Redirecionamento", description = "Operações de redirecionamento de links curtos")
public class RedirectController {

    private final ShortLinkService shortLinkService;

    public RedirectController(ShortLinkService shortLinkService) {
        this.shortLinkService = shortLinkService;
    }

    @Operation(summary = "Redirecionar para a URL original", description = "Busca o link pelo código curto e retorna "
            + "um redirecionamento para a URL original.")
    @ApiResponses({
            @ApiResponse(responseCode = "302", description = "Link encontrado; redirecionamento realizado.", headers = @Header(name = "Location", description = "URL original do link", schema = @Schema(type = "string", format = "uri"))),
            @ApiResponse(responseCode = "404", description = "Nenhum link foi encontrado para o código informado.")
    })
    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirect(
            @PathVariable String shortCode) {
        ShortLink shortLink = shortLinkService.findByShortCode(shortCode);

        return ResponseEntity
                .status(HttpStatus.FOUND)
                .location(URI.create(shortLink.getOriginalUrl()))
                .build();
    }
}
