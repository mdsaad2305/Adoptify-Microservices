package com.microservices.adoptify.user_service.service;

import com.microservices.adoptify.user_service.model.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

public interface UserService {

    public List<User> getAllUsers();

    public Optional<User> getUser(Long userId);

    public String addUser(User user);

    public boolean updateUser(Long userId, User updatedUser);

    public boolean deleteUser(Long userId);

}
