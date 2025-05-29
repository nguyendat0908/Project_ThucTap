package com.example.Project_Jobhunter.controller;

import java.util.List;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.Project_Jobhunter.domain.Permission;
import com.example.Project_Jobhunter.domain.User;
import com.example.Project_Jobhunter.dto.LoginDTO;
import com.example.Project_Jobhunter.dto.response.ResLoginDTO;
import com.example.Project_Jobhunter.dto.response.ResRegisterDTO;
import com.example.Project_Jobhunter.dto.response.ResUserDTO;
import com.example.Project_Jobhunter.dto.response.ResLoginDTO.RoleUserLogin;
import com.example.Project_Jobhunter.repository.PermissionRepository;
import com.example.Project_Jobhunter.service.AuthService;
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
    private final AuthService authService;

    public AuthController(AuthenticationManagerBuilder authenticationManagerBuilder, SecurityUtil securityUtil,
            UserService userService, PermissionRepository permissionRepository, AuthService authService) {
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.userService = userService;
        this.securityUtil = securityUtil;
        this.permissionRepository = permissionRepository;
        this.authService = authService;
    }

    @PostMapping("/auth/login")
    @ApiMessage("Đăng nhập thành công!")
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
                    List<Integer> listIdsPermission = currentUserDB.getRole().getPermissions().stream()
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
    @ApiMessage("Lấy thông tin tài khoản người dùng hiện tại thành công!")
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
                    List<Integer> listIdsPermission = currentUserDB.getRole().getPermissions().stream()
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
    @ApiMessage("Lấy lại access token thành công!")
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
                    List<Integer> listIdsPermission = currentUserDB.getRole().getPermissions().stream()
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
    @ApiMessage("Đăng xuất thành công!")
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

    @PostMapping("/auth/register")
    @ApiMessage("Mã OTP đã được gửi đến email của bạn thành công!")
    public ResponseEntity<ResRegisterDTO> register(@RequestBody @Valid User user) throws IdInvalidException {
        boolean isCheckEmail = this.userService.handleCheckExistByEmail(user.getEmail());
        if (isCheckEmail) {
            throw new IdInvalidException("Email " + user.getEmail() + " đã tồn tại, vui lòng sử dụng email khác.");
        }

        // Send email
        this.authService.handleRegisterUser(user);

        ResRegisterDTO resRegisterDTO = new ResRegisterDTO();
        resRegisterDTO.setMessage("Làm ơn kiểm tra email của bạn để kích hoạt tài khoản!");

        return ResponseEntity.ok(resRegisterDTO);
    }

    @PostMapping("/auth/active")
    @ApiMessage("Kích hoạt tài khoản thành công!")
    public ResponseEntity<ResUserDTO> activeAccountRegister(@RequestParam String email, @RequestParam String code)
            throws IdInvalidException {
        if (this.authService.handleVerifyOTP(email, code)) {
            User user = this.authService.handleActiveAccount(email);
            return ResponseEntity.ok(this.userService.convertToResUserDTO(user));
        } else {
            throw new IdInvalidException("Mã OTP không hợp lệ hoặc đã hết hạn. Vui lòng kiểm tra lại!");
        }
    }

    @PostMapping("/auth/forgot-password")
    @ApiMessage("Mật khẩu mới đã được gửi đến email của bạn thành công!")
    public ResponseEntity<ResRegisterDTO> forgotPassword(@RequestParam String email) throws IdInvalidException {

        User user = this.userService.handleGetUserByUsername(email);
        if (user == null) {
            throw new IdInvalidException("Email không tồn tại, vui lòng kiểm tra lại email!");
        }

        this.authService.handleForgotPassword(email);

        ResRegisterDTO resRegisterDTO = new ResRegisterDTO();
        resRegisterDTO.setMessage("Vui lòng kiểm tra email của bạn để nhận mật khẩu mới!");

        return ResponseEntity.ok(resRegisterDTO);
    }

    @PostMapping("/auth/update-info")
    @ApiMessage("Cập nhật thông tin tài khoản thành công!")
    public ResponseEntity<ResUserDTO> updateInfoUser(@RequestBody User user) throws IdInvalidException {
        User newUser = this.authService.handleUpdateInfoUser(user);
        if (newUser == null) {
            throw new IdInvalidException("ID người dùng không hợp lệ hoặc không tồn tại!");
        }
        return ResponseEntity.ok(this.userService.convertToResUserDTO(newUser));
    }

}
