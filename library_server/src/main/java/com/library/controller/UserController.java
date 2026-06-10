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
@Tag(name = "User Management", description = "User CRUD")
public class UserController {
    
    private final UserService userService;
    
    @GetMapping
    @Operation(summary = "Page query users")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Page<User>> list(@PageableDefault(size = 10) Pageable pageable) {
        return Result.success(userService.listUsers(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user details")
    @PreAuthorize("isAuthenticated()")
    public Result<User> get(@PathVariable Long id) {
        return Result.success(userService.getUserById(id));
    }

    @PostMapping
    @Operation(summary = "Create user")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<User> create(@Validated @RequestBody UserRequest request) {
        User user = new User();
        BeanUtils.copyProperties(request, user);
        return Result.success(userService.createUser(user, request.getRoleId()));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update user")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<User> update(@PathVariable Long id, @Validated @RequestBody UserRequest request) {
        User user = new User();
        BeanUtils.copyProperties(request, user);
        return Result.success(userService.updateUser(id, user, request.getRoleId()));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> delete(@PathVariable Long id) {
        userService.deleteUser(id);
        return Result.success();
    }
}