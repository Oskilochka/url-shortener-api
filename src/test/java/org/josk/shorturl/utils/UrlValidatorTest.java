package org.josk.shorturl.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class UrlValidatorTest {
    private UrlValidator urlValidator;

    @BeforeEach
    void setUp() {
        urlValidator = new UrlValidator();
    }

    @Test
    void validPublicHttpUrl_shouldBeValid() {
        assertThat(urlValidator.isValid("http://example.com")).isTrue();
    }

    @Test
    void validHttpsUrl_shouldBeValid() {
        assertThat(urlValidator.isValid("https://example.com/page")).isTrue();
    }

    @Test
    void invalidUrl_shouldBeInvalid() {
        assertThat(urlValidator.isValid("not_a_url")).isFalse();
    }

    @Test
    void unsupportedProtocol_shouldBeInvalid() {
        assertThat(urlValidator.isValid("ftp://example.com")).isFalse();
    }

    @Test
    void localhost_shouldBeInvalid() {
        assertThat(urlValidator.isValid("http://localhost")).isFalse();
        assertThat(urlValidator.isValid("http://127.0.0.1")).isFalse();
        assertThat(urlValidator.isValid("http://[::1]")).isFalse();
    }

    @Test
    void privateIp_shouldBeInvalid() {
        assertThat(urlValidator.isValid("http://192.168.0.100")).isFalse();
        assertThat(urlValidator.isValid("http://10.0.0.5")).isFalse();
        assertThat(urlValidator.isValid("http://172.16.0.1")).isFalse();
    }

    @Test
    void publicIp_shouldBeValid() {
        assertThat(urlValidator.isValid("http://8.8.8.8")).isTrue();
    }
}
