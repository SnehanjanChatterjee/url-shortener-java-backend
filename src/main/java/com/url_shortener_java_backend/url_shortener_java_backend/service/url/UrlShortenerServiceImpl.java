package com.url_shortener_java_backend.url_shortener_java_backend.service.url;

import com.url_shortener_java_backend.url_shortener_java_backend.constants.UrlShortenerConstant;
import com.url_shortener_java_backend.url_shortener_java_backend.dto.url.UrlRequestDto;
import com.url_shortener_java_backend.url_shortener_java_backend.dto.url.UrlResponseDto;
import com.url_shortener_java_backend.url_shortener_java_backend.entity.Url;
import com.url_shortener_java_backend.url_shortener_java_backend.entity.User;
import com.url_shortener_java_backend.url_shortener_java_backend.repository.UrlShortenerRepository;
import com.url_shortener_java_backend.url_shortener_java_backend.repository.UserRepository;
import com.url_shortener_java_backend.url_shortener_java_backend.util.UrlShortenerUtil;
import lombok.NonNull;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class UrlShortenerServiceImpl implements UrlShortenerService {

    @Autowired
    private UrlShortenerRepository urlShortenerRepository;

    @Autowired
    private UserRepository userRepository;

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
        final String userId = urlRequestDto.getUserId();
        User user = null;
        if (StringUtils.isNotBlank(userId)) {
            user = userRepository.findByUserId(userId);
        }

        final Url url = Url.builder()
                .shortUrl(shortenedUrl)
                .originalUrl(originalUrl)
                .createdAt(currentTime)
                .expiresAt(expirationTime)
                .user(user)
                .build();
        final Url savedUrl = urlShortenerRepository.save(url);

        return urlShortenerUtil.buildUrlResponseDto(savedUrl);
    }

    @Override
    public UrlResponseDto getOriginalUrl(final String shortUrlCode) throws Exception {
        final String shortUrl = urlShortenerUtil.constructShortUrl(shortUrlCode);
        final Url url = urlShortenerRepository.findByShortUrl(shortUrl);
        if (url == null) {
            throw new Exception("No original url found which have a shortened url " + shortUrl);
        }
        return urlShortenerUtil.buildUrlResponseDto(url);
    }

    @Override
    public List<UrlResponseDto> getAllOriginalUrls(final String userId) {
        final List<Url> urls = urlShortenerRepository.findByUser_UserId(userId);
        return urls.stream()
                .map(urlShortenerUtil::buildUrlResponseDto)
                .toList();
    }

    @Transactional
    @Override
    public void deleteShortUrl(final String shortUrlCode, final String userId) {
        final String shortUrl = urlShortenerUtil.constructShortUrl(shortUrlCode);
        final Url url = urlShortenerRepository.findByShortUrlAndUser_UserId(shortUrl, userId);
        urlShortenerRepository.delete(url);
    }

    @Transactional
    @Override
    public void deleteAllShortUrls(final String userId) {
        final List<Url> userUrls = urlShortenerRepository.findByUser_UserId(userId);
        if (CollectionUtils.isNotEmpty(userUrls)) {
            urlShortenerRepository.deleteAll(userUrls);
        }
    }
}
