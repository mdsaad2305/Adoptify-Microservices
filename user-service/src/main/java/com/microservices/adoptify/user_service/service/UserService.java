package com.microservices.adoptify.user_service.service;

import com.microservices.adoptify.user_service.dto.UserAndJwtDTO;
import com.microservices.adoptify.user_service.dto.UserDTO;
import com.microservices.adoptify.user_service.model.User;
import java.util.List;

public interface UserService {

  public UserAndJwtDTO registerUser(User user);

  public UserAndJwtDTO verify(User user);

  public List<UserDTO> getAllUsers();

  public UserDTO getUser(Long userId);

  public String addUser(User user);

  public boolean updateUser(Long userId, UserDTO updatedUser);

  public boolean deleteUser(Long userId);
}
