package com.url_shortener_java_backend.url_shortener_java_backend.service.user;

import com.url_shortener_java_backend.url_shortener_java_backend.dto.user.UserRequestDto;
import com.url_shortener_java_backend.url_shortener_java_backend.dto.user.UserResponseDto;
import com.url_shortener_java_backend.url_shortener_java_backend.entity.User;
import com.url_shortener_java_backend.url_shortener_java_backend.repository.UserRepository;
import com.url_shortener_java_backend.url_shortener_java_backend.util.UrlShortenerUtil;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UrlShortenerUtil urlShortenerUtil;

    @Override
    public UserResponseDto registerUser(@NonNull UserRequestDto userRequestDto) {
        final User user = User.builder()
                .userId(userRequestDto.getUserId())
                .name(userRequestDto.getName())
                .email(userRequestDto.getEmail())
                .image(userRequestDto.getImage())
                .createdAt(LocalDateTime.now())
                .build();
        final User savedUser = userRepository.save(user);
        return urlShortenerUtil.buildUserResponseDto(savedUser);
    }
}
