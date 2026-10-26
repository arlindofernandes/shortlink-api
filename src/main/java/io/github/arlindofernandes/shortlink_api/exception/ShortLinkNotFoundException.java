package io.github.arlindofernandes.shortlink_api.exception;

public class ShortLinkNotFoundException extends RuntimeException {

    public ShortLinkNotFoundException(String shortCode) {
        super("Link curto não encontrado: " + shortCode);
    }
}
