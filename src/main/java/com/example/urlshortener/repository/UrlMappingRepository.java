package com.example.urlshortener.repository;

import com.example.urlshortener.entity.UrlMapping;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UrlMappingRepository extends JpaRepository<UrlMapping, String> {
    Optional<UrlMapping> findByAlias(String alias);
    boolean existsByAlias(String alias);
    void deleteByAlias(String alias);
}