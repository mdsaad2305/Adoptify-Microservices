package com.microservices.adoptify.user_service.service.impl;

import com.microservices.adoptify.user_service.configuration.JWTService;
import com.microservices.adoptify.user_service.configuration.UserDetailsImpl;
import com.microservices.adoptify.user_service.dto.UserAndJwtDTO;
import com.microservices.adoptify.user_service.dto.UserDTO;
import com.microservices.adoptify.user_service.mapper.UserMapper;
import com.microservices.adoptify.user_service.model.User;
import com.microservices.adoptify.user_service.repository.UserRepository;
import com.microservices.adoptify.user_service.service.UserService;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

  private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
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
  public UserAndJwtDTO registerUser(User user) {
    // Check for duplicate username
    if (userRepository.existsByUsername(user.getUsername())) {
      logger.warn("Attempted to register with duplicate username: {}", user.getUsername());
      throw new RuntimeException("Username already exists");
    }

    user.setPassword(encoder.encode(user.getPassword()));
    logger.info("Password encoded for user: {}", user.getUsername());

    User savedUser = userRepository.save(user);
    logger.info("User registered successfully with userId: {}", savedUser.getUserId());

    String token = jwtService.generateToken(savedUser.getUserId());
    logger.debug("JWT token generated for userId {}: {}", savedUser.getUserId(), token);

    UserDTO userDTO = convertToUserDTO(savedUser);
    UserAndJwtDTO userAndJwtDTO = new UserAndJwtDTO();
    userAndJwtDTO.setUser(userDTO);
    userAndJwtDTO.setToken(token);

    return userAndJwtDTO;
  }

  @Override
  public UserAndJwtDTO verify(User user) {
    try {
      Authentication authentication =
          authenticationManager.authenticate(
              new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));

      if (authentication.isAuthenticated()) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Long userId = userDetails.getUserId();
        logger.info("User authenticated successfully with userId: {}", userId);

        String token = jwtService.generateToken(userId);
        logger.debug("JWT token generated for userId {}: {}", userId, token);

        UserAndJwtDTO userAndJwtDTO = new UserAndJwtDTO();
        userAndJwtDTO.setUser(convertToUserDTO(userRepository.findById(userId).orElse(null)));
        userAndJwtDTO.setToken(token);

        return userAndJwtDTO;
      } else {
        logger.warn("Authentication failed for username: {}", user.getUsername());
        throw new AuthenticationException("Invalid username or password") {};
      }
    } catch (AuthenticationException e) {
      logger.error("AuthenticationException for user: {}", user.getUsername(), e);
      throw new AuthenticationException("Invalid username or password") {};
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
      userRepository.save(user);
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
    if(user == null){
      return null;
    }
    return UserMapper.toDTO(user);
  }
}
