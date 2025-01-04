package com.microservices.adoptify.user_service.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.microservices.adoptify.user_service.configuration.JWTService;
import com.microservices.adoptify.user_service.configuration.UserDetailsImpl;
import com.microservices.adoptify.user_service.dto.UserAndJwtDTO;
import com.microservices.adoptify.user_service.dto.UserDTO;
import com.microservices.adoptify.user_service.model.User;
import com.microservices.adoptify.user_service.repository.UserRepository;
import com.microservices.adoptify.user_service.service.impl.UserServiceImpl;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
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

  @Test
  void getAllUsers_ShouldReturnListOfUserDTOs() {
    when(userRepository.findAll()).thenReturn(Arrays.asList(user1, user2));

    List<UserDTO> users = userService.getAllUsers();

    assertEquals(2, users.size(), "Should return two users");
    assertEquals("some1", users.get(0).getUsername(), "First user's username should be 'some1'");
    assertEquals("some2", users.get(1).getUsername(), "Second user's username should be 'some2'");
    verify(userRepository, times(1)).findAll();
  }

  @Test
  void registerUser_ShouldRegisterSuccessfully() {
    User newUser = new User("newUser", "newUser@somemock.com", "password123", "USER", "1234567890");
    User savedUser =
        new User("newUser", "newUser@somemock.com", "password123", "USER", "1234567890");

    savedUser.setUserId(3L);

    when(userRepository.existsByUsername("newUser")).thenReturn(false);
    when(userRepository.save(any(User.class))).thenReturn(savedUser);
    when(jwtService.generateToken(3L)).thenReturn("token");

    UserAndJwtDTO result = userService.registerUser(newUser);

    assertNotNull(result, "Result should not be null");
    assertEquals("newUser", result.getUser().getUsername(), "Username should be 'newUser'");
    assertEquals("token", result.getToken(), "Token should be 'token'");
    verify(userRepository, times(1)).existsByUsername("newUser");
    verify(userRepository, times(1)).save(any(User.class));
    verify(jwtService, times(1)).generateToken(3L);
  }

  @Test
  void registerUser_ShouldThrowExceptionWhenUserAlreadyExists() {
    User user =
        new User(
            "duplicateUser", "duplicateUser@something.com", "password123", "USER", "1234567890");

    when(userRepository.existsByUsername("duplicateUser")).thenReturn(true);

    RuntimeException exception =
        assertThrows(
            RuntimeException.class,
            () -> userService.registerUser(user),
            "Should throw RuntimeException");

    assertEquals("Username already exists", exception.getMessage());
    verify(userRepository, times(1)).existsByUsername("duplicateUser");
    verify(userRepository, never()).save(any(User.class));
    verify(jwtService, never()).generateToken(anyLong());
  }

  @Test
  void verify_shouldAuthenticateSuccessfully() {
    User credentials =
        new User("validUser", "valid@example.com", "validPassword", "USER", "7777777777");

    Authentication auth = mock(Authentication.class);
    UserDetailsImpl userDetails = mock(UserDetailsImpl.class);

    when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(auth);
    when(auth.isAuthenticated()).thenReturn(true);
    when(auth.getPrincipal()).thenReturn(userDetails);
    when(userDetails.getUserId()).thenReturn(1L);
    when(jwtService.generateToken(1L)).thenReturn("jwt-token");
    when(userRepository.findById(1L)).thenReturn(Optional.of(credentials));

    UserAndJwtDTO userAndJwtDTO = userService.verify(credentials);

    assertNotNull(userAndJwtDTO, "User should not be null");
    assertEquals(
        "validUser", userAndJwtDTO.getUser().getUsername(), "Username should be 'validUser'");
    assertEquals("jwt-token", userAndJwtDTO.getToken(), "Token should be 'jwt-token'");
    verify(authenticationManager, times(1)).authenticate(any(Authentication.class));
    verify(auth, times(1)).isAuthenticated();
    verify(userRepository, times(1)).findById(1L);
    verify(jwtService, times(1)).generateToken(1L);
  }

  @Test
  void verify_shouldThrowExceptionForInvalidCredentials() {
    User invalidCredentials =
        new User("invalidUser", "invalid@example.com", "wrongPassword", "USER", "8888888888");

    when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
        .thenThrow(new AuthenticationException("Invalid credentials") {});

    AuthenticationException exception =
        assertThrows(
            AuthenticationException.class,
            () -> userService.verify(invalidCredentials),
            "Should throw AuthenticationException");

    assertEquals(
        "Invalid username or password", exception.getMessage(), "exception message does not match");
    verify(authenticationManager, times(1))
        .authenticate(any(UsernamePasswordAuthenticationToken.class));
    verify(jwtService, never()).generateToken(anyLong());
    verify(userRepository, never()).findById(anyLong());
  }

  @Test
  void getUser_ShouldReturnUserDTO_WhenUserExists() {
    when(userRepository.findById(1L)).thenReturn(Optional.of(user1));

    UserDTO result = userService.getUser(1L);

    assertNotNull(result, "Result should not be null");
    assertEquals("some1", result.getUsername(), "Username should match");
    verify(userRepository, times(1)).findById(1L);
  }

  @Test
  void getUser_ShouldReturnNull_WhenUserDoesNotExist() {
    when(userRepository.findById(99L)).thenReturn(Optional.empty());

    UserDTO result = userService.getUser(99L);

    assertNull(result, "Result should be null for non-existing user");
    verify(userRepository, times(1)).findById(99L);
  }

  @Test
  void addUser_ShouldAddUserSuccessfully() {
    User newUser = new User("addUser", "adduser@example.com", "password", "USER", "9999999999");

    when(userRepository.save(newUser)).thenReturn(newUser);

    String result = userService.addUser(newUser);

    assertEquals("User created", result, "Should return success message");
    verify(userRepository, times(1)).save(newUser);
  }

  @Test
  void updateUser_ShouldUpdateExistingUserSuccessfully() {
    Long userId = 1L;
    UserDTO updatedUserDTO = new UserDTO();
    updatedUserDTO.setUsername("updatedUsername");
    updatedUserDTO.setEmail("updated@example.com");
    updatedUserDTO.setRoles("ADMIN");
    updatedUserDTO.setPhoneNumber("1111111111");

    when(userRepository.findById(userId)).thenReturn(Optional.of(user1));

    boolean result = userService.updateUser(userId, updatedUserDTO);

    assertTrue(result, "Update should return true for existing user");
    assertEquals("updatedUsername", user1.getUsername(), "Username should be updated");
    assertEquals("updated@example.com", user1.getEmail(), "Email should be updated");
    assertEquals("ADMIN", user1.getRoles(), "Roles should be updated");
    assertEquals("1111111111", user1.getPhoneNumber(), "Phone number should be updated");

    verify(userRepository, times(1)).save(any(User.class));
    verify(userRepository, times(1)).findById(userId);
  }

  @Test
  void updateUser_ShouldReturnFalse_WhenUserDoesNotExist() {
    Long userId = 99L;
    UserDTO updatedUserDTO = new UserDTO();
    updatedUserDTO.setUsername("nonExistentUser");

    when(userRepository.findById(userId)).thenReturn(Optional.empty());

    boolean result = userService.updateUser(userId, updatedUserDTO);

    assertFalse(result, "Update should return false for non-existing user");
    verify(userRepository, times(1)).findById(userId);
  }

  @Test
  void deleteUser_ShouldDeleteExistingUserSuccessfully() {
    Long userId = 1L;
    when(userRepository.findById(userId)).thenReturn(Optional.of(user1));
    doNothing().when(userRepository).delete(user1);

    boolean result = userService.deleteUser(userId);

    assertTrue(result, "Delete should return true for existing user");
    verify(userRepository, times(1)).findById(userId);
    verify(userRepository, times(1)).delete(user1);
  }

  @Test
  void deleteUser_ShouldReturnFalse_WhenUserDoesNotExist() {
    Long userId = 99L;
    when(userRepository.findById(userId)).thenReturn(Optional.empty());

    boolean result = userService.deleteUser(userId);

    assertFalse(result, "Delete should return false for non-existing user");
    verify(userRepository, times(1)).findById(userId);
    verify(userRepository, never()).delete(any(User.class));
  }
}
