package com.library.service;

import com.library.entity.Role;
import com.library.exception.BusinessException;
import com.library.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleService {
    
    private final RoleRepository roleRepository;
    
    public Page<Role> listRoles(Pageable pageable) {
        return roleRepository.findAll(pageable);
    }
    
    public List<Role> listAllRoles() {
        return roleRepository.findAll();
    }
    
    public Role getById(Long id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new BusinessException("角色不存在"));
    }
    
    @Transactional
    public Role create(Role role) {
        // 检查角色名是否已存在
        if (roleRepository.existsByRoleName(role.getRoleName())) {
            throw new BusinessException("角色名称已存在");
        }
        return roleRepository.save(role);
    }
    
    @Transactional
    public Role update(Long id, Role role) {
        Role existing = getById(id);
        existing.setRoleName(role.getRoleName());
        existing.setDescription(role.getDescription());
        return roleRepository.save(existing);
    }
    
    @Transactional
    public void delete(Long id) {
        if (!roleRepository.existsById(id)) {
            throw new BusinessException("角色不存在");
        }
        roleRepository.deleteById(id);
    }
}
