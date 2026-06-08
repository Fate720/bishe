# 图书馆管理系统 - 代码改进说明

## 改进概述

本次代码修复和完善主要针对以下几个方面进行了优化：

### 1. 安全性增强

#### JWT配置优化
- **问题**: JWT密钥硬编码在代码中
- **改进**: 
  - 将JWT密钥移至配置文件 `application-dev.yml`
  - 使用 `@Value` 注解从配置文件读取密钥
  - 添加了默认值以防配置缺失

**修改文件**:
- `src/main/java/com/library/util/JwtUtil.java`
- `src/main/resources/application-dev.yml`

### 2. 异常处理统一化

#### 统一使用BusinessException
- **问题**: 部分服务层使用RuntimeException，不利于统一异常处理
- **改进**: 将所有RuntimeException替换为BusinessException

**修改文件**:
- `src/main/java/com/library/service/BookService.java`

### 3. 数据验证增强

#### 添加字段长度验证
- **改进**: 
  - 用户名: 3-20个字符
  - 密码: 最少6个字符
  - 添加了更详细的错误提示信息

**修改文件**:
- `src/main/java/com/library/dto/RegisterRequest.java`
- `src/main/java/com/library/dto/UserRequest.java`

### 4. 业务逻辑完善

#### UserService改进
- **新增功能**:
  - 创建用户时验证密码不为空
  - 更新用户时支持选择性更新（只更新提供的字段）
  - 支持密码更新（如果提供了新密码）

#### BookService改进
- **新增功能**:
  - ISBN唯一性验证
  - 书名模糊查询方法
  - 作者模糊查询方法

#### RoleService & PermissionService改进
- **新增功能**:
  - 创建时检查名称/代码唯一性
  - 避免重复数据

#### NoticeService改进
- **改进**: 支持选择性更新通知字段

### 5. 数据库性能优化

#### 添加索引
- **Book表**: title, author, isbn
- **User表**: username
- **BorrowRecord表**: user_id, book_id, status

**修改文件**:
- `src/main/java/com/library/entity/Book.java`
- `src/main/java/com/library/entity/User.java`
- `src/main/java/com/library/entity/BorrowRecord.java`

### 6. API文档完善

#### 增强Swagger文档
- **改进**: 为所有API接口添加了详细描述
- **好处**: 前端开发人员可以更清楚地了解每个接口的用途

**修改文件**:
- `src/main/java/com/library/controller/AuthController.java`
- `src/main/java/com/library/controller/BookController.java`
- `src/main/java/com/library/controller/BorrowController.java`

### 7. 日志记录增强

#### 全局异常处理日志
- **改进**: 
  - 参数验证失败记录WARN级别日志
  - 业务异常记录WARN级别日志
  - 运行时异常记录ERROR级别日志并包含堆栈信息

**修改文件**:
- `src/main/java/com/library/exception/GlobalExceptionHandler.java`

### 8. Repository层扩展

#### BookRepository新增方法
- `findByIsbn(String isbn)`: 根据ISBN查询
- `findByTitleContaining(String title)`: 书名模糊查询
- `findByAuthorContaining(String author)`: 作者模糊查询

**修改文件**:
- `src/main/java/com/library/repository/BookRepository.java`

## 测试建议

### 1. 功能测试
```bash
# 启动后端服务
mvn spring-boot:run

# 访问API文档
http://localhost:8080/doc.html
```

### 2. 重点测试场景
1. **用户注册**: 测试用户名和密码长度验证
2. **图书管理**: 测试ISBN唯一性验证
3. **借阅功能**: 测试库存检查和重复借阅限制
4. **权限控制**: 测试管理员和普通用户的权限差异

### 3. 数据库验证
检查以下索引是否创建成功：
- t_book: idx_title, idx_author, idx_isbn
- t_user: idx_username
- t_borrow_record: idx_user_id, idx_book_id, idx_status

## 后续优化建议

1. **密码策略**: 添加密码强度验证（大小写、数字、特殊字符）
2. **分页优化**: 对于大数据量场景，考虑使用游标分页
3. **缓存机制**: 添加Redis缓存热门图书和用户信息
4. **审计日志**: 记录重要操作的审计日志
5. **单元测试**: 为核心业务逻辑添加单元测试
6. **API限流**: 添加请求限流防止滥用
7. **数据备份**: 实现定期数据备份机制

## 注意事项

1. **JWT密钥**: 生产环境务必更换为强密钥
2. **数据库密码**: 建议使用环境变量或密钥管理服务
3. **CORS配置**: 生产环境应限制允许的域名
4. **日志级别**: 生产环境调整为INFO或WARN级别

## 总结

本次改进主要提升了系统的安全性、健壮性和可维护性。所有修改都遵循了Spring Boot最佳实践，并且保持了向后兼容性。
