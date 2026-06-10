package com.library.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * User create/update request DTO
 */
@Data
public class UserRequest {
    private Long id;

    @NotBlank(message = "Username cannot be empty")
    @Size(min = 3, max = 20, message = "Username must be 3-20 characters")
    private String username;

    @Size(min = 6, max = 100, message = "Password must be at least 6 characters")
    private String password;

    private String email;

    private String phone;

    private Integer status;

    private Long roleId;
}