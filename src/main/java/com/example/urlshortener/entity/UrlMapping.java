package com.example.urlshortener.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UrlMapping {
    @Id
    private String alias;
    private String fullUrl;
    private LocalDateTime createdAt;
}