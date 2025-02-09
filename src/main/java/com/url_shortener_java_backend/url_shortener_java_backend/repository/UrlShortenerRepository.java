package com.url_shortener_java_backend.url_shortener_java_backend.repository;

import com.url_shortener_java_backend.url_shortener_java_backend.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UrlShortenerRepository extends JpaRepository<Url, Long> {
    Url findByShortUrl(String shortUrl);
    Url findByShortUrlAndUser_UserId(String shortUrl, String userId);
    List<Url> findByUser_UserId(String userId);
}
