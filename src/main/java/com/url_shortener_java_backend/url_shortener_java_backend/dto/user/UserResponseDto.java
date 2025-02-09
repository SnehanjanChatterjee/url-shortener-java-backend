package com.url_shortener_java_backend.url_shortener_java_backend.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDto {
    private long id;
    private String userId;
    private String name;
    private String email;
    private String image;
    private LocalDateTime createdAt;
}
