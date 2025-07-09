package com.example.urlshortener.repository;

import com.example.urlshortener.dto.UrlResponse;
import com.example.urlshortener.entity.UrlMapping;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class UrlShortenerRepositoryTest {

    @Autowired
    private UrlMappingRepository repository;

    @Test
    void testSaveAndFindById() {
        UrlMapping mapping = new UrlMapping("alias", "https://example.com", LocalDateTime.now());
        repository.save(mapping);

        Optional<UrlMapping> found = repository.findById("alias");
        assertTrue(found.isPresent());
        assertEquals("https://example.com", found.get().getFullUrl());
    }

    @Test
    void testDelete() {
        UrlMapping mapping = new UrlMapping("alias", "https://example.com", LocalDateTime.now());
        repository.save(mapping);

        repository.deleteById("alias");
        assertFalse(repository.findById("alias").isPresent());
    }
}
