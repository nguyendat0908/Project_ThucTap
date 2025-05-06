package com.example.Project_Jobhunter.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.Project_Jobhunter.domain.User;
import com.example.Project_Jobhunter.dto.response.ResUserDTO;
import com.example.Project_Jobhunter.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Create a new user
    public User handleCreateUser(User user) {
        return this.userRepository.save(user);
    }

    // Get a user by ID
    public User handleGetUserById(UUID id) {
        Optional<User> user = this.userRepository.findById(id);
        if (user.isPresent()) {
            return user.get();
        }
        return null;
    }

    // Get list users
    public List<User> handleGetUsers() {
        return this.userRepository.findAll();
    }

    // Update a user
    public User handleUpdateUser(User user) {
        User currentUser = this.handleGetUserById(user.getId());
        if (currentUser != null) {
            currentUser.setName(user.getName());
            currentUser.setAddress(user.getAddress());
            currentUser.setAge(user.getAge());
            currentUser.setGender(user.getGender());
            currentUser.setAvatar(user.getAvatar());
            currentUser.setActive(user.isActive());

            this.userRepository.save(currentUser);
        }
        return currentUser;
    }

    // Delete a user
    public void handleDeleteUserById(UUID id) {
        this.userRepository.deleteById(id);
    }

    // Convert User to ResUserDTO
    public ResUserDTO convertToResUserDTO(User user) {
        ResUserDTO resUserDTO = new ResUserDTO();

        resUserDTO.setId(user.getId());
        resUserDTO.setName(user.getName());
        resUserDTO.setEmail(user.getEmail());
        resUserDTO.setAddress(user.getAddress());
        resUserDTO.setAge(user.getAge());
        resUserDTO.setRefreshToken(user.getRefreshToken());
        resUserDTO.setActive(user.isActive());
        resUserDTO.setAvatar(user.getAvatar());
        resUserDTO.setGender(user.getGender());
        resUserDTO.setCreatedAt(user.getCreatedAt());
        resUserDTO.setUpdatedAt(user.getUpdatedAt());

        return resUserDTO;
    }
}
