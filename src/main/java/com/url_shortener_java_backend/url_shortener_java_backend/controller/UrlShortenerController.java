package com.url_shortener_java_backend.url_shortener_java_backend.controller;

import com.url_shortener_java_backend.url_shortener_java_backend.dto.ErrorResponseData;
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
            final ErrorResponseData errorResponseData = ErrorResponseData.builder()
                    .errorMessage("No url provided. Please provide a valid long url to shorten.")
                    .errorCode(HttpStatus.BAD_REQUEST.toString())
                    .build();
            restResponse.setErrorData(errorResponseData);
            responseEntity = new ResponseEntity<RestResponse<UrlResponseDto>>(restResponse, HttpStatus.BAD_REQUEST);
        } else {
            try {
                final UrlResponseDto urlResponseDto = urlShortenerService.generateAndPersistShortUrl(urlRequestDto);
                restResponse.setResult(urlResponseDto);
                responseEntity = new ResponseEntity<RestResponse<UrlResponseDto>>(restResponse, HttpStatus.CREATED);
            } catch (final Exception e) {
                responseEntity = getErrorResponseEntity(e, restResponse, "Failed to generate shortened url");
            }
        }

        return responseEntity;
    }

    @GetMapping("{shortUrlCode}")
    public ResponseEntity<?> getOriginalUrl(@PathVariable String shortUrlCode,
                                            HttpServletResponse response,
                                            @RequestHeader(value = "Accept", required = false) String acceptHeader) {
        final RestResponse<UrlResponseDto> restResponse = new RestResponse<>();

        if (StringUtils.isEmpty(shortUrlCode) || shortUrlCode.equals("null")) {
            final ErrorResponseData errorResponseData = ErrorResponseData.builder()
                    .errorMessage("No url provided. Please provide a valid short url to fetch the original url.")
                    .errorCode(HttpStatus.BAD_REQUEST.toString())
                    .build();
            restResponse.setErrorData(errorResponseData);
            return new ResponseEntity<RestResponse<UrlResponseDto>>(restResponse, HttpStatus.BAD_REQUEST);
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
                    return new ResponseEntity<RestResponse<UrlResponseDto>>(restResponse, HttpStatus.OK);
                }
            } catch (final Exception e) {
                return getErrorResponseEntity(e, restResponse, "Failed to fetch original url");
            }
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllOriginalUrls() {
        final RestResponse<List<UrlResponseDto>> restResponse = new RestResponse<>();
        try {
            final List<UrlResponseDto> urlResponseDtoList = urlShortenerService.getAllOriginalUrls();
            restResponse.setResult(urlResponseDtoList);
            return new ResponseEntity<RestResponse<List<UrlResponseDto>>>(restResponse, HttpStatus.OK);
        } catch (final Exception e) {
            return getErrorResponseEntity(e, restResponse, "Failed to fetch all original urls");
        }
    }

    @DeleteMapping("{shortUrlCode}")
    public ResponseEntity<?> deleteShortUrl(@PathVariable String shortUrlCode) {
        ResponseEntity<RestResponse<UrlResponseDto>> responseEntity;
        final RestResponse<UrlResponseDto> restResponse = new RestResponse<>();

        if (StringUtils.isEmpty(shortUrlCode) || shortUrlCode.equals("null")) {
            final ErrorResponseData errorResponseData = ErrorResponseData.builder()
                    .errorMessage("No url provided. Please provide a valid short url to be deleted.")
                    .errorCode(HttpStatus.BAD_REQUEST.toString())
                    .build();
            restResponse.setErrorData(errorResponseData);
            responseEntity = new ResponseEntity<RestResponse<UrlResponseDto>>(restResponse, HttpStatus.BAD_REQUEST);
        } else {
            try {
                urlShortenerService.deleteShortUrl(shortUrlCode);
                responseEntity = new ResponseEntity<RestResponse<UrlResponseDto>>(restResponse, HttpStatus.OK);
            } catch (final Exception e) {
                responseEntity = getErrorResponseEntity(e, restResponse, "Failed to delete url");
            }
        }
        return responseEntity;
    }

    @DeleteMapping
    public ResponseEntity<?> deleteAllShortUrls() {
        ResponseEntity<RestResponse<UrlResponseDto>> responseEntity;
        final RestResponse<UrlResponseDto> restResponse = new RestResponse<>();
        try {
            CompletableFuture.runAsync(() -> urlShortenerService.deleteAllShortUrls());
            responseEntity = new ResponseEntity<RestResponse<UrlResponseDto>>(restResponse, HttpStatus.OK);
        } catch (final Exception e) {
            responseEntity = getErrorResponseEntity(e, restResponse, "Failed to delete all urls");
        }
        return responseEntity;
    }
}
