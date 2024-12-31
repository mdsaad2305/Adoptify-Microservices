package com.microservices.adoptify.user_service.service.impl;

import com.microservices.adoptify.user_service.configuration.JWTService;
import com.microservices.adoptify.user_service.dto.UserDTO;
import com.microservices.adoptify.user_service.mapper.UserMapper;
import com.microservices.adoptify.user_service.model.User;
import com.microservices.adoptify.user_service.repository.UserRepository;
import com.microservices.adoptify.user_service.service.UserService;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final AuthenticationManager authenticationManager;
  private final JWTService jwtService;
  private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

  public UserServiceImpl(
      UserRepository userRepository,
      AuthenticationManager authenticationManager,
      JWTService jwtService) {
    this.userRepository = userRepository;
    this.authenticationManager = authenticationManager;
    this.jwtService = jwtService;
  }

  @Override
  public User registerUser(User user) {
    user.setPassword(encoder.encode(user.getPassword()));
    return userRepository.save(user);
  }

  @Override
  public String Verify(User user) {
    Authentication authentication =
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));

    if (authentication.isAuthenticated()) {
      return jwtService.generateToken(user.getUsername());
    } else {
      return "Invalid username or password";
    }
  }

  @Override
  public List<UserDTO> getAllUsers() {
    List<User> users = userRepository.findAll();
    List<UserDTO> userDTOs = new ArrayList<>();
    for (User user : users) {
      UserDTO userDTO = convertToUserDTO(user);
      userDTOs.add(userDTO);
    }
    return userDTOs;
  }

  @Override
  public UserDTO getUser(Long userId) {
    User user = userRepository.findById(userId).orElse(null);
    return this.convertToUserDTO(user);
  }

  @Override
  public String addUser(User user) {
    userRepository.save(user);
    return "User created";
  }

  @Override
  public boolean updateUser(Long userId, UserDTO updatedUser) {
    Optional<User> optionalUser = userRepository.findById(userId);
    if (optionalUser.isPresent()) {
      User user = optionalUser.get();
      if (updatedUser.getUsername() != null) {
        user.setUsername(updatedUser.getUsername());
      }
      if (updatedUser.getEmail() != null) {
        user.setEmail(updatedUser.getEmail());
      }
      if (updatedUser.getRoles() != null) {
        user.setRoles(updatedUser.getRoles());
      }
      if (updatedUser.getPhoneNumber() != null) {
        user.setPhoneNumber(updatedUser.getPhoneNumber());
      }
      return true;
    }
    return false;
  }

  @Override
  public boolean deleteUser(Long userId) {
    Optional<User> optionalUser = userRepository.findById(userId);
    if (optionalUser.isPresent()) {
      User user = optionalUser.get();
      userRepository.delete(user);
      return true;
    }
    return false;
  }

  private UserDTO convertToUserDTO(User user) {
    return UserMapper.toDTO(user);
  }
}
