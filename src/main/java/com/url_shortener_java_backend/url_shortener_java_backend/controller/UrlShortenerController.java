package com.url_shortener_java_backend.url_shortener_java_backend.controller;

import com.url_shortener_java_backend.url_shortener_java_backend.dto.ErrorResponseData;
import com.url_shortener_java_backend.url_shortener_java_backend.dto.RestResponse;
import com.url_shortener_java_backend.url_shortener_java_backend.dto.UrlRequestDto;
import com.url_shortener_java_backend.url_shortener_java_backend.dto.UrlResponseDto;
import com.url_shortener_java_backend.url_shortener_java_backend.service.UrlShortenerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
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
            responseEntity = new ResponseEntity<>(restResponse, HttpStatus.BAD_REQUEST);
        } else {
            try {
                final UrlResponseDto urlResponseDto = urlShortenerService.generateAndPersistShortUrl(urlRequestDto);
                restResponse.setResult(urlResponseDto);
                responseEntity = new ResponseEntity<>(restResponse, HttpStatus.CREATED);
            } catch (final Exception e) {
                final ErrorResponseData errorResponseData = new ErrorResponseData().builder()
                        .errorMessage("Failed to generate shortened url")
                        .errorCode(HttpStatus.INTERNAL_SERVER_ERROR.toString())
                        .details(e.getMessage())
                        .build();
                restResponse.setStatus("failure");
                restResponse.setErrorData(errorResponseData);
                responseEntity = new ResponseEntity<>(restResponse, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        return responseEntity;
    }
}
