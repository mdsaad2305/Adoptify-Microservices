package com.microservices.adoptify.user_service.repository;

import com.microservices.adoptify.user_service.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
