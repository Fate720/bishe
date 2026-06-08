package com.library.repository;

import com.library.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {
    
    /**
     * 根据权限代码查询
     */
    Optional<Permission> findByPermissionCode(String permissionCode);
    
    /**
     * 检查权限代码是否存在
     */
    boolean existsByPermissionCode(String permissionCode);
}
