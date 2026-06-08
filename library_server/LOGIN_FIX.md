# 登录问题解决方案

## 问题描述
无法登录系统，可能是因为数据库中没有初始用户数据。

## 解决方案

### 方法一：执行SQL初始化脚本（推荐）

1. **打开MySQL客户端**（如Navicat、MySQL Workbench或命令行）

2. **连接到数据库**
   - 主机: localhost
   - 端口: 3306
   - 用户名: root
   - 密码: 123456
   - 数据库: book_db

3. **执行初始化脚本**
   ```sql
   -- 复制并执行 init_permissions.sql 文件中的所有内容
   ```

4. **使用以下账号登录**
   - 用户名: `admin`
   - 密码: `123456`

### 方法二：通过注册功能创建新用户

如果数据库中已经有表结构，您可以：

1. 访问前端页面: http://localhost:3000
2. 如果有注册功能，注册一个新用户
3. 然后手动在数据库中为该用户分配角色

### 方法三：直接在数据库中插入测试用户

在MySQL中执行以下SQL：

```sql
-- 1. 临时关闭外键检查
SET FOREIGN_KEY_CHECKS = 0;

-- 2. 插入角色（如果不存在）
INSERT INTO t_role (id, role_name, description, created_time, updated_time) VALUES
(1, 'ROLE_ADMIN', '系统管理员', NOW(), NOW())
ON DUPLICATE KEY UPDATE role_name = role_name;

-- 3. 插入管理员用户（密码: 123456，已BCrypt加密）
INSERT INTO t_user (username, password, email, status, created_time, updated_time) 
VALUES (
  'admin', 
  '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 
  'admin@test.com', 
  1, 
  NOW(), 
  NOW()
) ON DUPLICATE KEY UPDATE username = username;

-- 4. 分配角色
INSERT INTO t_user_role (user_id, role_id) 
SELECT u.id, 1 
FROM t_user u 
WHERE u.username = 'admin'
ON DUPLICATE KEY UPDATE user_id = user_id;

-- 5. 恢复外键检查
SET FOREIGN_KEY_CHECKS = 1;
```

## 验证登录

执行完上述任一方法后：

1. 打开浏览器访问: http://localhost:3000
2. 输入用户名: `admin`
3. 输入密码: `123456`
4. 点击登录

## 常见问题

### 1. 数据库连接失败
- 检查MySQL服务是否启动
- 确认数据库 `book_db` 是否存在
- 检查用户名和密码是否正确

### 2. 登录后立即退出
- 清除浏览器缓存和localStorage
- 检查后端日志是否有错误

### 3. 提示"用户不存在"或"密码错误"
- 确认SQL脚本已成功执行
- 检查t_user表中是否有admin用户
- 确认密码字段是BCrypt加密后的值

## 默认账号信息

| 用户名 | 密码 | 角色 | 说明 |
|--------|------|------|------|
| admin | 123456 | ROLE_ADMIN | 管理员，拥有所有权限 |

## 其他测试账号

如果需要创建普通用户，可以注册新账号，然后在数据库中分配 `ROLE_USER` 角色。
