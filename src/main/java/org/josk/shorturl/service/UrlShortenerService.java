package org.josk.shorturl.service;

public interface UrlShortenerService {
    String createShortUrl(String originalUrl);
    String getOriginalUrl(String code);
}
