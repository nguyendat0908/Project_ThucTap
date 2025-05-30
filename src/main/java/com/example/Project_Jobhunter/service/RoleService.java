package com.example.Project_Jobhunter.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.example.Project_Jobhunter.domain.Permission;
import com.example.Project_Jobhunter.domain.Role;
import com.example.Project_Jobhunter.dto.response.ResPaginationDTO;
import com.example.Project_Jobhunter.dto.response.ResRoleDTO;
import com.example.Project_Jobhunter.repository.PermissionRepository;
import com.example.Project_Jobhunter.repository.RoleRepository;

@Service
public class RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public RoleService(RoleRepository roleRepository, PermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    // Create a new role
    public Role handleCreateRole(Role role) {

        if (role.getPermissions() != null) {
            List<Integer> permissionIds = role.getPermissions().stream()
                    .map(Permission::getId)
                    .collect(Collectors.toList());
            List<Permission> permissions = this.permissionRepository.findByIdIn(permissionIds);
            role.setPermissions(permissions);
        }

        return this.roleRepository.save(role);
    }

    // Get a role by ID
    public Role handleGetRoleById(int id) {
        Optional<Role> roleOptional = this.roleRepository.findById(id);
        if (roleOptional.isPresent()) {
            return roleOptional.get();
        }
        return null;
    }

    // Get all roles
    public ResPaginationDTO handleGetAllRoles(Specification<Role> spec, Pageable pageable) {
        Page<Role> pageRoles = this.roleRepository.findAll(spec, pageable);

        ResPaginationDTO resPaginationDTO = new ResPaginationDTO();
        ResPaginationDTO.Meta meta = new ResPaginationDTO.Meta();

        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(pageRoles.getTotalPages());
        meta.setTotal(pageRoles.getTotalElements());

        resPaginationDTO.setMeta(meta);

        List<ResRoleDTO> resRoleDTOs = pageRoles.getContent().stream()
                .map(this::convertToResRoleDTO)
                .collect(Collectors.toList());
        resPaginationDTO.setResult(resRoleDTOs);

        return resPaginationDTO;
    }

    // Update a role
    public Role handleUpdateRole(Role role) {

        // Check permission
        if (role.getPermissions() != null) {
            List<Integer> permissionIds = role.getPermissions().stream()
                    .map(Permission::getId)
                    .collect(Collectors.toList());
            List<Permission> permissions = this.permissionRepository.findByIdIn(permissionIds);
            role.setPermissions(permissions);
        }

        Role currentRole = this.handleGetRoleById(role.getId());
        if (currentRole != null) {
            currentRole.setName(role.getName() != null ? role.getName() : currentRole.getName());
            currentRole.setActive(role.isActive() != false ? role.isActive() : currentRole.isActive());
            currentRole.setDescription(
                    role.getDescription() != null ? role.getDescription() : currentRole.getDescription());
            currentRole.setPermissions(role.getPermissions());

            this.roleRepository.save(currentRole);
        }

        return currentRole;
    }

    // Delete a role
    public void handleDeleteRole(int id) {
        this.roleRepository.deleteById(id);
    }

    // Check if a role exists by name
    public boolean handleRoleExistsByName(String name) {
        return this.roleRepository.existsByName(name);
    }

    // Convert role to ResRoleDTO
    public ResRoleDTO convertToResRoleDTO(Role role) {
        ResRoleDTO resRoleDTO = new ResRoleDTO();
        resRoleDTO.setId(role.getId());
        resRoleDTO.setName(role.getName());
        resRoleDTO.setActive(role.isActive());
        resRoleDTO.setDescription(role.getDescription());
        resRoleDTO.setCreatedAt(role.getCreatedAt());
        resRoleDTO.setUpdatedAt(role.getUpdatedAt());

        if (role.getPermissions() != null) {
            List<String> permissions = role.getPermissions().stream()
                    .map(Permission::getName)
                    .collect(Collectors.toList());
            resRoleDTO.setPermissions(permissions);
        }

        return resRoleDTO;
    }
}
