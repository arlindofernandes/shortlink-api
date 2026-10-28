package io.github.arlindofernandes.shortlink_api.controller;

import static org.hamcrest.Matchers.matchesRegex;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(
        properties = "APP_BASE_URL=https://links.example.test"
)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ShortLinkBaseUrlIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void create_whenBaseUrlIsConfigured_usesConfiguredDomain()
            throws Exception {

        String requestBody = """
                {
                    "originalUrl": "https://www.example.com/artigo"
                }
                """;

        mockMvc.perform(post("/api/v1/short-links")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.shortUrl")
                        .value(matchesRegex(
                                "https://links\\.example\\.test/[a-zA-Z0-9]{7}")));
    }
}