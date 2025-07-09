package com.example.urlshortener.ittest;

import com.example.urlshortener.dto.CreateUrlRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UrlShortenerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreateAndRedirectUrl() throws Exception {
        CreateUrlRequest request = new CreateUrlRequest("https://example.com", "myalias");

        mockMvc.perform(post("/shorten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.shortUrl").value("http://localhost:8080/myalias"));

        mockMvc.perform(get("/myalias"))
                .andExpect(status().isFound());
    }

    @Test
    void testGetAllUrls() throws Exception {
        CreateUrlRequest request = new CreateUrlRequest("https://a.com", "a1");

        mockMvc.perform(post("/shorten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/urls"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].alias").value("a1"));
    }

    @Test
    void testDeleteAlias() throws Exception {
        CreateUrlRequest request = new CreateUrlRequest("https://to-delete.com", "delete1");

        mockMvc.perform(post("/shorten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        mockMvc.perform(delete("/delete1"))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/delete1"))
                .andExpect(status().isNotFound());
    }
}
