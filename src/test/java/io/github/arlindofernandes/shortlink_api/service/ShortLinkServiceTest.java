package io.github.arlindofernandes.shortlink_api.service;

import java.security.SecureRandom;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import io.github.arlindofernandes.shortlink_api.entity.ShortLink;
import io.github.arlindofernandes.shortlink_api.entity.ShortLinkRepository;
import io.github.arlindofernandes.shortlink_api.exception.ShortLinkNotFoundException;

@ExtendWith(MockitoExtension.class)
public class ShortLinkServiceTest {

        @Mock
        private ShortLinkRepository shortLinkRepository;
        @Mock
        private SecureRandom random;

        @InjectMocks
        private ShortLinkService shortLinkService;

        @Test
        void findByShortCode_whenCodeExists_returnsShortLink() {
                String shortCode = "abc1234";

                ShortLink expected = new ShortLink();
                expected.setShortCode(shortCode);
                expected.setOriginalUrl("https://www.google.com");

                when(shortLinkRepository.findByShortCode(shortCode))
                                .thenReturn(Optional.of(expected));

                ShortLink actual = shortLinkService.findByShortCode(shortCode);

                assertSame(expected, actual);
                verify(shortLinkRepository).findByShortCode(shortCode);
        }

        @Test
        void findByShortCode_whenCodeDoesNotExist_throwsException() {
                // Arrange
                String shortCode = "naoexiste";

                when(shortLinkRepository.findByShortCode(shortCode))
                                .thenReturn(Optional.empty());

                ShortLinkNotFoundException exception = assertThrows(
                                ShortLinkNotFoundException.class,
                                () -> shortLinkService.findByShortCode(shortCode));

                assertEquals(
                                "Link curto não encontrado: " + shortCode,
                                exception.getMessage());
                verify(shortLinkRepository).findByShortCode(shortCode);
        }

        @Test
        void create_generatesShortCodeAndSavesLink() {
                // Arrange
                String originalUrl = "https://www.google.com";

                when(random.nextInt(anyInt()))
                                .thenReturn(0, 1, 2, 3, 4, 5, 6);

                when(shortLinkRepository.existsByShortCode(any(String.class)))
                                .thenReturn(false);

                when(shortLinkRepository.save(any(ShortLink.class)))
                                .thenAnswer(invocation -> invocation.getArgument(0));

                // Act
                ShortLink actual = shortLinkService.create(originalUrl);

                // Assert
                assertEquals(originalUrl, actual.getOriginalUrl());
                assertNotNull(actual.getShortCode());
                assertEquals(7, actual.getShortCode().length());
                assertTrue(actual.getShortCode().matches("[a-zA-Z0-9]{7}"));

                verify(shortLinkRepository).save(actual);
        }

        @Test
        void create_whenCodeCollides_generatesAnotherCode() {
                // Arrange
                String originalUrl = "https://www.google.com";

                // Primeira tentativa: aaaaaaa
                // Segunda tentativa: bbbbbbb
                when(random.nextInt(anyInt()))
                                .thenReturn(
                                                0, 0, 0, 0, 0, 0, 0,
                                                1, 1, 1, 1, 1, 1, 1);

                when(shortLinkRepository.existsByShortCode("aaaaaaa"))
                                .thenReturn(true);

                when(shortLinkRepository.existsByShortCode("bbbbbbb"))
                                .thenReturn(false);

                when(shortLinkRepository.save(any(ShortLink.class)))
                                .thenAnswer(invocation -> invocation.getArgument(0));

                // Act
                ShortLink actual = shortLinkService.create(originalUrl);

                // Assert
                assertEquals("bbbbbbb", actual.getShortCode());
                assertEquals(originalUrl, actual.getOriginalUrl());

                verify(shortLinkRepository).existsByShortCode("aaaaaaa");
                verify(shortLinkRepository).existsByShortCode("bbbbbbb");
                verify(shortLinkRepository).save(actual);
        }

        @Test
        void create_whenAllCodesCollide_throwsException() {
                // Arrange
                when(random.nextInt(anyInt())).thenReturn(0);

                when(shortLinkRepository.existsByShortCode(any(String.class)))
                                .thenReturn(true);

                // Act + Assert
                assertThrows(
                                IllegalStateException.class,
                                () -> shortLinkService.create("https://www.google.com"));

                // O link não deve ser salvo
                verify(shortLinkRepository, org.mockito.Mockito.never())
                                .save(any(ShortLink.class));

                verify(shortLinkRepository, org.mockito.Mockito.times(5))
                                .existsByShortCode(any(String.class));
        }

}
