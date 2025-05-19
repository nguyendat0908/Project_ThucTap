package com.example.Project_Jobhunter.service;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.example.Project_Jobhunter.domain.Permission;
import com.example.Project_Jobhunter.dto.response.ResPaginationDTO;
import com.example.Project_Jobhunter.repository.PermissionRepository;

@Service
public class PermissionService {

    private final PermissionRepository permissionRepository;

    public PermissionService(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    // Create a new permission
    public Permission handleCreatePermission(Permission permission) {
        return permissionRepository.save(permission);
    }

    // Get permission by ID
    public Permission handleGetPermissionById(UUID id) {
        Optional<Permission> permissionOptional = this.permissionRepository.findById(id);
        if (permissionOptional.isPresent()) {
            return permissionOptional.get();
        }
        return null;
    }

    // Get all permissions
    public ResPaginationDTO handleGetAllPermission(Specification<Permission> spec, Pageable pageable) {
        Page<Permission> pagePermission = this.permissionRepository.findAll(spec, pageable);

        ResPaginationDTO resPaginationDTO = new ResPaginationDTO();
        ResPaginationDTO.Meta meta = new ResPaginationDTO.Meta();

        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(pagePermission.getTotalPages());
        meta.setTotal(pagePermission.getTotalElements());

        resPaginationDTO.setMeta(meta);
        resPaginationDTO.setResult(pagePermission.getContent());

        return resPaginationDTO;
    }

    // Update a permission
    public Permission handleUpdatePermission(Permission permission) {
        Permission currentPermission = this.handleGetPermissionById(permission.getId());
        if (currentPermission != null) {
            currentPermission
                    .setName(permission.getName() != null ? permission.getName() : currentPermission.getName());
            currentPermission.setApiPath(
                    permission.getApiPath() != null ? permission.getApiPath() : currentPermission.getApiPath());
            currentPermission
                    .setMethod(permission.getMethod() != null ? permission.getMethod() : currentPermission.getMethod());
            currentPermission
                    .setModule(permission.getModule() != null ? permission.getModule() : currentPermission.getModule());
            this.permissionRepository.save(currentPermission);
        }

        return currentPermission;
    }

    // Delete a permission
    public void handleDeletePermission(UUID id) {
        this.permissionRepository.deleteById(id);
    }

    // Check if permission name already exists
    public boolean checkExistPermissionName(String name) {
        return this.permissionRepository.existsByName(name);
    }

    // Check if permission exists by ID
    public boolean checkExistPermissionById(UUID id) {
        return this.permissionRepository.existsById(id);
    }
}
