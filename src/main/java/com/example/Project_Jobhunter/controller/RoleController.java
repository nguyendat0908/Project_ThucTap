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

import jakarta.validation.Valid;

import java.util.List;
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
    @ApiMessage("Tạo một vai trò mới thành công!")
    public ResponseEntity<ResRoleDTO> createRole(@RequestBody @Valid Role role) throws IdInvalidException {

        boolean isCheckExistRoleName = this.roleService.handleRoleExistsByName(role.getName());
        if (isCheckExistRoleName) {
            throw new IdInvalidException("Tên vai trò đã tồn tại! Vui lòng chọn tên khác.");
        }
        if (role.getPermissions() != null) {
            List<Integer> listIdPermissions = role.getPermissions().stream().map(Permission::getId)
                    .collect(Collectors.toList());
            for (int id : listIdPermissions) {
                boolean isCheckPermissionExist = this.permissionService.checkExistPermissionById(id);
                if (!isCheckPermissionExist) {
                    throw new IdInvalidException("Quyền hạn không tồn tại. Vui lòng kiểm tra lại quyền hạn của bạn!");
                }
            }
        }

        return ResponseEntity.ok(this.roleService.convertToResRoleDTO(this.roleService.handleCreateRole(role)));

    }

    // Get role by ID
    @GetMapping("/roles/{id}")
    @ApiMessage("Hiển thị thông tin chi tiết một vai trò thành công!")
    public ResponseEntity<ResRoleDTO> getRoleById(@PathVariable("id") int id) throws IdInvalidException {
        Role role = this.roleService.handleGetRoleById(id);
        if (role == null) {
            throw new IdInvalidException("Vai trò không tồn tại! Vui lòng kiểm tra lại ID.");
        }
        return ResponseEntity.ok(this.roleService.convertToResRoleDTO(role));
    }

    // Get all roles
    @GetMapping("/roles")
    @ApiMessage("Hiển thị danh sách các vai trò thành công!")
    public ResponseEntity<ResPaginationDTO> getListRoles(@Filter Specification<Role> spec, Pageable pageable) {
        return ResponseEntity.ok(this.roleService.handleGetAllRoles(spec, pageable));
    }

    // Update a role
    @PutMapping("/roles")
    @ApiMessage("Cập nhật thông tin vai trò thành công!")
    public ResponseEntity<ResRoleDTO> updateRole(@RequestBody @Valid Role role) throws IdInvalidException {

        Role newRole = this.roleService.handleGetRoleById(role.getId());
        if (newRole == null) {
            throw new IdInvalidException("Vai trò không tồn tại! Vui lòng kiểm tra lại ID.");
        }

        boolean isCheckExistRoleName = this.roleService.handleRoleExistsByName(role.getName());
        if (isCheckExistRoleName) {
            throw new IdInvalidException("Tên vai trò đã tồn tại! Vui lòng chọn tên khác.");
        }
        if (role.getPermissions() != null) {
            List<Integer> listIdPermissions = role.getPermissions().stream().map(Permission::getId)
                    .collect(Collectors.toList());
            for (int id : listIdPermissions) {
                boolean isCheckPermissionExist = this.permissionService.checkExistPermissionById(id);
                if (!isCheckPermissionExist) {
                    throw new IdInvalidException("Quyền hạn không tồn tại. Vui lòng kiểm tra lại quyền hạn của bạn!");
                }
            }
        }

        return ResponseEntity.ok(this.roleService.convertToResRoleDTO(this.roleService.handleUpdateRole(role)));

    }

    // Delete a role
    @DeleteMapping("/roles/{id}")
    @ApiMessage("Xóa vai trò thành công!")
    public ResponseEntity<Void> deleteRole(@PathVariable("id") int id) throws IdInvalidException {
        Role role = this.roleService.handleGetRoleById(id);
        if (role == null) {
            throw new IdInvalidException("Vai trò không tồn tại! Vui lòng kiểm tra lại ID.");
        }
        this.roleService.handleDeleteRole(id);
        return ResponseEntity.ok().build();
    }

}
