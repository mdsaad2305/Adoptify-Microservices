package com.microservices.adoptify.user_service.service;

import com.microservices.adoptify.user_service.dto.UserDTO;
import com.microservices.adoptify.user_service.model.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

public interface UserService {

    public User registerUser(User user);

    boolean Verify(User user);

    public List<UserDTO> getAllUsers();

    public UserDTO getUser(Long userId);

    public String addUser(User user);

    public boolean updateUser(Long userId, UserDTO updatedUser);

    public boolean deleteUser(Long userId);

}
