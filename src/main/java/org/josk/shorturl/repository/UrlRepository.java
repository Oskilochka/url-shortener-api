package org.josk.shorturl.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
public class UrlRepository {
    private final static String PREFIX = "url:";
    private static final Logger log = LoggerFactory.getLogger(UrlRepository.class);
    private final RedisTemplate<String, String> redisTemplate;

    public UrlRepository(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void save(String code, String originalUrl, long ttlSeconds) {
        String key = PREFIX + code;

        redisTemplate.opsForValue().set(PREFIX + code, originalUrl, Duration.ofSeconds(ttlSeconds));
        log.info("Saved URL with key {} and TTL {} seconds", key, ttlSeconds);
    }

    public String findOriginalUrl(String code) {
        String key = PREFIX + code;

        String url = redisTemplate.opsForValue().get(PREFIX + code);
        log.info("Finding URL for key {}: {}", key, url);

        return url;
    }

    public boolean exists(String code) {
        String key = PREFIX + code;

        Boolean exists = redisTemplate.hasKey(key);
        log.info("Checking existence for key {}: {}", key, exists);

        return Boolean.TRUE.equals(exists);
    }
}
