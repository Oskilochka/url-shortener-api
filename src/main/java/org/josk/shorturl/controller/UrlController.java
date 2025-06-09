package org.josk.shorturl.controller;

import org.josk.shorturl.model.Url;
import org.josk.shorturl.service.UrlShortenerService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/shorten")
public class UrlController {
    private final UrlShortenerService urlShortenerService;

    @Value("${app.base-url}")
    private String baseUrl;

    public UrlController(UrlShortenerService urlShortenerService) {
        this.urlShortenerService = urlShortenerService;
    }

    @PostMapping()
    public ResponseEntity<String> createShortUrl(@RequestBody Url url) {
        String shortCode = urlShortenerService.createShortUrl(url.getOriginalUrl());
        String shortUrl = baseUrl + "/" + shortCode;

        return ResponseEntity.ok(shortUrl);
    }

    @GetMapping("/{code}")
    public ResponseEntity<Void> getOriginalUrl(@PathVariable String code) {
        String originalUrl = urlShortenerService.getOriginalUrl(code);

        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(originalUrl))
                .build();
    }
}
