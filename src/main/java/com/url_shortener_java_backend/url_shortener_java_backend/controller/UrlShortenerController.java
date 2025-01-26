package com.url_shortener_java_backend.url_shortener_java_backend.controller;

import com.url_shortener_java_backend.url_shortener_java_backend.dto.ErrorResponseData;
import com.url_shortener_java_backend.url_shortener_java_backend.dto.RestResponse;
import com.url_shortener_java_backend.url_shortener_java_backend.dto.UrlRequestDto;
import com.url_shortener_java_backend.url_shortener_java_backend.dto.UrlResponseDto;
import com.url_shortener_java_backend.url_shortener_java_backend.service.UrlShortenerService;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1.0/rest/url-shortener")
public class UrlShortenerController {

    @Autowired
    private UrlShortenerService urlShortenerService;

    @PostMapping("/generate")
    public ResponseEntity<?> generateShortUrl(@RequestBody UrlRequestDto urlRequestDto) {
        ResponseEntity<RestResponse<UrlResponseDto>> responseEntity;
        final RestResponse<UrlResponseDto> restResponse = new RestResponse<>();

        if (urlRequestDto.getUrl() == null) {
            final ErrorResponseData errorResponseData = new ErrorResponseData().builder()
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
                final ErrorResponseData errorResponseData = new ErrorResponseData().builder()
                        .errorMessage("Failed to generate shortened url")
                        .errorCode(HttpStatus.INTERNAL_SERVER_ERROR.toString())
                        .details(e.getMessage())
                        .build();
                restResponse.setStatus("failure");
                restResponse.setErrorData(errorResponseData);
                responseEntity = new ResponseEntity<RestResponse<UrlResponseDto>>(restResponse, HttpStatus.INTERNAL_SERVER_ERROR);
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
            final ErrorResponseData errorResponseData = new ErrorResponseData().builder()
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
                final ErrorResponseData errorResponseData = new ErrorResponseData().builder()
                        .errorMessage("Failed to fetch original url")
                        .errorCode(HttpStatus.INTERNAL_SERVER_ERROR.toString())
                        .details(e.getMessage())
                        .build();
                restResponse.setStatus("failure");
                restResponse.setErrorData(errorResponseData);
                return new ResponseEntity<RestResponse<UrlResponseDto>>(restResponse, HttpStatus.INTERNAL_SERVER_ERROR);
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
            final ErrorResponseData errorResponseData = new ErrorResponseData().builder()
                    .errorMessage("Failed to fetch all original urls")
                    .errorCode(HttpStatus.INTERNAL_SERVER_ERROR.toString())
                    .details(e.getMessage())
                    .build();
            restResponse.setStatus("failure");
            restResponse.setErrorData(errorResponseData);
            return new ResponseEntity<RestResponse<List<UrlResponseDto>>>(restResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("{shortUrlCode}")
    public ResponseEntity<?> deleteShortUrl(@PathVariable String shortUrlCode) {
        ResponseEntity<RestResponse<UrlResponseDto>> responseEntity;
        final RestResponse<UrlResponseDto> restResponse = new RestResponse<>();

        if (StringUtils.isEmpty(shortUrlCode) || shortUrlCode.equals("null")) {
            final ErrorResponseData errorResponseData = new ErrorResponseData().builder()
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
                final ErrorResponseData errorResponseData = new ErrorResponseData().builder()
                        .errorMessage("Failed to delete url")
                        .errorCode(HttpStatus.INTERNAL_SERVER_ERROR.toString())
                        .details(e.getMessage())
                        .build();
                restResponse.setStatus("failure");
                restResponse.setErrorData(errorResponseData);
                responseEntity = new ResponseEntity<RestResponse<UrlResponseDto>>(restResponse, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        return responseEntity;
    }

    @DeleteMapping
    public ResponseEntity<?> deleteAllShortUrls() {
        ResponseEntity<RestResponse<UrlResponseDto>> responseEntity;
        final RestResponse<UrlResponseDto> restResponse = new RestResponse<>();
        try {
            urlShortenerService.deleteAllShortUrls();
            responseEntity = new ResponseEntity<RestResponse<UrlResponseDto>>(restResponse, HttpStatus.OK);
        } catch (final Exception e) {
            final ErrorResponseData errorResponseData = new ErrorResponseData().builder()
                    .errorMessage("Failed to delete all urls")
                    .errorCode(HttpStatus.INTERNAL_SERVER_ERROR.toString())
                    .details(e.getMessage())
                    .build();
            restResponse.setStatus("failure");
            restResponse.setErrorData(errorResponseData);
            responseEntity = new ResponseEntity<RestResponse<UrlResponseDto>>(restResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return responseEntity;
    }
}
