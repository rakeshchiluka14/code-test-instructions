package com.example.urlshortener.service;

import com.example.urlshortener.dto.UrlResponse;
import com.example.urlshortener.entity.UrlMapping;
import com.example.urlshortener.repository.UrlMappingRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UrlShortenerServiceTest {

    @Mock
    private UrlMappingRepository urlMappingRepository;

    @InjectMocks
    private UrlShortenerService urlShortenerService;

    @Test
    void testShortenUrlWithCustomAlias() {
        String fullUrl = "https://example.com";
        String alias = "customAlias";

        when(urlMappingRepository.existsByAlias(alias)).thenReturn(false);
        when(urlMappingRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        UrlResponse result = urlShortenerService.shortenUrl(fullUrl, alias);

        assertThat(result.alias()).isEqualTo(alias);
        assertThat(result.fullUrl()).isEqualTo(fullUrl);
        assertThat(result.shortUrl()).endsWith("/" + alias);
    }

    @Test
    void testShortenUrlWithDuplicateAliasThrowsException() {
        when(urlMappingRepository.existsByAlias("takenAlias")).thenReturn(true);
        assertThrows(IllegalArgumentException.class, () ->
                urlShortenerService.shortenUrl("https://x.com", "takenAlias"));
    }

    @Test
    void testGetAllUrls() {
        when(urlMappingRepository.findAll()).thenReturn(List.of(
                new UrlMapping("a1", "https://a.com", LocalDateTime.now()),
                new UrlMapping("b2", "https://b.com", LocalDateTime.now())
        ));

        List<UrlResponse> results = urlShortenerService.getAllUrls();
        assertThat(results).hasSize(2);
    }

    @Test
    void testGetUrlByAliasFound() {
        UrlMapping mapping = new UrlMapping("alias1", "https://example.com", LocalDateTime.now());
        when(urlMappingRepository.findByAlias("alias1")).thenReturn(Optional.of(mapping));

        UrlMapping result = urlShortenerService.getUrlByAlias("alias1");
        assertThat(result.getFullUrl()).isEqualTo("https://example.com");
    }

    @Test
    void testGetUrlByAliasNotFound() {
        when(urlMappingRepository.findByAlias("missing")).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> urlShortenerService.getUrlByAlias("missing"));
    }

    @Test
    void testDeleteAlias() {
        when(urlMappingRepository.existsByAlias("alias1")).thenReturn(true);
        doNothing().when(urlMappingRepository).deleteByAlias("alias1");
        urlShortenerService.deleteAlias("alias1");
        verify(urlMappingRepository).deleteByAlias("alias1");
    }

    @Test
    void testDeleteAliasNotFound() {
        when(urlMappingRepository.existsByAlias("missing"))
                .thenReturn(false);
        assertThrows(IllegalArgumentException.class, () -> urlShortenerService.deleteAlias("missing"));
    }
}