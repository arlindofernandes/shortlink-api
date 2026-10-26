package io.github.arlindofernandes.shortlink_api.service;

import java.security.SecureRandom;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.arlindofernandes.shortlink_api.entity.ShortLink;
import io.github.arlindofernandes.shortlink_api.entity.ShortLinkRepository;
import io.github.arlindofernandes.shortlink_api.exception.ShortLinkNotFoundException;

@Service
public class ShortLinkService {

    private static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int CODE_LENGTH = 7;
    private static final int MAX_ATTEMPTS = 5;

    private final ShortLinkRepository shortLinkRepository;
    private final SecureRandom random = new SecureRandom();

    public ShortLinkService(ShortLinkRepository shortLinkRepository) {
        this.shortLinkRepository = shortLinkRepository;
    }

    @Transactional
    public ShortLink create(String originalUrl) {
        String shortCode = generateUniqueCode();

        ShortLink shortLink = new ShortLink();
        shortLink.setOriginalUrl(originalUrl);
        shortLink.setShortCode(shortCode);

        return shortLinkRepository.save(shortLink);
    }

    private String generateUniqueCode() {
        for (int attempt = 0; attempt < MAX_ATTEMPTS; attempt++) {
            StringBuilder code = new StringBuilder(CODE_LENGTH);

            for (int i = 0; i < CODE_LENGTH; i++) {
                code.append(CHARACTERS.charAt(
                        random.nextInt(CHARACTERS.length())));
            }

            if (!shortLinkRepository.existsByShortCode(code.toString())) {
                return code.toString();
            }
        }

        throw new IllegalStateException(
                "Não foi possível gerar um código curto único.");
    }

    @Transactional(readOnly = true)
    public ShortLink findByShortCode(String shortCode) {
        return shortLinkRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new ShortLinkNotFoundException(shortCode));
    }
}
