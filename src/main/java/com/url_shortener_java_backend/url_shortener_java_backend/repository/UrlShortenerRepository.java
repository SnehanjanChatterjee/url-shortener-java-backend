package com.url_shortener_java_backend.url_shortener_java_backend.repository;

import com.url_shortener_java_backend.url_shortener_java_backend.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UrlShortenerRepository extends JpaRepository<Url, Long> {
    public Url findByShortUrl(String shortUrl);
}
