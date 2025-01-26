package com.url_shortener_java_backend.url_shortener_java_backend.controller;

import com.url_shortener_java_backend.url_shortener_java_backend.dto.RestResponse;
import com.url_shortener_java_backend.url_shortener_java_backend.dto.UrlRequestDto;
import com.url_shortener_java_backend.url_shortener_java_backend.dto.UrlResponseDto;
import com.url_shortener_java_backend.url_shortener_java_backend.service.UrlShortenerService;
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

    @BeforeEach
    void setUp() {
        urlRequestDto = new UrlRequestDto("https://example.com", null);
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
        // Arrange
        when(urlShortenerService.generateAndPersistShortUrl(any())).thenReturn(urlResponseDto);

        // Act
        ResponseEntity<?> response = controller.generateShortUrl(urlRequestDto);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        RestResponse<?> restResponse = (RestResponse<?>) response.getBody();
        assertNotNull(restResponse);
        assertNotNull(restResponse.getResult());
        assertTrue(restResponse.getResult() instanceof UrlResponseDto);
    }

    @Test
    void testGenerateShortUrlFailure_NullUrl() {
        // Arrange
        urlRequestDto.setUrl(null);

        // Act
        ResponseEntity<?> response = controller.generateShortUrl(urlRequestDto);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        RestResponse<?> restResponse = (RestResponse<?>) response.getBody();
        assertNotNull(restResponse);
        assertNotNull(restResponse.getErrorData());
    }

    @Test
    void testGenerateShortUrlFailure_ServiceError() {
        // Arrange
        doThrow(new RuntimeException("Service error")).when(urlShortenerService).generateAndPersistShortUrl(any());

        // Act
        ResponseEntity<?> response = controller.generateShortUrl(urlRequestDto);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        RestResponse<?> restResponse = (RestResponse<?>) response.getBody();
        assertNotNull(restResponse);
        assertNotNull(restResponse.getErrorData());
    }

    @Test
    void testGetOriginalUrlSuccess_BrowserRequest() throws Exception {
        // Arrange
        String shortUrlCode = "abc123";
        when(urlShortenerService.getOriginalUrl(any())).thenReturn(urlResponseDto);

        // Act
        ResponseEntity<?> response = controller.getOriginalUrl(shortUrlCode, mockResponse, "text/html");

        // Assert
        assertNull(response);
        verify(mockResponse).sendRedirect("https://example.com");
    }

    @Test
    void testGetOriginalUrlSuccess_ApiRequest() throws Exception {
        // Arrange
        String shortUrlCode = "abc123";
        when(urlShortenerService.getOriginalUrl(any())).thenReturn(urlResponseDto);

        // Act
        ResponseEntity<?> response = controller.getOriginalUrl(shortUrlCode, mockResponse, "application/json");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        RestResponse<?> restResponse = (RestResponse<?>) response.getBody();
        assertNotNull(restResponse);
        assertNotNull(restResponse.getResult());
        assertTrue(restResponse.getResult() instanceof UrlResponseDto);
    }

    @Test
    void testGetOriginalUrlFailure_InvalidShortUrlCode() throws Exception {
        // Arrange
        String shortUrlCode = "";

        // Act
        ResponseEntity<?> response = controller.getOriginalUrl(shortUrlCode, mockResponse, "text/html");

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        RestResponse<?> restResponse = (RestResponse<?>) response.getBody();
        assertNotNull(restResponse);
        assertNotNull(restResponse.getErrorData());
    }

    @Test
    void testGetOriginalUrlFailure_ServiceError() throws Exception {
        // Arrange
        String shortUrlCode = "abc123";
        doThrow(new RuntimeException("Service error")).when(urlShortenerService).getOriginalUrl(any());

        // Act
        ResponseEntity<?> response = controller.getOriginalUrl(shortUrlCode, mockResponse, "text/html");

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        RestResponse<?> restResponse = (RestResponse<?>) response.getBody();
        assertNotNull(restResponse);
        assertNotNull(restResponse.getErrorData());
    }

    @Test
    void testGetAllOriginalUrlsSuccess() {
        // Arrange
        List<UrlResponseDto> expectedList = new ArrayList<>();
        expectedList.add(new UrlResponseDto());
        when(urlShortenerService.getAllOriginalUrls()).thenReturn(expectedList);

        // Act
        ResponseEntity<?> response = controller.getAllOriginalUrls();

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(RestResponse.class, response.getBody().getClass());
        RestResponse<List<UrlResponseDto>> restResponse = (RestResponse<List<UrlResponseDto>>) response.getBody();
        assertEquals("success", restResponse.getStatus());
        assertNotNull(restResponse.getResult());
        assertEquals(1, restResponse.getResult().size());
    }

    @Test
    void testGetAllOriginalUrlsFailure() {
        // Arrange
        when(urlShortenerService.getAllOriginalUrls()).thenThrow(new RuntimeException("Test exception"));

        // Act
        ResponseEntity<?> response = controller.getAllOriginalUrls();

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(RestResponse.class, response.getBody().getClass());
        RestResponse<List<UrlResponseDto>> restResponse = (RestResponse<List<UrlResponseDto>>) response.getBody();
        assertEquals("failure", restResponse.getStatus());
        assertNull(restResponse.getResult());
        assertNotNull(restResponse.getErrorData());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.toString(), restResponse.getErrorData().getErrorCode());
        assertNotNull(restResponse.getErrorData().getDetails());
    }

    @Test
    void testDeleteShortUrlSuccess() {
        // Arrange
        String shortUrlCode = "abc123";

        // Act
        ResponseEntity<?> response = controller.deleteShortUrl(shortUrlCode);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(urlShortenerService).deleteShortUrl(eq(shortUrlCode));
    }

    @Test
    void testDeleteShortUrlFailure_InvalidShortUrlCode() {
        // Arrange
        String shortUrlCode = "";

        // Act
        ResponseEntity<?> response = controller.deleteShortUrl(shortUrlCode);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        RestResponse<?> restResponse = (RestResponse<?>) response.getBody();
        assertNotNull(restResponse);
        assertNotNull(restResponse.getErrorData());
    }

    @Test
    void testDeleteShortUrlFailure_ServiceError() {
        // Arrange
        String shortUrlCode = "abc123";
        doThrow(new RuntimeException("Service error")).when(urlShortenerService).deleteShortUrl(any());

        // Act
        ResponseEntity<?> response = controller.deleteShortUrl(shortUrlCode);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        RestResponse<?> restResponse = (RestResponse<?>) response.getBody();
        assertNotNull(restResponse);
        assertNotNull(restResponse.getErrorData());
    }
}
