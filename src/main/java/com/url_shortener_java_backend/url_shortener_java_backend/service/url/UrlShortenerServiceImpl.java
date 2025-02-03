package com.url_shortener_java_backend.url_shortener_java_backend.service.url;

import com.url_shortener_java_backend.url_shortener_java_backend.constants.UrlShortenerConstant;
import com.url_shortener_java_backend.url_shortener_java_backend.dto.url.UrlRequestDto;
import com.url_shortener_java_backend.url_shortener_java_backend.dto.url.UrlResponseDto;
import com.url_shortener_java_backend.url_shortener_java_backend.entity.Url;
import com.url_shortener_java_backend.url_shortener_java_backend.repository.UrlShortenerRepository;
import com.url_shortener_java_backend.url_shortener_java_backend.util.UrlShortenerUtil;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class UrlShortenerServiceImpl implements UrlShortenerService {

    @Autowired
    private UrlShortenerRepository urlShortenerRepository;

    @Autowired
    private UrlShortenerConstant urlShortenerConstant;

    @Autowired
    private UrlShortenerUtil urlShortenerUtil;

    @Override
    public UrlResponseDto generateAndPersistShortUrl(@NonNull final UrlRequestDto urlRequestDto) {
        final String originalUrl = urlRequestDto.getUrl();
        final String shortenedUrl = urlShortenerUtil.convertToShortUrl(originalUrl);
        final LocalDateTime currentTime = LocalDateTime.now();
        final LocalDateTime expirationTime = urlShortenerUtil.getExpirationTime(currentTime);

        final Url url = Url.builder()
                .shortUrl(shortenedUrl)
                .originalUrl(originalUrl)
                .createdAt(currentTime)
                .expiresAt(expirationTime)
                .build();
        urlShortenerRepository.save(url);

        return urlShortenerUtil.buildUrlResponseDto(url);
    }

    @Override
    public UrlResponseDto getOriginalUrl(final String shortUrlCode) throws Exception {
        final String shortUrl = urlShortenerConstant.getShortenedUrlBase().concat("/").concat(shortUrlCode);
        final Url url = urlShortenerRepository.findByShortUrl(shortUrl);
        if (url == null) {
            throw new Exception("No original url found which have a shortened url " + shortUrl);
        }
        return urlShortenerUtil.buildUrlResponseDto(url);
    }

    @Override
    public List<UrlResponseDto> getAllOriginalUrls() {
        final List<Url> urls = urlShortenerRepository.findAll();
        final List<UrlResponseDto> urlResponseDtoList = urls.stream()
                .map(urlShortenerUtil::buildUrlResponseDto)
                .toList();
        return urlResponseDtoList;
    }

    @Override
    public void deleteShortUrl(final String shortUrlCode) {
        final String shortUrl = urlShortenerConstant.getShortenedUrlBase().concat("/").concat(shortUrlCode);
        final Url url = urlShortenerRepository.findByShortUrl(shortUrl);
        urlShortenerRepository.delete(url);
    }

    @Override
    public void deleteAllShortUrls() {
        urlShortenerRepository.deleteAll();
    }
}
