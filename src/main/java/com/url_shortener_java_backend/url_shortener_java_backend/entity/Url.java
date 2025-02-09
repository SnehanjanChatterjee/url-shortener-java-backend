package com.url_shortener_java_backend.url_shortener_java_backend.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "url")
public class Url extends AbstractEntity {
    @Lob
    private String originalUrl;
    private String shortUrl;
    private LocalDateTime expiresAt;
    @ManyToOne(fetch = FetchType.LAZY)  // Many URLs can belong to one user
    @JoinColumn(name = "user_id", referencedColumnName = "userId")  // Foreign key column, nullable is true for guest user. If referencedColumnName is not set, by default it will refer to the primary key "id" column of User table
    private User user;
}
