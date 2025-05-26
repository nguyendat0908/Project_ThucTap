package com.example.Project_Jobhunter.service;

import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.Project_Jobhunter.domain.User;
import com.example.Project_Jobhunter.dto.OTPData;

@Service
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final ConcurrentHashMap<String, OTPData> otpStorage = new ConcurrentHashMap<String, OTPData>();
    private final EmailService emailService;

    Map<String, User> tempNewUser = new ConcurrentHashMap<>();

    public AuthService(PasswordEncoder passwordEncoder, UserService userService, EmailService emailService) {
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
        this.emailService = emailService;
    }

    // Đăng ký người dùng
    public void handleRegisterUser(User user) {
        this.handleSaveTempUser(user);
        String otp = this.handleGenerateOTP(user.getEmail());
        this.emailService.sendEmailActiveAccount(user.getEmail(), "Kích hoạt tài khoản.", "email", otp);
    }

    // Tạo mã OTP
    public String handleGenerateOTP(String email) {
        String otp = String.format("%06d", new Random().nextInt(1000000));
        otpStorage.put(email, new OTPData(otp, 30));
        return otp;
    }

    // Tạo mật khẩu
    public String handleGeneratePassword() {
        String newPassword = UUID.randomUUID().toString().substring(0, 6);
        return newPassword;
    }

    // Xác thực OTP
    public boolean handleVerifyOTP(String email, String inputOtp) {
        OTPData otpData = otpStorage.get(email);
        if (otpData == null)
            return false;
        if (otpData.isExpired()) {
            otpStorage.remove(email);
            return false;
        }
        boolean valid = otpData.getCode().equals(inputOtp);
        if (valid)
            otpStorage.remove(email);
        return valid;
    }

    // Lưu thông tin người dùng tạm thời
    public void handleSaveTempUser(User user) {
        tempNewUser.put(user.getEmail(), user);
    }

    // Lấy thông tin người dùng tạm thời
    public User handleGetTempUser(String email) {
        return tempNewUser.get(email);
    }

    // Kích hoạt tài khoản
    public User handleActiveAccount(String email) {
        User user = this.handleGetTempUser(email);
        String hashPassword = this.passwordEncoder.encode(user.getPassword());
        user.setPassword(hashPassword);
        user.setActive(true);
        User newUser = this.userService.handleCreateUser(user);

        return newUser;
    }

    // Quên mật khẩu
    public void handleForgotPassword(String email) {
        User user = this.userService.handleGetUserByUsername(email);

        // Logic gửi mật khẩu mới tới email
        String newPassword = this.handleGeneratePassword();
        System.out.println(">>>>>>>>>>>> CHECK: " + newPassword);
        this.emailService.sendEmailNewPassword(email, "Mật khẩu mới của bạn.", "password", newPassword);
        String hashPassword = this.passwordEncoder.encode(newPassword);
        user.setPassword(hashPassword);
        this.userService.handleCreateUser(user);
    }

    // Cập nhật thông tin
    public User handleUpdateInfoUser(User user) {

        User currentUser = this.userService.handleGetUserById(user.getId());
        if (currentUser != null) {
            currentUser.setName(user.getName() != null ? user.getName() : currentUser.getName());
            currentUser.setPassword(user.getPassword() != null ? user.getPassword() : currentUser.getPassword());
            currentUser.setAddress(user.getAddress() != null ? user.getAddress() : currentUser.getAddress());
            currentUser.setAge(user.getAge() != 0 ? user.getAge() : currentUser.getAge());
            currentUser.setGender(user.getGender() != null ? user.getGender() : currentUser.getGender());
            currentUser.setAvatar(user.getAvatar() != null ? user.getAvatar() : currentUser.getAvatar());

            String hashPassword = this.passwordEncoder.encode(currentUser.getPassword());
            currentUser.setPassword(hashPassword);
            this.userService.handleCreateUser(currentUser);
        }

        return currentUser;
    }
}
