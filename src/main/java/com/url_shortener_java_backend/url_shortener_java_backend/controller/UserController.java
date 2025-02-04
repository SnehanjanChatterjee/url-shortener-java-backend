package com.url_shortener_java_backend.url_shortener_java_backend.controller;

import com.url_shortener_java_backend.url_shortener_java_backend.dto.ErrorResponseData;
import com.url_shortener_java_backend.url_shortener_java_backend.dto.RestResponse;
import com.url_shortener_java_backend.url_shortener_java_backend.dto.user.UserRequestDto;
import com.url_shortener_java_backend.url_shortener_java_backend.dto.user.UserResponseDto;
import com.url_shortener_java_backend.url_shortener_java_backend.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1.0/rest/url-shortener/user")
public class UserController extends BaseController {

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<?> registerUser(@RequestBody UserRequestDto userRequestDto) {
        ResponseEntity<RestResponse<UserResponseDto>> responseEntity;
        final RestResponse<UserResponseDto> restResponse = new RestResponse<>();

        if (userRequestDto == null) {
            final ErrorResponseData errorResponseData = ErrorResponseData.builder()
                    .errorMessage("No user data provided. Please provide a valid user data.")
                    .errorCode(HttpStatus.BAD_REQUEST.toString())
                    .build();
            restResponse.setErrorData(errorResponseData);
            responseEntity = new ResponseEntity<RestResponse<UserResponseDto>>(restResponse, HttpStatus.BAD_REQUEST);
        } else {
            try {
                final UserResponseDto UserResponseDto = userService.registerUser(userRequestDto);
                restResponse.setResult(UserResponseDto);
                responseEntity = new ResponseEntity<RestResponse<UserResponseDto>>(restResponse, HttpStatus.CREATED);
            } catch (final Exception e) {
                responseEntity = getErrorResponseEntity(e, restResponse, "Failed to save user data");
            }
        }

        return responseEntity;
    }
}
