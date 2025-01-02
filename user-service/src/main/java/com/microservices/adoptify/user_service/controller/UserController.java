package com.microservices.adoptify.user_service.controller;

import com.microservices.adoptify.user_service.dto.UserAndJwtDTO;
import com.microservices.adoptify.user_service.dto.UserDTO;
import com.microservices.adoptify.user_service.model.User;
import com.microservices.adoptify.user_service.service.UserService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/users")
public class UserController {

  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping
  public ResponseEntity<List<UserDTO>> getAllUsers() {
    List<UserDTO> users = userService.getAllUsers();
    return new ResponseEntity<>(users, HttpStatus.OK);
  }

  @GetMapping("/{userId}")
  public ResponseEntity<UserDTO> getUserById(@PathVariable Long userId) {
    UserDTO userDTO = userService.getUser(userId);
    if (userDTO == null) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    return new ResponseEntity<>(userDTO, HttpStatus.OK);
  }

  @PostMapping("/register")
  public ResponseEntity<UserAndJwtDTO> registerUser(@RequestBody User user) {
    UserAndJwtDTO userAndJwtDTO = userService.registerUser(user);
    return new ResponseEntity<>(userAndJwtDTO, HttpStatus.CREATED);
  }

  @PostMapping("/login")
  public ResponseEntity<UserAndJwtDTO> loginUser(@RequestBody User user) {
    return new ResponseEntity<>(userService.verify(user), HttpStatus.OK);
  }

  //    @PostMapping
  //    public ResponseEntity<String> addUser(@RequestBody User user) {
  //        String response = userService.addUser(user);
  //        return new ResponseEntity<>(response, HttpStatus.CREATED);
  //    }

  @PatchMapping("/{userId}")
  public ResponseEntity<String> updateUser(
      @PathVariable Long userId, @RequestBody UserDTO userDTO) {
    boolean userPresent = userService.updateUser(userId, userDTO);
    if (userPresent) {
      return new ResponseEntity<>("User updated successfully", HttpStatus.OK);
    } else {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
    }
  }

  @DeleteMapping("/{userId}")
  public ResponseEntity<String> deleteUser(@PathVariable Long userId) {
    boolean userPresent = userService.deleteUser(userId);
    if (userPresent) {
      return new ResponseEntity<>("User deleted successfully", HttpStatus.NO_CONTENT);
    } else {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
    }
  }
}
