package org.josk.shorturl.service;

import org.josk.shorturl.exception.InvalidUrlException;
import org.josk.shorturl.exception.ShortUrlNotFoundException;
import org.josk.shorturl.repository.UrlRepository;
import org.josk.shorturl.utils.UrlValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UrlShortenerServiceImpl implements UrlShortenerService {
    private final UrlRepository urlRepository;
    private final UrlValidator urlValidator;

    @Value("${app.url-ttl-seconds}")
    private long ttlSeconds;

    public UrlShortenerServiceImpl(UrlRepository urlRepository, UrlValidator urlValidator) {
        this.urlRepository = urlRepository;
        this.urlValidator = urlValidator;
    }

    @Override
    public String createShortUrl(String originalUrl){
        if (!urlValidator.isValid(originalUrl)) {
            System.out.println("URL invalid");
            throw new InvalidUrlException("Invalid or unsafe URL provided.");
        }

        String code;

        do {
            code = generateCode();
        } while (urlRepository.exists(code));

        urlRepository.save(code, originalUrl, ttlSeconds);
        return code;
    }

    @Override
    public String getOriginalUrl(String code) {
        String url = urlRepository.findOriginalUrl(code);
        if (url == null) {
            throw new ShortUrlNotFoundException("Short URL not found for code: " + code);
        }
        return url;
    }

    private String generateCode() {
        return UUID.randomUUID().toString().substring(0, 6);
    }
}
