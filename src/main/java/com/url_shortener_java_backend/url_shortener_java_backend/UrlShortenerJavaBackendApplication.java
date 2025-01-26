package com.url_shortener_java_backend.url_shortener_java_backend;

import com.url_shortener_java_backend.url_shortener_java_backend.constants.UrlShortenerConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class UrlShortenerJavaBackendApplication {

	@Autowired
	private UrlShortenerConstant urlShortenerConstant;

	public static void main(String[] args) {
		SpringApplication.run(UrlShortenerJavaBackendApplication.class, args);
	}

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**")
						.allowedOrigins(urlShortenerConstant.getFrontendCloudUrl(), urlShortenerConstant.getFrontendLocalUrl())
						.allowedMethods("GET", "PUT", "POST", "PATCH", "DELETE", "OPTIONS");
			}
		};
	}

}
