package com.example.Project_Jobhunter.controller;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.Project_Jobhunter.domain.Permission;
import com.example.Project_Jobhunter.domain.User;
import com.example.Project_Jobhunter.dto.LoginDTO;
import com.example.Project_Jobhunter.dto.response.ResLoginDTO;
import com.example.Project_Jobhunter.dto.response.ResLoginDTO.RoleUserLogin;
import com.example.Project_Jobhunter.repository.PermissionRepository;
import com.example.Project_Jobhunter.service.UserService;
import com.example.Project_Jobhunter.util.SecurityUtil;
import com.example.Project_Jobhunter.util.annotation.ApiMessage;
import com.example.Project_Jobhunter.util.exception.IdInvalidException;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/v1")
public class AuthController {

    // Cấu hình cách thức xác thực (authentication) người dùng trong hệ thống
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final SecurityUtil securityUtil;
    private final PermissionRepository permissionRepository;
    private final UserService userService;

    public AuthController(AuthenticationManagerBuilder authenticationManagerBuilder, SecurityUtil securityUtil,
            UserService userService, PermissionRepository permissionRepository) {
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.userService = userService;
        this.securityUtil = securityUtil;
        this.permissionRepository = permissionRepository;
    }

    @PostMapping("/auth/login")
    @ApiMessage("Login success!")
    public ResponseEntity<ResLoginDTO> login(@RequestBody @Valid LoginDTO loginDTO) {

        // Chứa thông tin người dùng chưa xác thực
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                loginDTO.getUsername(), loginDTO.getPassword());

        // Xác thực người dùng bằng cấu hình lại method loadUserByUsername
        Authentication authentication = authenticationManagerBuilder.getObject()
                .authenticate(usernamePasswordAuthenticationToken);

        // Set thông tin người dùng vào context của Security
        SecurityContextHolder.getContext().setAuthentication(authentication);

        ResLoginDTO resLoginDTO = new ResLoginDTO();
        ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin();

        // Lấy thông tin người dùng đăng nhập
        User currentUserDB = this.userService.handleGetUserByUsername(loginDTO.getUsername());
        if (currentUserDB != null) {
            userLogin.setId(currentUserDB.getId());
            userLogin.setEmail(currentUserDB.getEmail());
            userLogin.setName(currentUserDB.getName());

            if (currentUserDB.getRole() != null) {
                RoleUserLogin roleUserLogin = new RoleUserLogin();
                roleUserLogin.setName(currentUserDB.getRole().getName());
                if (currentUserDB.getRole().getPermissions() != null) {
                    List<UUID> listIdsPermission = currentUserDB.getRole().getPermissions().stream()
                            .map(item -> item.getId()).collect(Collectors.toList());
                    List<Permission> permissions = this.permissionRepository.findByIdIn(listIdsPermission);
                    List<String> namePermissions = permissions.stream().map(item -> item.getName())
                            .collect(Collectors.toList());
                    roleUserLogin.setPermissions(namePermissions);
                }
                userLogin.setRoleUserLogin(roleUserLogin);
            }
            resLoginDTO.setUserLogin(userLogin);
        }

        // Tạo token
        String accessToken = this.securityUtil.createAccessToken(authentication.getName(), resLoginDTO);
        resLoginDTO.setAccessToken(accessToken);

        // Create refresh token
        String refreshToken = this.securityUtil.createRefreshToken(loginDTO.getUsername(), resLoginDTO);

        // Update refresh token vào DB
        this.userService.handleUpdateUserAddRefreshToken(loginDTO.getUsername(), refreshToken);

        // Set cookie
        ResponseCookie cookie = ResponseCookie
                .from("refresh_token", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(86400)
                .build();

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body(resLoginDTO);
    }

    // Get account when f5 website
    @GetMapping("/auth/account")
    @ApiMessage("Get account when f5 website")
    public ResponseEntity<ResLoginDTO.UserLogin> getAccount() {
        String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : "";

        ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin();
        User currentUserDB = this.userService.handleGetUserByUsername(email);
        if (currentUserDB != null) {
            userLogin.setId(currentUserDB.getId());
            userLogin.setEmail(currentUserDB.getEmail());
            userLogin.setName(currentUserDB.getName());

            if (currentUserDB.getRole() != null) {
                RoleUserLogin roleUserLogin = new RoleUserLogin();
                roleUserLogin.setName(currentUserDB.getRole().getName());
                if (currentUserDB.getRole().getPermissions() != null) {
                    List<UUID> listIdsPermission = currentUserDB.getRole().getPermissions().stream()
                            .map(item -> item.getId()).collect(Collectors.toList());
                    List<Permission> permissions = this.permissionRepository.findByIdIn(listIdsPermission);
                    List<String> namePermissions = permissions.stream().map(item -> item.getName())
                            .collect(Collectors.toList());
                    roleUserLogin.setPermissions(namePermissions);
                }
                userLogin.setRoleUserLogin(roleUserLogin);
            }
        }

        return ResponseEntity.ok(userLogin);
    }

    @GetMapping("/auth/refresh")
    @ApiMessage("Get user by refresh token")
    public ResponseEntity<ResLoginDTO> getRefreshToken(
            @CookieValue(name = "refresh_token", defaultValue = "No refresh_token") String refreshToken)
            throws IdInvalidException {

        if (refreshToken.equals("No refresh_token")) {
            throw new IdInvalidException("Không có refresh_token ở cookie.");
        }

        // Kiểm tra refresh token
        Jwt decodedToken = this.securityUtil.checkValidRefreshToken(refreshToken);
        String email = decodedToken.getSubject();

        // Kiểm tra người dùng với email và token
        User currentUser = this.userService.handleGetUserByRefreshTokenAndEmail(refreshToken, email);
        if (currentUser == null) {
            throw new IdInvalidException("Refresh token không tồn tại hoặc không hợp lệ!");
        }

        ResLoginDTO resLoginDTO = new ResLoginDTO();
        ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin();

        User currentUserDB = this.userService.handleGetUserByUsername(email);
        if (currentUserDB != null) {
            userLogin.setId(currentUserDB.getId());
            userLogin.setEmail(currentUserDB.getEmail());
            userLogin.setName(currentUserDB.getName());

            if (currentUserDB.getRole() != null) {
                RoleUserLogin roleUserLogin = new RoleUserLogin();
                roleUserLogin.setName(currentUserDB.getRole().getName());
                if (currentUserDB.getRole().getPermissions() != null) {
                    List<UUID> listIdsPermission = currentUserDB.getRole().getPermissions().stream()
                            .map(item -> item.getId()).collect(Collectors.toList());
                    List<Permission> permissions = this.permissionRepository.findByIdIn(listIdsPermission);
                    List<String> namePermissions = permissions.stream().map(item -> item.getName())
                            .collect(Collectors.toList());
                    roleUserLogin.setPermissions(namePermissions);
                }
                userLogin.setRoleUserLogin(roleUserLogin);
            }
            resLoginDTO.setUserLogin(userLogin);
        }

        String newAccessToken = this.securityUtil.createAccessToken(email, resLoginDTO);
        resLoginDTO.setAccessToken(newAccessToken);

        String newRefreshToken = this.securityUtil.createRefreshToken(email, resLoginDTO);
        this.userService.handleUpdateUserAddRefreshToken(email, newRefreshToken);

        // Create cookie
        ResponseCookie responseCookie = ResponseCookie.from("refresh_token", newRefreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(86400)
                .build();

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                .body(resLoginDTO);
    }

    @PostMapping("/auth/logout")
    @ApiMessage("Logout success!")
    public ResponseEntity<ResLoginDTO> logout() throws IdInvalidException {

        String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : "";
        this.userService.handleUpdateUserAddRefreshToken(email, null);

        if (email.equals("")) {
            throw new IdInvalidException("Access Token không hợp lệ");
        }

        ResponseCookie deleteCookie = ResponseCookie.from("refresh_token", null)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(86400)
                .build();

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
                .body(null);
    }

}
