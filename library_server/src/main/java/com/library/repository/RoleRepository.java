package com.library.repository;

import com.library.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    
    /**
     * 根据角色名称查询
     */
    Optional<Role> findByRoleName(String roleName);
    
    /**
     * 检查角色名称是否存在
     */
    boolean existsByRoleName(String roleName);
}
