package com.library.controller;

import com.library.common.Result;
import com.library.entity.Permission;
import com.library.service.PermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/permissions")
@RequiredArgsConstructor
@Tag(name = "权限管理", description = "权限增删改查接口")
public class PermissionController {
    
    private final PermissionService permissionService;
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "分页查询权限")
    public Result<Page<Permission>> list(@PageableDefault(size = 10) Pageable pageable) {
        return Result.success(permissionService.listPermissions(pageable));
    }
    
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "查询所有权限")
    public Result<List<Permission>> listAll() {
        return Result.success(permissionService.listAllPermissions());
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "获取权限详情")
    public Result<Permission> get(@PathVariable Long id) {
        return Result.success(permissionService.getById(id));
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "创建权限")
    public Result<Permission> create(@RequestBody Permission permission) {
        return Result.success(permissionService.create(permission));
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "更新权限")
    public Result<Permission> update(@PathVariable Long id, @RequestBody Permission permission) {
        return Result.success(permissionService.update(id, permission));
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "删除权限")
    public Result<Void> delete(@PathVariable Long id) {
        permissionService.delete(id);
        return Result.success();
    }
}
