package com.url_shortener_java_backend.url_shortener_java_backend.service.user;

import com.url_shortener_java_backend.url_shortener_java_backend.dto.user.UserRequestDto;
import com.url_shortener_java_backend.url_shortener_java_backend.dto.user.UserResponseDto;
import lombok.NonNull;
import org.springframework.stereotype.Service;

@Service
public interface UserService {
    public UserResponseDto registerUser(@NonNull final UserRequestDto userRequestDto);
}
