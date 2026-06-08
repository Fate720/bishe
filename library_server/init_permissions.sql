-- 1. 临时关闭外键检查（防止删除顺序报错）
SET FOREIGN_KEY_CHECKS = 0;

-- 2. 清理旧数据
DELETE FROM t_user_role;
DELETE FROM t_role_permission;
DELETE FROM t_permission;
DELETE FROM t_role;
DELETE FROM t_user WHERE username = 'admin';

-- 3. 插入角色数据
INSERT INTO t_role (id, role_name, description, created_time, updated_time) VALUES
(1, 'ROLE_ADMIN', '系统管理员，拥有所有权限', NOW(), NOW()),
(2, 'ROLE_USER', '普通用户，可以借阅图书', NOW(), NOW());

-- 4. 插入权限数据
INSERT INTO t_permission (id, permission_name, permission_code, description, created_time, updated_time) VALUES
(1, '图书管理', 'book:manage', '管理图书信息（增删改查）', NOW(), NOW()),
(2, '借阅管理', 'borrow:manage', '管理借阅记录', NOW(), NOW()),
(3, '用户管理', 'user:manage', '管理用户信息', NOW(), NOW()),
(4, '通知管理', 'notice:manage', '发布和管理通知', NOW(), NOW()),
(5, '系统设置', 'system:manage', '管理系统角色和权限', NOW(), NOW());

-- 5. 为管理员角色分配所有权限
INSERT INTO t_role_permission (role_id, permission_id) VALUES
(1, 1), (1, 2), (1, 3), (1, 4), (1, 5);

-- 6. 为普通用户角色分配部分权限
INSERT INTO t_role_permission (role_id, permission_id) VALUES
(2, 1), (2, 2);

-- 7. 创建 admin 用户（密码：123456，已 BCrypt 加密）
INSERT INTO t_user (username, password, email, status, created_time, updated_time) 
VALUES (
  'admin', 
  '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 
  'admin@test.com', 
  1, 
  NOW(), 
  NOW()
);

-- 8. 为 admin 用户分配管理员角色
INSERT INTO t_user_role (user_id, role_id) 
SELECT u.id, 1 
FROM t_user u 
WHERE u.username = 'admin';

-- 9. 恢复外键检查
SET FOREIGN_KEY_CHECKS = 1;

-- 初始化完成！请使用 admin / 123456 登录。