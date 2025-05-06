package com.example.Project_Jobhunter.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.Project_Jobhunter.domain.User;
import com.example.Project_Jobhunter.dto.response.ResUserDTO;
import com.example.Project_Jobhunter.service.UserService;
import com.example.Project_Jobhunter.util.annotation.ApiMessage;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/api/v1")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Create a new user
    @PostMapping("/users")
    @ApiMessage("Create a new user")
    public ResponseEntity<ResUserDTO> createUser(@RequestBody User user) {
        User newUser = this.userService.handleCreateUser(user);
        return ResponseEntity.ok(this.userService.convertToResUserDTO(newUser));
    }

    // Get a user by ID
    @GetMapping("/users/{id}")
    @ApiMessage("Get user by ID")
    public ResponseEntity<ResUserDTO> getUserById(@PathVariable("id") UUID id) {
        User user = this.userService.handleGetUserById(id);
        return ResponseEntity.ok(this.userService.convertToResUserDTO(user));
    }

    // Get list users
    @GetMapping("/users")
    @ApiMessage("Get list users")
    public ResponseEntity<List<ResUserDTO>> getListUsers() {
        List<User> users = this.userService.handleGetUsers();
        List<ResUserDTO> resUserDTOs = users.stream().map(user -> this.userService.convertToResUserDTO(user)).toList();
        return ResponseEntity.ok(resUserDTOs);
    }

    // Update a user
    @PutMapping("/users")
    @ApiMessage("Update a user")
    public ResponseEntity<ResUserDTO> updateUser(@RequestBody User user) {
        User updatedUser = this.userService.handleUpdateUser(user);
        return ResponseEntity.ok(this.userService.convertToResUserDTO(updatedUser));
    }

    // Delete a user
    @DeleteMapping("/users/{id}")
    @ApiMessage("Delete a user")
    public ResponseEntity<Void> deleteUserById(@PathVariable("id") UUID id) {
        this.userService.handleDeleteUserById(id);
        return ResponseEntity.ok(null);
    }

}
