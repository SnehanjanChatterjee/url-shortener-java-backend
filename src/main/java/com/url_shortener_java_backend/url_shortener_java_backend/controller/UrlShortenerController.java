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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@CrossOrigin
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
                    .errorMessage("No url provided")
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
        ResponseEntity<RestResponse<UrlResponseDto>> responseEntity;
        final RestResponse<UrlResponseDto> restResponse = new RestResponse<>();

        if (StringUtils.isEmpty(shortUrlCode) || shortUrlCode.equals("null")) {
            final ErrorResponseData errorResponseData = new ErrorResponseData().builder()
                    .errorMessage("No url provided")
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

    @DeleteMapping("{shortUrlCode}")
    public ResponseEntity<?> deleteShortUrl(@PathVariable String shortUrlCode) {
        ResponseEntity<RestResponse<UrlResponseDto>> responseEntity;
        final RestResponse<UrlResponseDto> restResponse = new RestResponse<>();

        if (StringUtils.isEmpty(shortUrlCode) || shortUrlCode.equals("null")) {
            final ErrorResponseData errorResponseData = new ErrorResponseData().builder()
                    .errorMessage("No url provided")
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
}
