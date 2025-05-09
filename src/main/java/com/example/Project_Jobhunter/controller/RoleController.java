package com.example.Project_Jobhunter.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.Project_Jobhunter.domain.Permission;
import com.example.Project_Jobhunter.domain.Role;
import com.example.Project_Jobhunter.dto.response.ResPaginationDTO;
import com.example.Project_Jobhunter.dto.response.ResRoleDTO;
import com.example.Project_Jobhunter.service.PermissionService;
import com.example.Project_Jobhunter.service.RoleService;
import com.example.Project_Jobhunter.util.annotation.ApiMessage;
import com.example.Project_Jobhunter.util.exception.IdInvalidException;
import com.turkraft.springfilter.boot.Filter;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
public class RoleController {

    private final RoleService roleService;
    private final PermissionService permissionService;

    public RoleController(RoleService roleService, PermissionService permissionService) {
        this.roleService = roleService;
        this.permissionService = permissionService;
    }

    // Create a new role
    @PostMapping("/roles")
    @ApiMessage("Create a new role")
    public ResponseEntity<ResRoleDTO> createRole(@RequestBody Role role) throws IdInvalidException {

        boolean isCheckExistRoleName = this.roleService.handleRoleExistsByName(role.getName());
        if (isCheckExistRoleName) {
            throw new IdInvalidException("Role name already exists! Please choose another name.");
        }
        if (role.getPermissions() != null) {
            List<UUID> listIdPermissions = role.getPermissions().stream().map(Permission::getId)
                    .collect(Collectors.toList());
            for (UUID id : listIdPermissions) {
                boolean isCheckPermissionExist = this.permissionService.checkExistPermissionById(id);
                if (!isCheckPermissionExist) {
                    throw new IdInvalidException("Permission not exist. Please check your permission!");
                }
            }
        }

        return ResponseEntity.ok(this.roleService.convertToResRoleDTO(this.roleService.handleCreateRole(role)));

    }

    // Get role by ID
    @GetMapping("/roles/{id}")
    @ApiMessage("Get role by ID")
    public ResponseEntity<ResRoleDTO> getRoleById(@PathVariable("id") UUID id) throws IdInvalidException {
        Role role = this.roleService.handleGetRoleById(id);
        if (role == null) {
            throw new IdInvalidException("Role not found! Please check the ID again.");
        }
        return ResponseEntity.ok(this.roleService.convertToResRoleDTO(role));
    }

    // Get all roles
    @GetMapping("/roles")
    @ApiMessage("Get list roles")
    public ResponseEntity<ResPaginationDTO> getListRoles(@Filter Specification<Role> spec, Pageable pageable) {
        return ResponseEntity.ok(this.roleService.handleGetAllRoles(spec, pageable));
    }

    // Update a role
    @PutMapping("/roles")
    @ApiMessage("Update role")
    public ResponseEntity<ResRoleDTO> updateRole(@RequestBody Role role) throws IdInvalidException {

        Role newRole = this.roleService.handleGetRoleById(role.getId());
        if (newRole == null) {
            throw new IdInvalidException("Role no exist! Please check your role ID.");
        }

        boolean isCheckExistRoleName = this.roleService.handleRoleExistsByName(role.getName());
        if (isCheckExistRoleName) {
            throw new IdInvalidException("Role name already exists! Please choose another name.");
        }
        if (role.getPermissions() != null) {
            List<UUID> listIdPermissions = role.getPermissions().stream().map(Permission::getId)
                    .collect(Collectors.toList());
            for (UUID id : listIdPermissions) {
                boolean isCheckPermissionExist = this.permissionService.checkExistPermissionById(id);
                if (!isCheckPermissionExist) {
                    throw new IdInvalidException("Permission not exist. Please check your permission!");
                }
            }
        }

        return ResponseEntity.ok(this.roleService.convertToResRoleDTO(this.roleService.handleUpdateRole(role)));

    }

    // Delete a role
    @DeleteMapping("/roles/{id}")
    @ApiMessage("Delete role")
    public ResponseEntity<Void> deleteRole(@PathVariable("id") UUID id) throws IdInvalidException {
        Role role = this.roleService.handleGetRoleById(id);
        if (role == null) {
            throw new IdInvalidException("Role not found! Please check the ID again.");
        }
        this.roleService.handleDeleteRole(id);
        return ResponseEntity.ok().build();
    }

}
