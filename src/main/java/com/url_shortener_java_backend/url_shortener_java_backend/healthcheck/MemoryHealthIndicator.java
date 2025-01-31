package com.url_shortener_java_backend.url_shortener_java_backend.healthcheck;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import static com.url_shortener_java_backend.url_shortener_java_backend.util.UrlShortenerUtil.getCurrentDateTimeInIndia;

@Component
public class MemoryHealthIndicator implements HealthIndicator {

    @Override
    @Scheduled(cron = "0 0 * * * *")
    public Health health() {
        System.out.println("Memory health check started at " + getCurrentDateTimeInIndia());
        final long freeMemory = Runtime.getRuntime().freeMemory();
        final long totalMemory = Runtime.getRuntime().totalMemory();
        final double freeMemoryPercent = ((double) freeMemory / (double) totalMemory) * 100;
        if (freeMemoryPercent > 5) {
            System.out.println("Memory health check finished at " + getCurrentDateTimeInIndia() + ". Health is up");
            return Health.up()
                    .withDetail("free_memory", freeMemory + " bytes")
                    .withDetail("total_memory", totalMemory + " bytes")
                    .withDetail("free_memory_percent", freeMemoryPercent + "%")
                    .build();
        } else {
            System.out.println("Memory health check finished at " + getCurrentDateTimeInIndia() + ". Health is down");
            return Health.down()
                    .withDetail("free_memory", freeMemory + " bytes")
                    .withDetail("total_memory", totalMemory + " bytes")
                    .withDetail("free_memory_percent", freeMemoryPercent + "%")
                    .build();
        }
    }
}