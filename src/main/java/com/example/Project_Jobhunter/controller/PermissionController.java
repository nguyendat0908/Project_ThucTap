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
    @ApiMessage("Create a new permission")
    public ResponseEntity<Permission> createPermission(@RequestBody @Valid Permission permission)
            throws IdInvalidException {
        boolean isCheckExistPermissionName = this.permissionService.checkExistPermissionName(permission.getName());
        if (isCheckExistPermissionName) {
            throw new IdInvalidException("Permission name already exists! Please choose another name.");
        }
        return ResponseEntity.ok(this.permissionService.handleCreatePermission(permission));
    }

    // Get permission by ID
    @GetMapping("/permissions/{id}")
    @ApiMessage("Get permission by ID")
    public ResponseEntity<Permission> getPermissionById(@PathVariable("id") UUID id) throws IdInvalidException {
        Permission permission = this.permissionService.handleGetPermissionById(id);
        if (permission == null) {
            throw new IdInvalidException("Permission not found! Please check the ID again.");
        }
        return ResponseEntity.ok(permission);

    }

    // Get all permissions
    @GetMapping("/permissions")
    @ApiMessage("Get list permissions")
    public ResponseEntity<ResPaginationDTO> getListPermissions(@Filter Specification<Permission> spec,
            Pageable pageable) {
        return ResponseEntity.ok(this.permissionService.handleGetAllPermission(spec, pageable));
    }

    // Update a permission
    @PutMapping("/permissions")
    @ApiMessage("Update permission")
    public ResponseEntity<Permission> updatePermission(@RequestBody @Valid Permission permission)
            throws IdInvalidException {

        boolean isCheckExistPermissionName = this.permissionService.checkExistPermissionName(permission.getName());
        if (isCheckExistPermissionName) {
            throw new IdInvalidException("Permission name already exists! Please choose another name.");
        }
        Permission newPermission = this.permissionService.handleUpdatePermission(permission);
        if (newPermission == null) {
            throw new IdInvalidException("Permission not found! Please check the ID again.");
        }

        return ResponseEntity.ok(newPermission);
    }

    // Delete a permission
    @DeleteMapping("/permissions/{id}")
    @ApiMessage("Delete permission")
    public ResponseEntity<Void> deletePermission(@PathVariable("id") UUID id) throws IdInvalidException {
        Permission permission = this.permissionService.handleGetPermissionById(id);
        if (permission == null) {
            throw new IdInvalidException("Permission not found! Please check the ID again.");
        }
        this.permissionService.handleDeletePermission(id);
        return ResponseEntity.ok().build();
    }

}
