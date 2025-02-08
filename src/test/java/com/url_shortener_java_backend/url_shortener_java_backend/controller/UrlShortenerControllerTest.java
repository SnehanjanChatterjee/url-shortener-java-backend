package com.url_shortener_java_backend.url_shortener_java_backend.controller;

import com.url_shortener_java_backend.url_shortener_java_backend.dto.RestResponse;
import com.url_shortener_java_backend.url_shortener_java_backend.dto.url.UrlRequestDto;
import com.url_shortener_java_backend.url_shortener_java_backend.dto.url.UrlResponseDto;
import com.url_shortener_java_backend.url_shortener_java_backend.service.url.UrlShortenerService;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UrlShortenerControllerTest {
    @Mock
    private UrlShortenerService urlShortenerService;

    @InjectMocks
    private UrlShortenerController controller;

    private UrlRequestDto urlRequestDto;
    private UrlResponseDto urlResponseDto;
    private HttpServletResponse mockResponse;

    public static final String SHORT_URL_CODE = "test_short_code";
    public static final String USER_ID = "test_user_id";

    @BeforeEach
    void setUp() {
        urlRequestDto = new UrlRequestDto("https://example.com", null, USER_ID);
        urlResponseDto = UrlResponseDto.builder()
                .originalUrl("https://example.com")
                .shortUrl("https://short.url")
                .creationDateTime(LocalDateTime.now())
                .expirationDateTime(LocalDateTime.now().plusDays(1))
                .build();

        mockResponse = mock(HttpServletResponse.class);
    }

    @Test
    void testGenerateShortUrlSuccess() {
        when(urlShortenerService.generateAndPersistShortUrl(any())).thenReturn(urlResponseDto);

        final ResponseEntity<?> response = controller.generateShortUrl(urlRequestDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        final RestResponse<?> restResponse = (RestResponse<?>) response.getBody();
        assertNotNull(restResponse);
        assertNotNull(restResponse.getResult());
        assertInstanceOf(UrlResponseDto.class, restResponse.getResult());
    }

    @Test
    void testGenerateShortUrlFailure_NullUrl() {
        urlRequestDto.setUrl(null);

        final ResponseEntity<?> response = controller.generateShortUrl(urlRequestDto);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        final RestResponse<?> restResponse = (RestResponse<?>) response.getBody();
        assertNotNull(restResponse);
        assertNotNull(restResponse.getErrorData());
    }

    @Test
    void testGenerateShortUrlFailure_ServiceError() {
        doThrow(new RuntimeException("Service error")).when(urlShortenerService).generateAndPersistShortUrl(any());

        final ResponseEntity<?> response = controller.generateShortUrl(urlRequestDto);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());

        final RestResponse<?> restResponse = (RestResponse<?>) response.getBody();
        assertNotNull(restResponse);
        assertNotNull(restResponse.getErrorData());
    }

    @Test
    void testGetOriginalUrlSuccess_BrowserRequest() throws Exception {
        when(urlShortenerService.getOriginalUrl(anyString(), anyString())).thenReturn(urlResponseDto);

        final ResponseEntity<?> response = controller.getOriginalUrl(SHORT_URL_CODE, "abcd", mockResponse, "text/html");

        assertNull(response);
        verify(mockResponse).sendRedirect("https://example.com");
    }

    @Test
    void testGetOriginalUrlSuccess_ApiRequest() throws Exception {
        when(urlShortenerService.getOriginalUrl(anyString(), anyString())).thenReturn(urlResponseDto);

        final ResponseEntity<?> response = controller.getOriginalUrl(SHORT_URL_CODE, USER_ID, mockResponse, "application/json");

        assertEquals(HttpStatus.OK, response.getStatusCode());

        final RestResponse<?> restResponse = (RestResponse<?>) response.getBody();
        assertNotNull(restResponse);
        assertNotNull(restResponse.getResult());
        assertTrue(restResponse.getResult() instanceof UrlResponseDto);
    }

    @Test
    void testGetOriginalUrlFailure_InvalidShortUrlCode() throws Exception {
        final ResponseEntity<?> response = controller.getOriginalUrl("", USER_ID, mockResponse, "text/html");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        final RestResponse<?> restResponse = (RestResponse<?>) response.getBody();
        assertNotNull(restResponse);
        assertNotNull(restResponse.getErrorData());
    }

    @Test
    void testGetOriginalUrlFailure_ServiceError() throws Exception {
        doThrow(new RuntimeException("Service error")).when(urlShortenerService).getOriginalUrl(anyString(), anyString());

        final ResponseEntity<?> response = controller.getOriginalUrl(SHORT_URL_CODE, USER_ID, mockResponse, "text/html");

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());

        final RestResponse<?> restResponse = (RestResponse<?>) response.getBody();
        assertNotNull(restResponse);
        assertNotNull(restResponse.getErrorData());
    }

    @Test
    void testGetAllOriginalUrlsSuccess() {
        final List<UrlResponseDto> expectedList = new ArrayList<>();
        expectedList.add(new UrlResponseDto());
        when(urlShortenerService.getAllOriginalUrls(anyString())).thenReturn(expectedList);

        ResponseEntity<?> response = controller.getAllOriginalUrls(USER_ID);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(RestResponse.class, response.getBody().getClass());

        final RestResponse<List<UrlResponseDto>> restResponse = (RestResponse<List<UrlResponseDto>>) response.getBody();
        assertEquals("success", restResponse.getStatus());
        assertNotNull(restResponse.getResult());
        assertEquals(1, restResponse.getResult().size());
    }

    @Test
    void testGetAllOriginalUrlsFailure() {
        when(urlShortenerService.getAllOriginalUrls(anyString())).thenThrow(new RuntimeException("Test exception"));

        final ResponseEntity<?> response = controller.getAllOriginalUrls(USER_ID);

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(RestResponse.class, response.getBody().getClass());

        final RestResponse<List<UrlResponseDto>> restResponse = (RestResponse<List<UrlResponseDto>>) response.getBody();
        assertEquals("failure", restResponse.getStatus());
        assertNull(restResponse.getResult());
        assertNotNull(restResponse.getErrorData());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.toString(), restResponse.getErrorData().getErrorCode());
        assertNotNull(restResponse.getErrorData().getDetails());
    }

    @Test
    void testDeleteShortUrlSuccess() {
        final ResponseEntity<?> response = controller.deleteShortUrl(SHORT_URL_CODE, USER_ID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(urlShortenerService).deleteShortUrl(eq(SHORT_URL_CODE), eq(USER_ID));
    }

    @Test
    void testDeleteShortUrlFailure_InvalidShortUrlCode() {
        ResponseEntity<?> response = controller.deleteShortUrl("", USER_ID);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        final RestResponse<?> restResponse = (RestResponse<?>) response.getBody();
        assertNotNull(restResponse);
        assertNotNull(restResponse.getErrorData());
    }

    @Test
    void testDeleteShortUrlFailure_ServiceError() {
        doThrow(new RuntimeException("Service error")).when(urlShortenerService).deleteShortUrl(anyString(), anyString());

        final ResponseEntity<?> response = controller.deleteShortUrl(SHORT_URL_CODE, USER_ID);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());

        final RestResponse<?> restResponse = (RestResponse<?>) response.getBody();
        assertNotNull(restResponse);
        assertNotNull(restResponse.getErrorData());
    }

    @Test
    void testDeleteAllShortUrlsSuccess() {
        final ResponseEntity<?> response = controller.deleteAllShortUrls(USER_ID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        RestResponse<?> restResponse = (RestResponse<?>) response.getBody();
        assertNotNull(restResponse);
        assertNull(restResponse.getErrorData());
    }

}
