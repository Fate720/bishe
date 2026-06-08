package com.library.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * 用户创建/更新请求 DTO
 */
@Data
public class UserRequest {
    private Long id;

    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 20, message = "用户名长度必须在3-20个字符之间")
    private String username;

    @Size(min = 6, max = 100, message = "密码长度不能少于6个字符")
    private String password;

    private String email;

    private String phone;

    private Integer status;

    private List<Long> roleIds;
}
