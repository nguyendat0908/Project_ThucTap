package com.example.Project_Jobhunter.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.Project_Jobhunter.domain.Company;
import com.example.Project_Jobhunter.domain.Role;
import com.example.Project_Jobhunter.domain.User;
import com.example.Project_Jobhunter.dto.response.ResPaginationDTO;
import com.example.Project_Jobhunter.dto.response.ResUserDTO;
import com.example.Project_Jobhunter.service.CompanyService;
import com.example.Project_Jobhunter.service.RoleService;
import com.example.Project_Jobhunter.service.UserService;
import com.example.Project_Jobhunter.util.annotation.ApiMessage;
import com.example.Project_Jobhunter.util.exception.IdInvalidException;
import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final PasswordEncoder passwordEncoder;
    private final CompanyService companyService;
    private final RoleService roleService;

    public UserController(UserService userService, PasswordEncoder passwordEncoder, CompanyService companyService,
            RoleService roleService) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.companyService = companyService;
        this.roleService = roleService;
    }

    // Create a new user
    @PostMapping("/users")
    @ApiMessage("Tao một người dùng mới thành công!")
    public ResponseEntity<ResUserDTO> createUser(@RequestBody @Valid User user) throws IdInvalidException {

        boolean isEmailExists = this.userService.handleCheckExistByEmail(user.getEmail());
        if (isEmailExists) {
            throw new IdInvalidException("Email đã tồn tại! Vui lòng chọn email khác.");
        }

        this.checkCompanyAndRole(user);

        // Hash password before saving to database
        String hashedPassword = this.passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);

        User newUser = this.userService.handleCreateUser(user);
        return ResponseEntity.ok(this.userService.convertToResUserDTO(newUser));
    }

    // Get a user by ID
    @GetMapping("/users/{id}")
    @ApiMessage("Hiển thị thông tin chi tiết một người dùng thành công!")
    public ResponseEntity<ResUserDTO> getUserById(@PathVariable("id") UUID id) throws IdInvalidException {
        User user = this.userService.handleGetUserById(id);
        if (user == null) {
            throw new IdInvalidException("ID không tồn tại! Vui lòng kiểm tra lại ID.");
        }
        return ResponseEntity.ok(this.userService.convertToResUserDTO(user));
    }

    // Get list users
    @GetMapping("/users")
    @ApiMessage("Hiển thị danh sách người dùng thành công!")
    public ResponseEntity<ResPaginationDTO> getListUsers(@Filter Specification<User> spec, Pageable pageable) {
        return ResponseEntity.ok(this.userService.handleGetUsers(spec, pageable));
    }

    // Update a user
    @PutMapping("/users")
    @ApiMessage("Câp nhật thông tin người dùng thành công!")
    public ResponseEntity<ResUserDTO> updateUser(@RequestBody User user) throws IdInvalidException {
        User newUser = this.userService.handleUpdateUser(user);
        if (newUser == null) {
            throw new IdInvalidException("ID không tồn tại! Vui lòng kiểm tra lại ID.");
        }
        this.checkCompanyAndRole(user);
        return ResponseEntity.ok(this.userService.convertToResUserDTO(newUser));
    }

    // Delete a user
    @DeleteMapping("/users/{id}")
    @ApiMessage("Xóa người dùng thành công!")
    public ResponseEntity<Void> deleteUserById(@PathVariable("id") UUID id) throws IdInvalidException {
        User user = this.userService.handleGetUserById(id);
        if (user == null) {
            throw new IdInvalidException("ID không tồn tại! Vui lòng kiểm tra lại ID.");
        }
        this.userService.handleDeleteUserById(id);
        return ResponseEntity.ok(null);
    }

    // Check company and role
    private void checkCompanyAndRole(User user) throws IdInvalidException {

        Company company = this.companyService.handleGetCompanyById(user.getCompany().getId());
        if (company == null) {
            throw new IdInvalidException("Công ty không tồn tại! Vui lòng kiểm tra lại ID.");
        }

        Role role = this.roleService.handleGetRoleById(user.getRole().getId());
        if (role == null) {
            throw new IdInvalidException("Vai trò không tồn tại! Vui lòng kiểm tra lại ID.");
        }

    }

}
