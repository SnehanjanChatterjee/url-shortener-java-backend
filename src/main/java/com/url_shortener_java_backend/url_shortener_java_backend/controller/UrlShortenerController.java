package com.url_shortener_java_backend.url_shortener_java_backend.controller;

import com.url_shortener_java_backend.url_shortener_java_backend.dto.RestResponse;
import com.url_shortener_java_backend.url_shortener_java_backend.dto.url.UrlRequestDto;
import com.url_shortener_java_backend.url_shortener_java_backend.dto.url.UrlResponseDto;
import com.url_shortener_java_backend.url_shortener_java_backend.service.url.UrlShortenerService;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/v1.0/rest/url-shortener/url")
public class UrlShortenerController extends BaseController {

    @Autowired
    private UrlShortenerService urlShortenerService;

    @PostMapping("/generate")
    public ResponseEntity<?> generateShortUrl(@RequestBody UrlRequestDto urlRequestDto) {
        ResponseEntity<RestResponse<UrlResponseDto>> responseEntity;
        final RestResponse<UrlResponseDto> restResponse = new RestResponse<>();

        if (urlRequestDto.getUrl() == null) {
            responseEntity = getErrorResponseEntity(null, HttpStatus.BAD_REQUEST, restResponse, "No url provided. Please provide a valid long url to shorten.");
        } else {
            try {
                final UrlResponseDto urlResponseDto = urlShortenerService.generateAndPersistShortUrl(urlRequestDto);
                restResponse.setResult(urlResponseDto);
                responseEntity = new ResponseEntity<>(restResponse, HttpStatus.CREATED);
            } catch (final Exception e) {
                responseEntity = getErrorResponseEntity(e, HttpStatus.INTERNAL_SERVER_ERROR, restResponse, "Failed to generate shortened url");
            }
        }

        return responseEntity;
    }

    @GetMapping("{shortUrlCode:[a-f0-9]{8}}")
    public ResponseEntity<?> getOriginalUrl(@PathVariable String shortUrlCode,
                                            HttpServletResponse response,
                                            @RequestHeader(value = "Accept", required = false) String acceptHeader) {
        final RestResponse<UrlResponseDto> restResponse = new RestResponse<>();

        if (StringUtils.isEmpty(shortUrlCode) || shortUrlCode.equals("null")) {
            return getErrorResponseEntity(null, HttpStatus.BAD_REQUEST, restResponse, "No url provided. Please provide a valid short url to fetch the original url.");
        } else {
            try {
                final UrlResponseDto urlResponseDto = urlShortenerService.getOriginalUrl(shortUrlCode);
                restResponse.setResult(urlResponseDto);

                // Check if the client accepts HTML (browser request)
                if (acceptHeader != null && acceptHeader.contains("text/html")) {
                    // Redirect the browser
                    response.sendRedirect(urlResponseDto.getOriginalUrl());
                    return null; // No further response needed, as the redirection is handled
                } else {
                    // For API clients (e.g., Postman), return the URL in JSON format
                    return new ResponseEntity<>(restResponse, HttpStatus.OK);
                }
            } catch (final Exception e) {
                return getErrorResponseEntity(e, HttpStatus.INTERNAL_SERVER_ERROR, restResponse, "Failed to fetch original url");
            }
        }
    }

    @GetMapping("{userId:[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}}")
    public ResponseEntity<?> getAllOriginalUrls(@PathVariable String userId) {
        final RestResponse<List<UrlResponseDto>> restResponse = new RestResponse<>();
        try {
            final List<UrlResponseDto> urlResponseDtoList = urlShortenerService.getAllOriginalUrls(userId);
            restResponse.setResult(urlResponseDtoList);
            return new ResponseEntity<>(restResponse, HttpStatus.OK);
        } catch (final Exception e) {
            return getErrorResponseEntity(e, HttpStatus.INTERNAL_SERVER_ERROR, restResponse, "Failed to fetch all original urls");
        }
    }

    @DeleteMapping("{shortUrlCode}/{userId}")
    public ResponseEntity<?> deleteShortUrl(@PathVariable String shortUrlCode,
                                            @PathVariable String userId) {
        ResponseEntity<RestResponse<UrlResponseDto>> responseEntity;
        final RestResponse<UrlResponseDto> restResponse = new RestResponse<>();

        if (StringUtils.isEmpty(shortUrlCode) || shortUrlCode.equals("null")) {
            responseEntity = getErrorResponseEntity(null, HttpStatus.BAD_REQUEST, restResponse, "No url provided. Please provide a valid short url to be deleted.");
        } else {
            try {
                urlShortenerService.deleteShortUrl(shortUrlCode, userId);
                responseEntity = new ResponseEntity<>(restResponse, HttpStatus.OK);
            } catch (final Exception e) {
                responseEntity = getErrorResponseEntity(e, HttpStatus.INTERNAL_SERVER_ERROR, restResponse, "Failed to delete url");
            }
        }
        return responseEntity;
    }

    @DeleteMapping("{userId}")
    public ResponseEntity<?> deleteAllShortUrls(@PathVariable String userId) {
        ResponseEntity<RestResponse<UrlResponseDto>> responseEntity;
        final RestResponse<UrlResponseDto> restResponse = new RestResponse<>();
        try {
            CompletableFuture.runAsync(() -> urlShortenerService.deleteAllShortUrls(userId));
            responseEntity = new ResponseEntity<>(restResponse, HttpStatus.OK);
        } catch (final Exception e) {
            responseEntity = getErrorResponseEntity(e, HttpStatus.INTERNAL_SERVER_ERROR, restResponse, "Failed to delete all urls");
        }
        return responseEntity;
    }
}
