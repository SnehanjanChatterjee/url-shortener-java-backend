package com.url_shortener_java_backend.url_shortener_java_backend.repository;

import com.url_shortener_java_backend.url_shortener_java_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
