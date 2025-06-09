package org.josk.shorturl.integration;

import org.josk.shorturl.exception.InvalidUrlException;
import org.josk.shorturl.service.UrlShortenerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.josk.shorturl.TestConstants.INVALID_URL;
import static org.josk.shorturl.TestConstants.ORIGINAL_URL;

@SpringBootTest
@Testcontainers
class UrlServiceIntegrationTest {

    @Container
    static GenericContainer<?> redisContainer = new GenericContainer<>("redis:7")
            .withExposedPorts(6379);

    @DynamicPropertySource
    static void redisProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", redisContainer::getHost);
        registry.add("spring.data.redis.port", () -> redisContainer.getMappedPort(6379));
    }

    @Autowired
    private UrlShortenerService urlShortenerService;

    @Test
    void createAndGetShortUrl_shouldWorkCorrectly() {
        String code = urlShortenerService.createShortUrl(ORIGINAL_URL);
        assertThat(code).isNotNull();
        assertThat(code.length()).isEqualTo(6);

        String fetchedUrl = urlShortenerService.getOriginalUrl(code);
        assertThat(fetchedUrl).isEqualTo(ORIGINAL_URL);
    }

    @Test
    void createShortUrl_shouldThrowException_whenUrlIsInvalid() {
        assertThatThrownBy(() -> urlShortenerService.createShortUrl(INVALID_URL))
                .isInstanceOf(InvalidUrlException.class)
                .hasMessageContaining("Invalid or unsafe URL");
    }
}
