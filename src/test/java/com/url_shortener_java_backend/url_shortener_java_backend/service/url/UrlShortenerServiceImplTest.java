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
    public static final String USER_ID = "test_user_id";

    private String SHORTENED_URL;

    @BeforeEach
    void setUp() {
        lenient().when(urlShortenerConstant.getShortenedUrlBase()).thenReturn("http://localhost:8080/v1.0/rest/url-shortener");
        SHORTENED_URL = urlShortenerConstant.getShortenedUrlBase() + "/" + SHORT_URL_CODE;

        lenient().when(urlShortenerUtil.getExpirationTime(any(LocalDateTime.class))).thenReturn(EXPIRATION_TIME);
        lenient().when(urlShortenerUtil.convertToShortUrl(ORIGINAL_URL)).thenReturn(SHORTENED_URL);
        lenient().when(urlShortenerUtil.buildUrlResponseDto(any(Url.class))).thenReturn(UrlResponseDto.builder()
                .originalUrl(ORIGINAL_URL)
                .shortUrl(SHORTENED_URL)
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
        when(urlShortenerRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        final UrlRequestDto urlRequestDto = UrlRequestDto.builder().url(ORIGINAL_URL).build();

        final UrlResponseDto result = urlShortenerServiceImpl.generateAndPersistShortUrl(urlRequestDto);

        assertNotNull(result);
        assertEquals(ORIGINAL_URL, result.getOriginalUrl());
        assertTrue(result.getShortUrl().contains(urlShortenerConstant.getShortenedUrlBase()));

        verify(urlShortenerRepository).save(any(Url.class));
    }

    @Test
    void testGetOriginalUrl_Found() throws Exception {
        final Url url = Url.builder()
                .shortUrl(SHORTENED_URL)
                .originalUrl(ORIGINAL_URL)
                .createdAt(CURRENT_TIME)
                .expiresAt(EXPIRATION_TIME)
                .build();
        when(urlShortenerRepository.findByShortUrlAndUser_UserId(SHORTENED_URL, USER_ID)).thenReturn(url);

        final UrlResponseDto result = urlShortenerServiceImpl.getOriginalUrl(SHORT_URL_CODE, USER_ID);

        assertNotNull(result);
        assertEquals(ORIGINAL_URL, result.getOriginalUrl());
        assertEquals(SHORTENED_URL, result.getShortUrl());

        verify(urlShortenerRepository).findByShortUrlAndUser_UserId(SHORTENED_URL, USER_ID);
    }

    @Test
    void testGetOriginalUrl_NotFound() {
        when(urlShortenerRepository.findByShortUrlAndUser_UserId(SHORTENED_URL, USER_ID)).thenReturn(null);

        final Exception exception = assertThrows(Exception.class, () -> urlShortenerServiceImpl.getOriginalUrl(SHORT_URL_CODE, USER_ID));
        assertTrue(exception.getMessage().contains("No original url found"));

        verify(urlShortenerRepository).findByShortUrlAndUser_UserId(SHORTENED_URL, USER_ID);
    }

    @Test
    void testGetAllOriginalUrls() {
        final Url url1 = Url.builder()
                .shortUrl(SHORTENED_URL)
                .originalUrl(ORIGINAL_URL)
                .createdAt(CURRENT_TIME)
                .expiresAt(EXPIRATION_TIME)
                .build();
        final Url url2 = Url.builder()
                .shortUrl(urlShortenerConstant.getShortenedUrlBase() + "/67890")
                .originalUrl("https://www.example2.com")
                .createdAt(CURRENT_TIME)
                .expiresAt(EXPIRATION_TIME)
                .build();
        when(urlShortenerRepository.findByUser_UserId(USER_ID)).thenReturn(List.of(url1, url2));

        final List<UrlResponseDto> results = urlShortenerServiceImpl.getAllOriginalUrls(USER_ID);

        assertEquals(2, results.size());
        assertEquals(ORIGINAL_URL, results.get(0).getOriginalUrl());
        assertEquals(SHORTENED_URL, results.get(0).getShortUrl());

        verify(urlShortenerRepository).findByUser_UserId(USER_ID);
    }

    @Test
    void testDeleteShortUrl() {
        final Url url = Url.builder()
                .shortUrl(SHORTENED_URL)
                .originalUrl(ORIGINAL_URL)
                .createdAt(CURRENT_TIME)
                .expiresAt(EXPIRATION_TIME)
                .build();
        when(urlShortenerRepository.findByShortUrlAndUser_UserId(SHORTENED_URL, USER_ID)).thenReturn(url);

        urlShortenerServiceImpl.deleteShortUrl(SHORT_URL_CODE, USER_ID);

        verify(urlShortenerRepository).findByShortUrlAndUser_UserId(SHORTENED_URL, USER_ID);
        verify(urlShortenerRepository).delete(url);
    }

    @Test
    void testDeleteShortUrl_NotFound() {
        when(urlShortenerRepository.findByShortUrlAndUser_UserId(SHORTENED_URL, USER_ID)).thenReturn(null);

        urlShortenerServiceImpl.deleteShortUrl(SHORT_URL_CODE, USER_ID);

        verify(urlShortenerRepository).findByShortUrlAndUser_UserId(SHORTENED_URL, USER_ID);
        verify(urlShortenerRepository).delete(null);
    }

    @Test
    void testDeleteAllShortUrl() {
        final Url url1 = Url.builder()
                .shortUrl(SHORTENED_URL)
                .originalUrl(ORIGINAL_URL)
                .createdAt(CURRENT_TIME)
                .expiresAt(EXPIRATION_TIME)
                .build();
        final Url url2 = Url.builder()
                .shortUrl(urlShortenerConstant.getShortenedUrlBase() + "/67890")
                .originalUrl("https://www.example2.com")
                .createdAt(CURRENT_TIME)
                .expiresAt(EXPIRATION_TIME)
                .build();
        final List<Url> urls = List.of(url1, url2);
        when(urlShortenerRepository.findByUser_UserId(USER_ID)).thenReturn(urls);

        urlShortenerServiceImpl.deleteAllShortUrls(USER_ID);

        verify(urlShortenerRepository).findByUser_UserId(USER_ID);
        verify(urlShortenerRepository).deleteAll(urls);
    }
}
