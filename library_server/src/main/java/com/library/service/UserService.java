package com.library.service;

import com.library.entity.Role;
import com.library.entity.User;
import com.library.exception.BusinessException;
import com.library.repository.RoleRepository;
import com.library.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    
    public Page<User> listUsers(Pageable pageable) {
        Page<User> users = userRepository.findAll(pageable);
        // 触发懒加载，确保 roles 已加载
        users.forEach(user -> user.getRoles().size());
        return users;
    }
    
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new BusinessException("用户不存在"));
    }
    
    @Transactional
    public User createUser(User user, List<Long> roleIds) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new BusinessException("用户名已存在");
        }
        
        // 验证密码不为空
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            throw new BusinessException("密码不能为空");
        }
        
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        // 设置角色
        if (roleIds != null && !roleIds.isEmpty()) {
            Set<Role> roles = new HashSet<>(roleRepository.findAllById(roleIds));
            user.setRoles(roles);
        }
        
        return userRepository.save(user);
    }
    
    @Transactional
    public User updateUser(Long id, User user, List<Long> roleIds) {
        User existing = getUserById(id);
        
        // 更新基本信息
        if (user.getUsername() != null) {
            existing.setUsername(user.getUsername());
        }
        if (user.getEmail() != null) {
            existing.setEmail(user.getEmail());
        }
        if (user.getPhone() != null) {
            existing.setPhone(user.getPhone());
        }
        if (user.getStatus() != null) {
            existing.setStatus(user.getStatus());
        }
        
        // 如果提供了新密码，则更新密码
        if (user.getPassword() != null && !user.getPassword().trim().isEmpty()) {
            existing.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        
        // 更新角色
        if (roleIds != null) {
            Set<Role> roles = new HashSet<>(roleRepository.findAllById(roleIds));
            existing.setRoles(roles);
        }
        
        return userRepository.save(existing);
    }
    
    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new BusinessException("用户不存在");
        }
        userRepository.deleteById(id);
    }
}
