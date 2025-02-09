package com.url_shortener_java_backend.url_shortener_java_backend.controller;

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
            responseEntity = getErrorResponseEntity(null, HttpStatus.BAD_REQUEST, restResponse, "No user data provided. Please provide a valid user data.");
        } else {
            try {
                final UserResponseDto UserResponseDto = userService.registerUser(userRequestDto);
                restResponse.setResult(UserResponseDto);
                responseEntity = new ResponseEntity<>(restResponse, HttpStatus.CREATED);
            } catch (final Exception e) {
                responseEntity = getErrorResponseEntity(e, HttpStatus.INTERNAL_SERVER_ERROR, restResponse, "Failed to save user data");
            }
        }

        return responseEntity;
    }
}
