package com.microservices.adoptify.user_service.service.impl;

import com.microservices.adoptify.user_service.model.User;
import com.microservices.adoptify.user_service.repository.UserRepository;
import com.microservices.adoptify.user_service.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> getUser(Long userId) {
        return userRepository.findById(userId);
    }

    @Override
    public String addUser(User user) {
        userRepository.save(user);
        return "User created";
    }

    @Override
    public boolean updateUser(Long userId, User updatedUser) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if(optionalUser.isPresent()){
            User user = optionalUser.get();
            if(updatedUser.getUsername() != null) {
                user.setUsername(updatedUser.getUsername());
            }
            if(updatedUser.getEmail() != null) {
                user.setEmail(updatedUser.getEmail());
            }
            if(updatedUser.getRoles() != null) {
                user.setRoles(updatedUser.getRoles());
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteUser(Long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if(optionalUser.isPresent()){
            User user = optionalUser.get();
            userRepository.delete(user);
            return true;
        }
        return false;
    }
}
