package com.microservices.adoptify.user_service.dto;

public class UserAndJwtDTO {
  private UserDTO user;
  private String token;

  public UserAndJwtDTO() {}

  public UserAndJwtDTO(UserDTO user, String token) {
    this.user = user;
    this.token = token;
  }

  public UserDTO getUser() {
    return user;
  }

  public void setUser(UserDTO user) {
    this.user = user;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }
}
