package com.microservices.adoptify.user_service.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservices.adoptify.user_service.configuration.JWTService;
import com.microservices.adoptify.user_service.dto.UserAndJwtDTO;
import com.microservices.adoptify.user_service.dto.UserDTO;
import com.microservices.adoptify.user_service.mapper.UserMapper;
import com.microservices.adoptify.user_service.messaging.UserEmailProducer;
import com.microservices.adoptify.user_service.model.User;
import com.microservices.adoptify.user_service.service.UserService;
import java.util.Arrays;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UserController.class)
@WithMockUser // I added this for mocking authenticated user
public class UserControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private UserService userService;

  @MockitoBean private UserEmailProducer userEmailProducer;

  @MockitoBean private JWTService jwtService;

  @Autowired private ObjectMapper objectMapper;

  private User user1;
  private User user2;
  private UserDTO userDTO1;
  private UserDTO userDTO2;
  private UserAndJwtDTO userAndJwtDTO;

  @BeforeEach
  void setup() {
    user1 = new User("some1", "someone1@somemock.com", "password123", "USER", "1234567890");
    user1.setUserId(1L);
    user2 = new User("some2", "someone2@something.com", "password456", "ADMIN", "0987654321");
    user2.setUserId(2L);

    userDTO1 = UserMapper.toDTO(user1);
    userDTO2 = UserMapper.toDTO(user2);

    userAndJwtDTO = new UserAndJwtDTO(userDTO1, "jwt-token");
  }

  @Test
  void getAllUsers_ShouldReturnListOfUserDTOs() throws Exception {
    // Arrange
    when(userService.getAllUsers()).thenReturn(Arrays.asList(userDTO1, userDTO2));

    // Act & Assert
    mockMvc
        .perform(get("/api/users"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.size()").value(2))
        .andExpect(jsonPath("$[0].username").value("some1"))
        .andExpect(jsonPath("$[1].username").value("some2"));

    verify(userService, times(1)).getAllUsers();
  }

  @Test
  void getAllUsers_ShouldReturnEmptyList() throws Exception {
    when(userService.getAllUsers()).thenReturn(Collections.emptyList());

    mockMvc
        .perform(get("/api/users"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.size()").value(0));

    verify(userService, times(1)).getAllUsers();
  }

  @Test
  void getUserById_ShoulReturnUserDTO() throws Exception {
    when(userService.getUser(1L)).thenReturn(userDTO1);

    mockMvc
        .perform(get("/api/users/{userId}", 1L))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.username").value("some1"))
        .andExpect(jsonPath("$.email").value("someone1@somemock.com"));

    verify(userService, times(1)).getUser(1L);
  }

  @Test
  void getUserById_ShouldReturnNotFound_WhenUserDoesNotExist() throws Exception {
    when(userService.getUser(99L)).thenReturn(null);

    mockMvc.perform(get("/api/users/{userId}", 99L)).andExpect(status().isNotFound());

    verify(userService, times(1)).getUser(99L);
  }

  //    @Test
  //    @WithAnonymousUser
  //    void registerUser_ShouldRegisterSuccessfully() throws Exception {
  //        User newUser = new User("newUser", "newuser@example.com", "plainPassword", "USER",
  // "5555555555");
  //        String newUserJson = objectMapper.writeValueAsString(newUser);
  //
  //        when(userService.registerUser(any(User.class))).thenReturn(userAndJwtDTO);
  //
  //        mockMvc.perform(post("/api/users/register")
  //                        .contentType(MediaType.APPLICATION_JSON)
  //                        .content(newUserJson))
  //                .andExpect(status().isCreated())
  //                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
  //                .andExpect(jsonPath("$.user.username").value("some1"))
  //                .andExpect(jsonPath("$.token").value("jwt-token"));
  //
  //        verify(userService, times(1)).registerUser(any(User.class));
  //        verify(userEmailProducer, times(1)).sendMessage(any(User.class));
  //    }
  //
  //    @Test
  //    @WithAnonymousUser
  //    void registerUser_ShouldReturnBadRequest_WhenUsernameAlreadyExists() throws Exception {
  //        User duplicateUser = new User("duplicateUser", "dup@example.com", "password", "USER",
  // "6666666666");
  //        String duplicateUserJson = objectMapper.writeValueAsString(duplicateUser);
  //
  //        when(userService.registerUser(any(User.class)))
  //                .thenThrow(new RuntimeException("Username already exists"));
  //
  //        mockMvc.perform(post("/api/users/register")
  //                        .contentType(MediaType.APPLICATION_JSON)
  //                        .content(duplicateUserJson))
  //                .andExpect(status().isBadRequest())
  //                .andExpect(status().reason("Username already exists"));
  //
  //        verify(userService, times(1)).registerUser(any(User.class));
  //        verify(userEmailProducer, never()).sendMessage(any(User.class));
  //    }
  // @Test
  // void updateUser_ShouldUpdateExistingUserSuccessfully() throws Exception {
  //    Long userId = 1L;
  //    UserDTO updatedUserDTO = new UserDTO();
  //    updatedUserDTO.setUsername("updatedUsername");
  //    updatedUserDTO.setEmail("updated@example.com");
  //    updatedUserDTO.setRoles("ADMIN");
  //    updatedUserDTO.setPhoneNumber("1111111111");
  //    String updatedUserJson = objectMapper.writeValueAsString(updatedUserDTO);
  //
  //    when(userService.updateUser(eq(userId), any(UserDTO.class))).thenReturn(true);
  //
  //    mockMvc.perform(patch("/api/users/{userId}", userId)
  //                    .contentType(MediaType.APPLICATION_JSON)
  //                    .content(updatedUserJson))
  //            .andExpect(status().isOk())
  //            .andExpect(content().string("User updated successfully"));
  //
  //    verify(userService, times(1)).updateUser(eq(userId), any(UserDTO.class));
  // }
  //
  //    @Test
  //    void updateUser_ShouldReturnNotFound_WhenUserDoesNotExist() throws Exception {
  //        Long userId = 99L;
  //        UserDTO updatedUserDTO = new UserDTO();
  //        updatedUserDTO.setUsername("nonExistentUser");
  //        String updatedUserJson = objectMapper.writeValueAsString(updatedUserDTO);
  //
  //        when(userService.updateUser(eq(userId), any(UserDTO.class))).thenReturn(false);
  //
  //        mockMvc.perform(patch("/api/users/{userId}", userId)
  //                        .contentType(MediaType.APPLICATION_JSON)
  //                        .content(updatedUserJson))
  //                .andExpect(status().isNotFound())
  //                .andExpect(status().reason("User not found"));
  //
  //        verify(userService, times(1)).updateUser(eq(userId), any(UserDTO.class));
  //    }
  //
  //    @Test
  //    void deleteUser_ShouldDeleteExistingUserSuccessfully() throws Exception {
  //        Long userId = 1L;
  //        when(userService.deleteUser(userId)).thenReturn(true);
  //
  //        mockMvc.perform(delete("/api/users/{userId}", userId))
  //                .andExpect(status().isNoContent());
  //
  //        verify(userService, times(1)).deleteUser(userId);
  //    }
  //
  //    @Test
  //    void deleteUser_ShouldReturnNotFound_WhenUserDoesNotExist() throws Exception {
  //        Long userId = 99L;
  //        when(userService.deleteUser(userId)).thenReturn(false);
  //
  //        mockMvc.perform(delete("/api/users/{userId}", userId))
  //                .andExpect(status().isNotFound())
  //                .andExpect(status().reason("User not found"));
  //
  //        verify(userService, times(1)).deleteUser(userId);
  //    }
}
