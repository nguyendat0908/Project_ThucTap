package com.example.Project_Jobhunter.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import com.example.Project_Jobhunter.domain.Permission;
import com.example.Project_Jobhunter.domain.Role;
import com.example.Project_Jobhunter.domain.User;
import com.example.Project_Jobhunter.service.UserService;
import com.example.Project_Jobhunter.util.SecurityUtil;
import com.example.Project_Jobhunter.util.exception.PermissionException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class PermissionInterceptor implements HandlerInterceptor {

    @Autowired
    UserService userService;

    @Override
    @Transactional
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws PermissionException {

        String path = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        String requestURI = request.getRequestURI();
        String httpMethod = request.getMethod();
        System.out.println(">>> Run preHandle");
        System.out.println(">>> Path = " + path);
        System.out.println(">>> HttpMethod = " + httpMethod);
        System.out.println(">>> RequestURI = " + requestURI);

        String email = SecurityUtil.getCurrentUserLogin().isPresent() == true ? SecurityUtil.getCurrentUserLogin().get()
                : "";
        if (email != null && !email.isEmpty()) {
            User user = this.userService.handleGetUserByUsername(email);
            if (user != null) {
                Role role = user.getRole();
                if (role != null) {
                    List<Permission> permissions = role.getPermissions();
                    boolean isAllow = permissions.stream()
                            .anyMatch(item -> item.getApiPath().equals(path) && item.getMethod().equals(httpMethod));
                    if (isAllow == false) {
                        throw new PermissionException("Bạn không có quyền hạn để truy cập!");
                    }
                } else {
                    throw new PermissionException("Bạn không có quyền hạn truy cập!");
                }
            }
        }

        return true;
    }
}
