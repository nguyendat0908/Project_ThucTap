package com.example.Project_Jobhunter.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.Project_Jobhunter.domain.Permission;
import com.example.Project_Jobhunter.dto.response.ResPaginationDTO;
import com.example.Project_Jobhunter.service.PermissionService;
import com.example.Project_Jobhunter.util.annotation.ApiMessage;
import com.example.Project_Jobhunter.util.exception.IdInvalidException;
import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/api/v1")
public class PermissionController {

    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    // Create a new permission
    @PostMapping("/permissions")
    @ApiMessage("Tạo một quyền hạn mới thành công!")
    public ResponseEntity<Permission> createPermission(@RequestBody @Valid Permission permission)
            throws IdInvalidException {
        boolean isCheckExistPermissionName = this.permissionService.checkExistPermissionName(permission.getName());
        if (isCheckExistPermissionName) {
            throw new IdInvalidException("Tên quyền hạn đã tồn tại! Vui lòng chọn tên khác.");
        }
        return ResponseEntity.ok(this.permissionService.handleCreatePermission(permission));
    }

    // Get permission by ID
    @GetMapping("/permissions/{id}")
    @ApiMessage("Hiển thi thị thông tin chi tiết một quyền hạn thành công!")
    public ResponseEntity<Permission> getPermissionById(@PathVariable("id") UUID id) throws IdInvalidException {
        Permission permission = this.permissionService.handleGetPermissionById(id);
        if (permission == null) {
            throw new IdInvalidException("Quyền hạn không tồn tại! Vui lòng kiểm tra lại ID.");
        }
        return ResponseEntity.ok(permission);

    }

    // Get all permissions
    @GetMapping("/permissions")
    @ApiMessage("Hiển thị danh sách quyền hạn thành công!")
    public ResponseEntity<ResPaginationDTO> getListPermissions(@Filter Specification<Permission> spec,
            Pageable pageable) {
        return ResponseEntity.ok(this.permissionService.handleGetAllPermission(spec, pageable));
    }

    // Update a permission
    @PutMapping("/permissions")
    @ApiMessage("Cập nhật thông tin quyền hạn thành công!")
    public ResponseEntity<Permission> updatePermission(@RequestBody @Valid Permission permission)
            throws IdInvalidException {

        boolean isCheckExistPermissionName = this.permissionService.checkExistPermissionName(permission.getName());
        if (isCheckExistPermissionName) {
            throw new IdInvalidException("Tên quyền hạn đã tồn tại! Vui lòng chọn tên khác.");
        }
        Permission newPermission = this.permissionService.handleUpdatePermission(permission);
        if (newPermission == null) {
            throw new IdInvalidException("ID không tồn tại! Vui lòng kiểm tra ID của bạn.");
        }

        return ResponseEntity.ok(newPermission);
    }

    // Delete a permission
    @DeleteMapping("/permissions/{id}")
    @ApiMessage("Xoá quyền hạn thành công!")
    public ResponseEntity<Void> deletePermission(@PathVariable("id") UUID id) throws IdInvalidException {
        Permission permission = this.permissionService.handleGetPermissionById(id);
        if (permission == null) {
            throw new IdInvalidException("ID không tồn tại! Vui lòng kiểm tra ID của bạn.");
        }
        this.permissionService.handleDeletePermission(id);
        return ResponseEntity.ok().build();
    }

}
