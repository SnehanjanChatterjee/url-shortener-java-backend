package com.url_shortener_java_backend.url_shortener_java_backend.util;

import com.google.common.hash.Hashing;
import org.apache.commons.validator.routines.UrlValidator;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static com.url_shortener_java_backend.url_shortener_java_backend.constants.UrlShortenerConstant.SHORTENED_URL_BASE;

public class UrlShortenerUtil {
    public static LocalDateTime getExpirationTime(final LocalDateTime dateTime) {
        return dateTime.plusSeconds(10);
    }

    public static String convertToShortUrl(final String originalUrl) {
        final UrlValidator urlValidator = new UrlValidator(new String[]{"http", "https"});
        if (urlValidator.isValid(originalUrl)) {
            final LocalDateTime time = LocalDateTime.now();
            final String encodedString = Hashing.murmur3_32_fixed()
                    .hashString(originalUrl.concat(time.toString()), StandardCharsets.UTF_8)
                    .toString();
            final String shortenedUrl = SHORTENED_URL_BASE.concat(encodedString);
            return shortenedUrl;
        }
        return null;
    }
}
