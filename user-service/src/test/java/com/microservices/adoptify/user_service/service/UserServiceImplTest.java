package com.microservices.adoptify.user_service.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.microservices.adoptify.user_service.configuration.JWTService;
import com.microservices.adoptify.user_service.dto.UserDTO;
import com.microservices.adoptify.user_service.model.User;
import com.microservices.adoptify.user_service.repository.UserRepository;
import com.microservices.adoptify.user_service.service.impl.UserServiceImpl;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class UserServiceImplTest {

  @Mock private UserRepository userRepository;

  @Mock private AuthenticationManager authenticationManager;

  @Mock private JWTService jwtService;

  @InjectMocks private UserServiceImpl userService;

  private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);

  private User user1;
  private User user2;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    user1 = new User("some1", "someone1@somemock.com", "password123", "USER", "1234567890");
    user2 = new User("some2", "someone2@something.com", "password456", "ADMIN", "0987654321");
  }

  //  @Test
  //  void registerUser_ShouldSaveWithEncodedPassword() {
  //    when(userRepository.save(Mockito.any(User.class))).thenReturn(user1);
  //
  //    UserAndJwtDTO registered_user = userService.registerUser(user1);
  //
  //    assertNotNull(registered_user);
  //    assertNotEquals(
  //        "password123", registered_user.getUser().getPassword(), "Password should be encoded");
  //    assertTrue(passwordEncoder.matches("password123", registered_user.getUser().getPassword()));
  //    verify(userRepository, times(1)).save(any(User.class));
  //  }

  @Test
  void getAllUsers_ShouldReturnListOfUserDTOs() {
    when(userRepository.findAll()).thenReturn(Arrays.asList(user1, user2));

    List<UserDTO> users = userService.getAllUsers();

    assertEquals(2, users.size(), "Should return two users");
    assertEquals("some1", users.get(0).getUsername(), "First user's username should be 'some1'");
    assertEquals("some2", users.get(1).getUsername(), "Second user's username should be 'some2'");
    verify(userRepository, times(1)).findAll();
  }
}
