package org.josk.shorturl.integration;

import org.josk.shorturl.repository.UrlRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import static org.assertj.core.api.Assertions.assertThat;
import static org.josk.shorturl.TestConstants.DEFAULT_TTL_SECONDS;
import static org.josk.shorturl.TestConstants.ORIGINAL_URL;
import static org.josk.shorturl.TestConstants.TEST_CODE;

@SpringBootTest
@Testcontainers
class UrlRepositoryIntegrationTest {
    @Container
    static GenericContainer<?> redisContainer = new GenericContainer<>("redis:7")
            .withExposedPorts(6379);

    @DynamicPropertySource
    static void redisProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", redisContainer::getHost);
        registry.add("spring.data.redis.port", () -> redisContainer.getMappedPort(6379));
    }

    @Autowired
    private UrlRepository urlRepository;

    @Test
    void saveAndFindOriginalUrl_shouldWorkCorrectly() {
        urlRepository.save(TEST_CODE, ORIGINAL_URL, DEFAULT_TTL_SECONDS);

        String result = urlRepository.findOriginalUrl(TEST_CODE);
        assertThat(result).isEqualTo(ORIGINAL_URL);
    }

    @Test
    void findOriginalUrl_shouldReturnNull_whenCodeDoesNotExist() {
        String result = urlRepository.findOriginalUrl("nonexistent");
        assertThat(result).isNull();
    }

    @Test
    void exists_shouldReturnTrue_whenCodeExists() {
        urlRepository.save(TEST_CODE, ORIGINAL_URL, DEFAULT_TTL_SECONDS);

        boolean exists = urlRepository.exists(TEST_CODE);
        assertThat(exists).isTrue();
    }

    @Test
    void exists_shouldReturnFalse_whenCodeDoesNotExist() {
        boolean exists = urlRepository.exists("missingCode");
        assertThat(exists).isFalse();
    }

    @Test
    void save_shouldExpire_afterTTL() throws InterruptedException {
        urlRepository.save(TEST_CODE, ORIGINAL_URL, 1);

        Thread.sleep(2000);

        String result = urlRepository.findOriginalUrl(TEST_CODE);
        assertThat(result).isNull();
    }
}
