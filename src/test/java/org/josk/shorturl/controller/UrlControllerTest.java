package org.josk.shorturl.controller;

import org.josk.shorturl.exception.InvalidUrlException;
import org.josk.shorturl.exception.ShortUrlNotFoundException;
import org.josk.shorturl.service.UrlShortenerService;
import org.josk.shorturl.utils.UrlValidator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.josk.shorturl.TestConstants.INVALID_URL;
import static org.josk.shorturl.TestConstants.ORIGINAL_URL;
import static org.josk.shorturl.TestConstants.TEST_CODE;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UrlController.class)
class UrlControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UrlShortenerService urlShortenerService;

    @MockitoBean
    private UrlValidator urlValidator;

    @Test
    void createShortUrl() throws Exception {
        when(urlShortenerService.createShortUrl(ORIGINAL_URL)).thenReturn(TEST_CODE);

        String EXPECTED_URL = "http://localhost:8080/" + TEST_CODE;

        mockMvc.perform(post("/api/v1/shorten")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"originalUrl\":\"" + ORIGINAL_URL + "\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string(EXPECTED_URL));
    }

    @Test
    void createShortUrl_shouldReturnBadRequest_whenUrlIsInvalid() throws Exception {
        when(urlValidator.isValid(INVALID_URL)).thenReturn(false);
        when(urlShortenerService.createShortUrl(INVALID_URL))
                .thenThrow(new InvalidUrlException("Invalid or unsafe URL provided."));

        mockMvc.perform(post("/api/v1/shorten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"originalUrl\":\"" + INVALID_URL + "\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Invalid or unsafe URL provided.")));
    }

    @Test
    void createShortUrl_shouldReturnServerError_whenExceptionThrown() throws Exception {
        when(urlValidator.isValid(ORIGINAL_URL)).thenReturn(true);
        when(urlShortenerService.createShortUrl(ORIGINAL_URL)).thenThrow(new RuntimeException("Something broke"));


        mockMvc.perform(post("/api/v1/shorten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"originalUrl\":\"" + ORIGINAL_URL + "\"}"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(containsString("An unexpected error occurred: Something broke")));
    }

    @Test
    void getOriginalUrl_shouldReturnIsFound_whenCodeExists() throws Exception {
        when(urlShortenerService.getOriginalUrl(TEST_CODE)).thenReturn(ORIGINAL_URL);

        mockMvc.perform(get("/api/v1/shorten/" + TEST_CODE))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", ORIGINAL_URL));
    }

    @Test
    void getOriginalUrl_shouldReturnNotFound_whenCodeNotExists() throws Exception {
        when(urlShortenerService.getOriginalUrl(TEST_CODE))
                .thenThrow(new ShortUrlNotFoundException("Short URL not found"));

        mockMvc.perform(get("/api/v1/shorten/" + TEST_CODE))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Short URL not found")));
    }
}
