package com.example.urlshortener.controller;

import com.example.urlshortener.dto.CreateUrlRequest;
import com.example.urlshortener.dto.UrlResponse;
import com.example.urlshortener.entity.UrlMapping;
import com.example.urlshortener.service.UrlShortenerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.List;


@RestController
@RequestMapping("/api")
public class UrlShortenerController {

    private static final Logger logger = LoggerFactory.getLogger(UrlShortenerService.class);

    private final UrlShortenerService urlShortenerService;

    public UrlShortenerController(UrlShortenerService urlShortenerService) {
        this.urlShortenerService = urlShortenerService;
    }

    @Operation(summary = "Shorten a URL")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "URL successfully shortened"),
            @ApiResponse(responseCode = "400", description = "Invalid input or alias already taken")
    })
    @PostMapping("/shorten")
    public ResponseEntity<UrlResponse> shorten(@RequestBody CreateUrlRequest request) {
        UrlResponse urlResponse = urlShortenerService.shortenUrl(request.fullUrl(), request.customAlias());
        return ResponseEntity.status(HttpStatus.CREATED).body(urlResponse);
    }

    @Operation(
            summary = "Redirect to full URL",
            description = "Returns a 302 redirect to the original full URL based on the alias. " +
                    "Note: Swagger UI does not follow redirect responses. " +
                    "Check the 'Location' header in the response and copy it into your browser to test the redirect."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "302", description = "Redirect to the original URL"),
            @ApiResponse(responseCode = "404", description = "Alias not found")
    })
    @GetMapping("/{alias}")
    public ResponseEntity<String> redirect(@PathVariable("alias") String alias) {
        UrlMapping mapping = urlShortenerService.getUrlByAlias(alias);
        if (mapping == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Alias not found");
        }
        return ResponseEntity.ok("Redirect target: " + mapping.getFullUrl());

    }

    @Operation(summary = "Delete a shortened URL")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Alias not found")
    })
    @DeleteMapping("/{alias}")
    public ResponseEntity<Void> delete(@PathVariable("alias") String alias) {
        try {
            boolean deleted = urlShortenerService.deleteAlias(alias);
            if (!deleted) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Alias not found");
            }
            logger.info("Alias '{}' deleted successfully", alias);
            // Return 204 No Content on successful deletion
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.error("Error deleting alias {}", alias, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal error deleting alias");
        }
    }


    @Operation(summary = "List all shortened URLs")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "A list of shortened URLs")
    })
    @GetMapping("/urls")
    public ResponseEntity<List<UrlResponse>> listAll() {
        return ResponseEntity.ok(urlShortenerService.getAllUrls());
    }

}