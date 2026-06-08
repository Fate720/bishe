package com.library.service;

import com.library.entity.Permission;
import com.library.exception.BusinessException;
import com.library.repository.PermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PermissionService {
    
    private final PermissionRepository permissionRepository;
    
    public Page<Permission> listPermissions(Pageable pageable) {
        return permissionRepository.findAll(pageable);
    }
    
    public List<Permission> listAllPermissions() {
        return permissionRepository.findAll();
    }
    
    public Permission getById(Long id) {
        return permissionRepository.findById(id)
                .orElseThrow(() -> new BusinessException("权限不存在"));
    }
    
    @Transactional
    public Permission create(Permission permission) {
        // 检查权限代码是否已存在
        if (permissionRepository.existsByPermissionCode(permission.getPermissionCode())) {
            throw new BusinessException("权限代码已存在");
        }
        return permissionRepository.save(permission);
    }
    
    @Transactional
    public Permission update(Long id, Permission permission) {
        Permission existing = getById(id);
        existing.setPermissionName(permission.getPermissionName());
        existing.setPermissionCode(permission.getPermissionCode());
        existing.setDescription(permission.getDescription());
        return permissionRepository.save(existing);
    }
    
    @Transactional
    public void delete(Long id) {
        if (!permissionRepository.existsById(id)) {
            throw new BusinessException("权限不存在");
        }
        permissionRepository.deleteById(id);
    }
}
