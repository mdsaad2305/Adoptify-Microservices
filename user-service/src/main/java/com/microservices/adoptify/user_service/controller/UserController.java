package com.microservices.adoptify.user_service.controller;

import com.microservices.adoptify.user_service.model.User;
import com.microservices.adoptify.user_service.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable Long userId) {
        Optional<User> user = userService.getUser(userId);
        return user.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    @PostMapping
    public ResponseEntity<String> addUser(@RequestBody User user) {
        String response = userService.addUser(user);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<String> updateUser(@PathVariable Long userId, @RequestBody User user) {
        boolean userPresent = userService.updateUser(userId, user);
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
