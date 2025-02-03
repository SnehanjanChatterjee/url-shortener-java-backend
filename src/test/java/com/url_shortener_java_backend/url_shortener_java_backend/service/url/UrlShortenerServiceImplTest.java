package com.url_shortener_java_backend.url_shortener_java_backend.service.url;

import com.url_shortener_java_backend.url_shortener_java_backend.constants.UrlShortenerConstant;
import com.url_shortener_java_backend.url_shortener_java_backend.dto.url.UrlRequestDto;
import com.url_shortener_java_backend.url_shortener_java_backend.dto.url.UrlResponseDto;
import com.url_shortener_java_backend.url_shortener_java_backend.entity.Url;
import com.url_shortener_java_backend.url_shortener_java_backend.repository.UrlShortenerRepository;
import com.url_shortener_java_backend.url_shortener_java_backend.util.UrlShortenerUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UrlShortenerServiceImplTest {

    @Mock
    private UrlShortenerUtil urlShortenerUtil;

    @Mock
    private UrlShortenerConstant urlShortenerConstant;

    @Mock
    private UrlShortenerRepository urlShortenerRepository;

    @InjectMocks
    private UrlShortenerServiceImpl urlShortenerServiceImpl;

    private static final String ORIGINAL_URL = "https://www.example.com";
    private static final String SHORT_URL_CODE = "12345";
    private static final LocalDateTime CURRENT_TIME = LocalDateTime.of(2022, 1, 1, 12, 0, 0);
    private static final LocalDateTime EXPIRATION_TIME = CURRENT_TIME.plusDays(30);

    private String shortenedUrl;

    @BeforeEach
    void setUp() {
        lenient().when(urlShortenerConstant.getShortenedUrlBase()).thenReturn("http://localhost:8080/v1.0/rest/url-shortener");
        shortenedUrl = urlShortenerConstant.getShortenedUrlBase() + "/" + SHORT_URL_CODE;

        lenient().when(urlShortenerUtil.getExpirationTime(any(LocalDateTime.class))).thenReturn(EXPIRATION_TIME);
        lenient().when(urlShortenerUtil.convertToShortUrl(ORIGINAL_URL)).thenReturn(shortenedUrl);
        lenient().when(urlShortenerUtil.buildUrlResponseDto(any(Url.class))).thenReturn(UrlResponseDto.builder()
                .originalUrl(ORIGINAL_URL)
                .shortUrl(shortenedUrl)
                .creationDateTime(CURRENT_TIME)
                .expirationDateTime(EXPIRATION_TIME)
                .build());
    }

    @Test
    void testGenerateAndPersistShortUrl_NullPayload() {
        assertThrows(NullPointerException.class, () -> urlShortenerServiceImpl.generateAndPersistShortUrl(null));
    }

    @Test
    void testGenerateAndPersistShortUrl() {
        // Arrange
        when(urlShortenerRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        UrlRequestDto urlRequestDto = UrlRequestDto.builder().url(ORIGINAL_URL).build();

        // Act
        UrlResponseDto result = urlShortenerServiceImpl.generateAndPersistShortUrl(urlRequestDto);

        // Assert
        assertNotNull(result);
        assertEquals(ORIGINAL_URL, result.getOriginalUrl());
        assertTrue(result.getShortUrl().contains(urlShortenerConstant.getShortenedUrlBase()));

        verify(urlShortenerRepository).save(any(Url.class));
    }

    @Test
    void testGetOriginalUrl_Found() throws Exception {
        // Arrange
        Url url = Url.builder()
                .shortUrl(shortenedUrl)
                .originalUrl(ORIGINAL_URL)
                .createdAt(CURRENT_TIME)
                .expiresAt(EXPIRATION_TIME)
                .build();
        when(urlShortenerRepository.findByShortUrl(shortenedUrl)).thenReturn(url);

        // Act
        UrlResponseDto result = urlShortenerServiceImpl.getOriginalUrl(SHORT_URL_CODE);

        // Assert
        assertNotNull(result);
        assertEquals(ORIGINAL_URL, result.getOriginalUrl());
        assertEquals(shortenedUrl, result.getShortUrl());

        verify(urlShortenerRepository).findByShortUrl(shortenedUrl);
    }

    @Test
    void testGetOriginalUrl_NotFound() {
        // Arrange
        when(urlShortenerRepository.findByShortUrl(shortenedUrl)).thenReturn(null);

        // Act and Assert
        Exception exception = assertThrows(Exception.class, () -> urlShortenerServiceImpl.getOriginalUrl(SHORT_URL_CODE));
        assertTrue(exception.getMessage().contains("No original url found"));

        verify(urlShortenerRepository).findByShortUrl(shortenedUrl);
    }

    @Test
    void testGetAllOriginalUrls() {
        // Arrange
        Url url1 = Url.builder()
                .shortUrl(shortenedUrl)
                .originalUrl(ORIGINAL_URL)
                .createdAt(CURRENT_TIME)
                .expiresAt(EXPIRATION_TIME)
                .build();
        Url url2 = Url.builder()
                .shortUrl(urlShortenerConstant.getShortenedUrlBase() + "/67890")
                .originalUrl("https://www.example2.com")
                .createdAt(CURRENT_TIME)
                .expiresAt(EXPIRATION_TIME)
                .build();
        when(urlShortenerRepository.findAll()).thenReturn(List.of(url1, url2));

        // Act
        List<UrlResponseDto> results = urlShortenerServiceImpl.getAllOriginalUrls();

        // Assert
        assertEquals(2, results.size());
        assertEquals(ORIGINAL_URL, results.get(0).getOriginalUrl());
        assertEquals(shortenedUrl, results.get(0).getShortUrl());

        verify(urlShortenerRepository).findAll();
    }

    @Test
    void testDeleteShortUrl() {
        // Arrange
        Url url = Url.builder()
                .shortUrl(shortenedUrl)
                .originalUrl(ORIGINAL_URL)
                .createdAt(CURRENT_TIME)
                .expiresAt(EXPIRATION_TIME)
                .build();
        when(urlShortenerRepository.findByShortUrl(shortenedUrl)).thenReturn(url);

        // Act
        urlShortenerServiceImpl.deleteShortUrl(SHORT_URL_CODE);

        // Assert
        verify(urlShortenerRepository).findByShortUrl(shortenedUrl);
        verify(urlShortenerRepository).delete(url);
    }

    @Test
    void testDeleteShortUrl_NotFound() {
        // Arrange
        when(urlShortenerRepository.findByShortUrl(shortenedUrl)).thenReturn(null);

        // Act
        urlShortenerServiceImpl.deleteShortUrl(SHORT_URL_CODE);

        // Assert
        verify(urlShortenerRepository).findByShortUrl(shortenedUrl);
        verify(urlShortenerRepository).delete(null);
    }
}
