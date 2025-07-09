package com.example.urlshortener.controller;

import com.example.urlshortener.dto.CreateUrlRequest;
import com.example.urlshortener.dto.UrlResponse;
import com.example.urlshortener.entity.UrlMapping;
import com.example.urlshortener.service.UrlShortenerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UrlShortenerController.class)
class UrlShortenerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UrlShortenerService urlShortenerService;

    @Test
    void testShortenUrl() throws Exception {
        CreateUrlRequest request = new CreateUrlRequest("https://example.com", "testalias");
        UrlResponse response = new UrlResponse("testalias", "https://example.com", "http://localhost:8080/testalias");

        when(urlShortenerService.shortenUrl(anyString(), anyString())).thenReturn(response);

        mockMvc.perform(post("/shorten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.alias").value("testalias"));
    }

    @Test
    void testRedirectToFullUrl() throws Exception {
        UrlMapping mapping = new UrlMapping("alias1", "https://redirect.com", LocalDateTime.now());
        when(urlShortenerService.getUrlByAlias("alias1")).thenReturn(mapping);

        mockMvc.perform(get("/alias1"))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "https://redirect.com"));
    }

    @Test
    void testDeleteAlias() throws Exception {
        doNothing().when(urlShortenerService).deleteAlias("alias1");

        mockMvc.perform(delete("/alias1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testGetAllUrls() throws Exception {
        List<UrlResponse> responses = List.of(
                new UrlResponse("a1", "https://a.com", "http://localhost:8080/a1"),
                new UrlResponse("b2", "https://b.com", "http://localhost:8080/b2")
        );

        when(urlShortenerService.getAllUrls()).thenReturn(responses);

        mockMvc.perform(get("/urls"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].alias").value("a1"))
                .andExpect(jsonPath("$[1].alias").value("b2"));
    }
}