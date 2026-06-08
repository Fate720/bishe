package com.library.controller;

import com.library.common.Result;
import com.library.entity.Role;
import com.library.service.RoleService;
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
@RequestMapping("/api/roles")
@RequiredArgsConstructor
@Tag(name = "角色管理", description = "角色增删改查接口")
public class RoleController {
    
    private final RoleService roleService;
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "分页查询角色")
    public Result<Page<Role>> list(@PageableDefault(size = 10) Pageable pageable) {
        return Result.success(roleService.listRoles(pageable));
    }
    
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "查询所有角色")
    public Result<List<Role>> listAll() {
        return Result.success(roleService.listAllRoles());
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "获取角色详情")
    public Result<Role> get(@PathVariable Long id) {
        return Result.success(roleService.getById(id));
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "创建角色")
    public Result<Role> create(@RequestBody Role role) {
        return Result.success(roleService.create(role));
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "更新角色")
    public Result<Role> update(@PathVariable Long id, @RequestBody Role role) {
        return Result.success(roleService.update(id, role));
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "删除角色")
    public Result<Void> delete(@PathVariable Long id) {
        roleService.delete(id);
        return Result.success();
    }
}