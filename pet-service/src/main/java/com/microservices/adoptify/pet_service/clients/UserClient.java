package com.microservices.adoptify.pet_service.clients;

import com.microservices.adoptify.pet_service.external.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "USER-SERVICE")
public interface UserClient {

    @GetMapping("/api/users/{userId}")
    User getUserById(@PathVariable("userId") Long userId, @RequestHeader("Authorization") String token);
}
