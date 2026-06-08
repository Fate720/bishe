package com.library.controller;

import com.library.common.Result;
import com.library.dto.UserRequest;
import com.library.entity.User;
import com.library.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "用户管理", description = "用户增删改查接口")
public class UserController {
    
    private final UserService userService;
    
    @GetMapping
    @Operation(summary = "分页查询用户")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Page<User>> list(@PageableDefault(size = 10) Pageable pageable) {
        return Result.success(userService.listUsers(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取用户详情")
    @PreAuthorize("isAuthenticated()")
    public Result<User> get(@PathVariable Long id) {
        return Result.success(userService.getUserById(id));
    }

    @PostMapping
    @Operation(summary = "创建用户")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<User> create(@Validated @RequestBody UserRequest request) {
        User user = new User();
        BeanUtils.copyProperties(request, user);
        return Result.success(userService.createUser(user, request.getRoleIds()));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新用户")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<User> update(@PathVariable Long id, @Validated @RequestBody UserRequest request) {
        User user = new User();
        BeanUtils.copyProperties(request, user);
        return Result.success(userService.updateUser(id, user, request.getRoleIds()));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除用户")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> delete(@PathVariable Long id) {
        userService.deleteUser(id);
        return Result.success();
    }
}
