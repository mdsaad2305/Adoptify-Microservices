package com.microservices.adoptify.user_service.dto;

import com.microservices.adoptify.user_service.model.User;

public class UserAndJwtDTO {
    private User user;
    private String token;

    public UserAndJwtDTO() {
    }

    public UserAndJwtDTO(User user, String token) {
        this.user = user;
        this.token = token;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
