package com.example.Project_Jobhunter.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.example.Project_Jobhunter.domain.Company;
import com.example.Project_Jobhunter.domain.Role;
import com.example.Project_Jobhunter.domain.User;
import com.example.Project_Jobhunter.dto.response.ResPaginationDTO;
import com.example.Project_Jobhunter.dto.response.ResUserDTO;
import com.example.Project_Jobhunter.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final CompanyService companyService;
    private final RoleService roleService;

    public UserService(UserRepository userRepository, CompanyService companyService, RoleService roleService) {
        this.userRepository = userRepository;
        this.companyService = companyService;
        this.roleService = roleService;
    }

    // Create a new user
    public User handleCreateUser(User user) {

        // Check company
        if (user.getCompany() != null) {
            Company company = this.companyService.handleGetCompanyById(user.getCompany().getId());
            if (company != null) {
                user.setCompany(company);
            } else {
                user.setCompany(null);
            }

        }

        // Check role
        if (user.getRole() != null) {
            Role role = this.roleService.handleGetRoleById(user.getRole().getId());
            if (role != null) {
                user.setRole(role);
            } else {
                user.setRole(null);
            }
        }
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
    public ResPaginationDTO handleGetUsers(Specification<User> spec, Pageable pageable) {

        Page<User> pageUser = this.userRepository.findAll(spec, pageable);
        ResPaginationDTO resPaginationDTO = new ResPaginationDTO();
        ResPaginationDTO.Meta meta = new ResPaginationDTO.Meta();

        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(pageUser.getTotalPages());
        meta.setTotal(pageUser.getTotalElements());

        resPaginationDTO.setMeta(meta);

        List<ResUserDTO> listUserDTOs = pageUser.getContent().stream().map(item -> this.convertToResUserDTO(item))
                .collect(Collectors.toList());

        resPaginationDTO.setResult(listUserDTOs);

        return resPaginationDTO;
    }

    // Update a user
    public User handleUpdateUser(User user) {
        User currentUser = this.handleGetUserById(user.getId());
        if (currentUser != null) {
            currentUser.setName(user.getName() != null ? user.getName() : currentUser.getName());
            currentUser.setAddress(user.getAddress() != null ? user.getAddress() : currentUser.getAddress());
            currentUser.setAge(user.getAge() != 0 ? user.getAge() : currentUser.getAge());
            currentUser.setGender(user.getGender() != null ? user.getGender() : currentUser.getGender());
            currentUser.setAvatar(user.getAvatar() != null ? user.getAvatar() : currentUser.getAvatar());
            currentUser.setActive(user.isActive() != false ? user.isActive() : currentUser.isActive());

            // Check company
            if (user.getCompany() != null) {
                Company company = this.companyService.handleGetCompanyById(user.getCompany().getId());
                user.setCompany(company);
                currentUser.setCompany(user.getCompany());
            }
            // Check role
            if (user.getRole() != null) {
                Role role = this.roleService.handleGetRoleById(user.getRole().getId());
                user.setRole(role);
                currentUser.setRole(user.getRole());
            }

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
        ResUserDTO.CompanyUser companyUser = new ResUserDTO.CompanyUser();
        ResUserDTO.RoleUser roleUser = new ResUserDTO.RoleUser();
        if (user.getCompany() != null) {
            companyUser.setId(user.getCompany().getId());
            companyUser.setName(user.getCompany().getName());
            resUserDTO.setCompanyUser(companyUser);
        }
        if (user.getRole() != null) {
            roleUser.setId(user.getRole().getId());
            roleUser.setName(user.getRole().getName());
            resUserDTO.setRoleUser(roleUser);
        }

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

    // Check if email exists
    public boolean handleCheckExistByEmail(String email) {
        return this.userRepository.existsByEmail(email);
    }

    // Get user by username (email)
    public User handleGetUserByUsername(String email) {
        return this.userRepository.findByEmail(email);
    }
}
