package org.josk.shorturl.service;

import org.josk.shorturl.exception.InvalidUrlException;
import org.josk.shorturl.exception.ShortUrlNotFoundException;
import org.josk.shorturl.repository.UrlRepository;
import org.josk.shorturl.utils.UrlValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.josk.shorturl.TestConstants.DEFAULT_TTL_SECONDS;
import static org.josk.shorturl.TestConstants.INVALID_URL;
import static org.josk.shorturl.TestConstants.ORIGINAL_URL;
import static org.josk.shorturl.TestConstants.TEST_CODE;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class})
class UrlShortenerServiceImplTest {
    @Mock
    private UrlRepository urlRepository;
    @Mock
    private UrlValidator urlValidator;

    @InjectMocks
    private UrlShortenerServiceImpl urlShortenerService;

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(urlShortenerService, "ttlSeconds", DEFAULT_TTL_SECONDS);
    }

    @Test
    void createShortUrl_shouldReturnCodeAndSaveUrl() {
        when(urlValidator.isValid(ORIGINAL_URL)).thenReturn(true);
        when(urlRepository.exists(anyString())).thenReturn(false);

        String code = urlShortenerService.createShortUrl(ORIGINAL_URL);

        assertThat(code).isNotNull();
        assertThat(code.length()).isEqualTo(6);

        verify(urlRepository, times(1)).save(eq(code), eq(ORIGINAL_URL), eq(DEFAULT_TTL_SECONDS));
    }


    @Test
    void createShortUrl_shouldThrowException_whenUrlIsInvalid() {
        when(urlValidator.isValid(INVALID_URL)).thenReturn(false);

        assertThatThrownBy(() -> urlShortenerService.createShortUrl(INVALID_URL))
                .isInstanceOf(InvalidUrlException.class)
                .hasMessageContaining("Invalid or unsafe URL provided.");

        verify(urlRepository, never()).save(anyString(), anyString(), anyLong());
    }

    @Test
    void getOriginalUrl_shouldReturnCorrectUrl() {
        when(urlRepository.findOriginalUrl(TEST_CODE)).thenReturn(ORIGINAL_URL);

        String result = urlShortenerService.getOriginalUrl(TEST_CODE);

        assertThat(result).isEqualTo(ORIGINAL_URL);
    }

    @Test
    void getOriginalUrl_shouldReturnNull_whenCodeNotFound() {
        when(urlRepository.findOriginalUrl(TEST_CODE)).thenReturn(null);

        assertThatThrownBy(() -> urlShortenerService.getOriginalUrl(TEST_CODE))
                .isInstanceOf(ShortUrlNotFoundException.class)
                .hasMessageContaining("Short URL not found for code: " + TEST_CODE);
    }
}
