package com.example.urlshortener.service;

import com.example.urlshortener.dto.UrlResponse;
import com.example.urlshortener.entity.UrlMapping;
import com.example.urlshortener.repository.UrlMappingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static org.slf4j.LoggerFactory.getLogger;
@Service
public class UrlShortenerService {

    private static final Logger logger = LoggerFactory.getLogger(UrlShortenerService.class);

    private final UrlMappingRepository urlMappingRepository;
    private static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int ALIAS_LENGTH = 6;

    public UrlShortenerService(UrlMappingRepository urlMappingRepository) {
        this.urlMappingRepository = urlMappingRepository;
    }

    public UrlResponse shortenUrl(String fullUrl, String customAlias) {
        String alias = (customAlias == null || customAlias.isBlank()) ? generateUniqueAlias() : customAlias;

        if (urlMappingRepository.existsByAlias(alias)) {
            throw new IllegalArgumentException("Alias already exists: " + alias);
        }

        UrlMapping mapping = new UrlMapping(alias, fullUrl, LocalDateTime.now());
        urlMappingRepository.save(mapping);
        return toResponse(mapping);
    }

    public UrlMapping getUrlByAlias(String alias) {
        return urlMappingRepository.findByAlias(alias)
                .orElseThrow(() -> new IllegalArgumentException("Alias not found: " + alias));
    }

    @Transactional
    public boolean deleteAlias(String alias) {
        if (!urlMappingRepository.existsByAlias(alias)) {
            throw new IllegalArgumentException("Alias not found: " + alias);
        }

        UrlMapping mapping = urlMappingRepository.findByAlias(alias)
                .orElseThrow(() -> new IllegalArgumentException("Alias not found: " + alias));

        if (mapping.getFullUrl() == null || mapping.getFullUrl().isBlank()) {
            throw new IllegalArgumentException("Cannot delete alias with empty URL: " + alias);
        }

        urlMappingRepository.delete(mapping); // or deleteByAlias(alias)
        return true;
    }

    public List<UrlResponse> getAllUrls() {
        return urlMappingRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    private String generateUniqueAlias() {
        String alias;
        do {
            alias = ThreadLocalRandom.current()
                    .ints(ALIAS_LENGTH, 0, CHARACTERS.length())
                    .mapToObj(i -> String.valueOf(CHARACTERS.charAt(i)))
                    .reduce("", String::concat);
        } while (urlMappingRepository.existsByAlias(alias));
        return alias;
    }

    private UrlResponse toResponse(UrlMapping mapping) {
        return new UrlResponse(mapping.getAlias(), mapping.getFullUrl(), "http://localhost:8080/" + mapping.getAlias());
    }
}
