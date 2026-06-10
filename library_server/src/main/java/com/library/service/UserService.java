package com.library.service;

import com.library.entity.BorrowRecord;
import com.library.entity.Role;
import com.library.entity.User;
import com.library.exception.BusinessException;
import com.library.repository.BorrowRecordRepository;
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
    private final BorrowRecordRepository borrowRecordRepository;
    private final PasswordEncoder passwordEncoder;
    
    public Page<User> listUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }
    
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new BusinessException("User not found"));
    }
    
    @Transactional
    public User createUser(User user, Long roleId) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new BusinessException("Username already exists");
        }
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            throw new BusinessException("Password cannot be empty");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (roleId != null) {
            Role role = roleRepository.findById(roleId)
                    .orElseThrow(() -> new BusinessException("Role not found"));
            user.setRoles(new HashSet<>(List.of(role)));
        }
        return userRepository.save(user);
    }
    
    @Transactional
    public User updateUser(Long id, User user, Long roleId) {
        User existing = getUserById(id);
        if (user.getUsername() != null) existing.setUsername(user.getUsername());
        if (user.getEmail() != null) existing.setEmail(user.getEmail());
        if (user.getPhone() != null) existing.setPhone(user.getPhone());
        if (user.getStatus() != null) existing.setStatus(user.getStatus());
        if (user.getPassword() != null && !user.getPassword().trim().isEmpty()) {
            existing.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        if (roleId != null) {
            Role role = roleRepository.findById(roleId)
                    .orElseThrow(() -> new BusinessException("Role not found"));
            existing.setRoles(new HashSet<>(List.of(role)));
            roleRepository.flush();
        }
        return userRepository.save(existing);
    }
    
    @Transactional
    public void deleteUser(Long id) {
        User user = getUserById(id);
        List<BorrowRecord> activeRecords = borrowRecordRepository.findByUserIdAndStatus(id, 0);
        if (!activeRecords.isEmpty()) {
            throw new BusinessException("该读者有未归还的图书，请先归还后再删除");
        }
        borrowRecordRepository.findByUserId(id).forEach(record -> {
            borrowRecordRepository.delete(record);
        });
        userRepository.deleteById(id);
    }
}