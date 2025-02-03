package com.url_shortener_java_backend.url_shortener_java_backend.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "app_user")
public class User extends AbstractEntity {
    @Column(unique = true, nullable = false)
    private String userId;
    private String name;
    private String email;
    private String picture;
}
