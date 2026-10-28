package io.github.arlindofernandes.shortlink_api.controller;

import static org.hamcrest.Matchers.matchesRegex;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ShortLinkControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void create_withValidUrl_returnsCreatedResponse() throws Exception {
        String requestBody = """
                {
                    "originalUrl": "https://www.example.com/artigo"
                }
                """;

        mockMvc.perform(post("/api/v1/short-links")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.originalUrl")
                        .value("https://www.example.com/artigo"))
                .andExpect(jsonPath("$.shortCode")
                        .value(matchesRegex("[a-zA-Z0-9]{7}")))
                .andExpect(jsonPath("$.shortUrl")
                        .value(matchesRegex(
                                "http://localhost:8080/[a-zA-Z0-9]{7}")))
                .andExpect(jsonPath("$.createdAt").isNotEmpty());
    }

    @Test
    void create_withBlankUrl_returnsBadRequest() throws Exception {
        String requestBody = """
                {
                    "originalUrl": ""
                }
                """;

        mockMvc.perform(post("/api/v1/short-links")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_withInvalidUrl_returnsBadRequest() throws Exception {
        String requestBody = """
                {
                    "originalUrl": "ftp://example.com"
                }
                """;

        mockMvc.perform(post("/api/v1/short-links")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void redirect_whenCodeExists_returnsFoundWithLocation() throws Exception {
        String shortCode = "test123";
        String originalUrl = "https://www.example.com/artigo";

        jdbcTemplate.update(
                "INSERT INTO short_links (original_url, short_code) VALUES (?, ?)",
                originalUrl,
                shortCode);

        mockMvc.perform(get("/" + shortCode))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", originalUrl));
    }

    @Test
    void redirect_whenCodeDoesNotExist_returnsNotFound() throws Exception {
        mockMvc.perform(get("/codigoInexistente"))
                .andExpect(status().isNotFound());
    }

    @AfterEach
    void cleanUp() {
        jdbcTemplate.update(
                "DELETE FROM short_links WHERE short_code = ?",
                "test123");
    }

}