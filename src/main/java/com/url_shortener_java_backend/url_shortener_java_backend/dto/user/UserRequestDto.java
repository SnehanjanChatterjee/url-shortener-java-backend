package com.url_shortener_java_backend.url_shortener_java_backend.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserRequestDto {
    private String userId;
    private String name;
    private String email;
    private String image;
}
