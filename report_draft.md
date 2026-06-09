# 毕业设计报告

## 项目名称：基于 Spring Boot 与 Vue 3 的图书管理系统的设计与实现

---

## 一、项目概述

### 1.1 项目背景

随着高校图书馆藏书规模不断扩大和读者数量持续增长，传统的人工管理方式已难以满足高效、准确的图书借阅管理需求。图书借阅过程中容易出现记录错漏、逾期难以追踪、库存信息滞后等问题，亟需一套信息化管理系统来提升管理效率和服务质量。

本项目面向中小型图书馆的数字化管理需求，设计并实现了一套基于 B/S（Browser/Server）架构的图书管理系统。系统采用前后端分离的开发模式，将图书资源管理、读者借阅流程、通知公告发布、用户角色权限管理等核心业务进行数字化整合，为图书馆管理员和普通读者提供统一的 Web 操作平台。

### 1.2 项目目的与意义

本项目的目的旨在通过信息化手段替代传统的手工管理方式，具体体现为以下方面：

（1）**提高管理效率**：系统通过自动化的图书管理和借阅流程，简化了传统的图书登记、借还手续，大幅提升了图书馆管理员的工作效率，降低了管理成本。

（2）**优化读者体验**：读者可以通过系统快速检索图书、查询个人借阅记录、在线办理借阅和归还手续，获得更加便捷的图书服务体验。

（3）**数据准确可靠**：系统实现了库存自动管理、借阅数量限制、逾期自动检测等功能，避免了人工记录容易出现的差错。

（4）**权限安全保障**：基于 RBAC（基于角色的访问控制）模型实现细粒度权限管理，确保不同角色只能访问授权范围内的功能。

### 1.3 技术选型

在技术选型上，项目采用当前主流的前后端分离架构：

| 层级 | 技术 | 版本 |
|------|------|------|
| 后端框架 | Spring Boot | 3.3.7 |
| 持久层 | Spring Data JPA | 3.3.7 |
| 安全框架 | Spring Security | 6.3.7 |
| 认证方式 | JWT (jjwt) | 0.11.5 |
| API 文档 | Knife4j (OpenAPI3) | 4.3.0 |
| 前端框架 | Vue | 3.4 |
| 前端 UI | Element Plus | 2.5 |
| 状态管理 | Pinia | 2.1 |
| 路由管理 | Vue Router | 4.2 |
| HTTP 客户端 | Axios | 1.6 |
| 构建工具 | Vite | 5.0 |
| 数据库 | MySQL | 8.x |
| 运行环境 | JDK | 17 |
| 运行环境 | Node.js | 18+ |

---

## 二、项目制作过程

### 2.1 需求分析

项目从需求分析开始，将系统划分为管理员和读者两种角色视角，梳理出以下核心功能模块：

**（1）认证模块**：支持用户注册、登录、Token 认证和密码修改功能。采用 BCrypt 对密码进行加密存储，登录成功后返回 JWT Token 供后续请求使用。

**（2）图书管理模块**：支持图书的增删改查操作，提供按书名、作者、ISBN、出版社、分类等多条件组合搜索功能，支持分页展示和库存管理。

**（3）借阅管理模块**：实现完整的图书借阅与归还流程，包含借阅数量限制（最多 5 本）、借阅期限（30 天）、库存自动扣减与恢复、逾期自动检测等功能。

**（4）读者管理模块**：管理员可对读者账号进行统一管理，包括用户信息的增删改查、角色分配和账户状态控制。

**（5）通知管理模块**：支持管理员发布公告通知，包含通知的创建、编辑、发布和删除操作，所有用户均可查看已发布的通知。

**（6）角色权限管理模块**：基于 RBAC 模型实现细粒度权限控制，支持角色和权限的增删改查，通过 @PreAuthorize 注解在接口层面进行权限校验。

**（7）数据统计模块**：提供仪表盘数据概览，包括图书总数、借阅总数、在借数量、用户总数等统计信息，以及最近借阅记录动态展示。

### 2.2 数据库设计

系统采用 MySQL 作为关系型数据库，基于 JPA 实体映射设计了六张核心数据表：

**（1）图书表（t_book）**：存储图书基本信息，包括主键 ID、书名（title）、作者（author）、ISBN、出版社（publisher）、出版日期（publish_date）、价格（price）、库存数量（stock）、分类（category）、描述（description）、馆藏位置（location）、创建时间和更新时间。在书名、作者、ISBN 字段上建立了索引以提升检索性能。

**（2）用户表（t_user）**：存储用户信息，包括主键 ID、用户名（username）、密码（password）、邮箱（email）、电话（phone）、状态（status）、创建时间和更新时间。用户名字段设置了唯一约束。用户与角色之间采用多对多关联关系，通过中间表 t_user_role 维护关联。

**（3）角色表（t_role）**：存储角色信息，包括主键 ID、角色名称（role_name）、描述（description）、创建时间和更新时间。角色与权限之间采用多对多关联关系，通过中间表 t_role_permission 维护关联。

**（4）权限表（t_permission）**：存储权限信息，包括主键 ID、权限名称（permission_name）、权限代码（permission_code）、描述（description）、创建时间和更新时间。权限代码字段设置了唯一约束。

**（5）借阅记录表（t_borrow_record）**：存储借阅记录，包括主键 ID、关联用户（user_id）、关联图书（book_id）、借阅日期（borrow_date）、应还日期（due_date）、实际归还日期（return_date）、状态（status：0-借阅中、1-已归还、2-已逾期）、创建时间和更新时间。在用户 ID、图书 ID 和状态字段上建立了索引。

**（6）通知表（t_notice）**：存储通知公告信息，包括主键 ID、标题（title）、内容（content）、状态（status：0-草稿、1-已发布）、作者（author）、创建时间、更新时间和发布时间。

### 2.3 后端开发

后端开发采用标准的分层架构模式，依次完成以下工作：

**第一，项目骨架搭建**。基于 Spring Initializr 创建 Spring Boot 项目，集成 spring-boot-starter-web、spring-boot-starter-data-jpa、spring-boot-starter-security、spring-boot-starter-validation 等 Starter 依赖，配置 MySQL 数据源和 JPA 持久化参数。集成 Knife4j 生成 RESTful API 文档，集成 JWT 依赖实现无状态认证。

**第二，实体类与数据访问层开发**。编写 Book、User、BorrowRecord、Notice、Role、Permission 六个 JPA 实体类，利用 @Entity、@Table、@Column 等注解定义数据库映射关系，使用 @PrePersist 和 @PreUpdate 生命周期回调自动维护创建和更新时间。编写对应 Repository 接口，继承 JpaRepository 和 JpaSpecificationExecutor，通过 @Query 注解编写 JPQL 查询，使用 JOIN FETCH 预加载关联实体，避免 N+1 查询性能问题。

**第三，业务逻辑层与控制器层开发**。实现 AuthService、BookService、BorrowService、NoticeService、UserService、RoleService、PermissionService、StatisticsService 八个 Service 类，完成各模块的业务逻辑。在 Controller 层定义 RESTful 风格的 API 端点，统一使用 Result<T> 封装标准响应格式。通过 @PreAuthorize 注解在接口层面进行细粒度权限控制。

**第四，认证与授权机制实现**。自定义 JwtTokenFilter 实现 JWT Token 拦截器，用户登录成功后返回 Token，后续请求在过滤器中解析 Token 并设置 Spring Security 认证上下文。编写 CustomUserDetailsService 实现用户详情加载，通过 BCryptPasswordEncoder 进行密码加密与校验。SecurityConfig 中配置了免认证路径（登录注册接口、API 文档）和认证路径，采用无状态会话策略（STATELESS）。

**第五，定时任务与拦截器**。编写 BorrowOverdueTask 定时任务类，使用 @Scheduled 注解设置 cron 表达式 \"0 0 2 * * ?\"，每日凌晨 2 点自动扫描状态为\"借阅中\"且已超过应还日期的借阅记录，将其标记为\"已逾期\"。实现 LogInterceptor 记录每个 API 请求的 URI、方法和响应耗时。

**第六，全局异常处理**。通过 @RestControllerAdvice 编写 GlobalExceptionHandler，统一处理参数验证异常（MethodArgumentNotValidException）、认证异常（BadCredentialsException）、权限不足异常（AccessDeniedException）、业务异常（BusinessException）和运行时异常（RuntimeException），返回标准化的错误响应。

**关键代码示例 — 借阅服务（BorrowService）：**

借阅流程包含用户身份校验、图书存在性检查、库存验证、借阅数量限制（最多 5 本）、重复借阅检查、借阅记录创建和库存扣减等多个步骤。归还流程则实现借阅记录状态更新、实际归还日期记录和库存恢复。两个操作均在 @Transactional 事务保护下执行，确保数据一致性。

`java
@Transactional
public BorrowRecord borrowBook(Long bookId) {
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new BusinessException(\"用户不存在\"));
    
    Book book = bookRepository.findById(bookId)
            .orElseThrow(() -> new BusinessException(\"图书不存在\"));
    
    if (book.getStock() == null || book.getStock() <= 0) {
        throw new BusinessException(\"图书库存不足\");
    }
    
    long currentBorrowCount = borrowRecordRepository.countCurrentBorrowsByUserId(user.getId());
    if (currentBorrowCount >= MAX_BORROW_COUNT) {
        throw new BusinessException(\"借阅数量已达上限(5本)\");
    }
    
    BorrowRecord record = new BorrowRecord();
    record.setUser(user);
    record.setBook(book);
    record.setBorrowDate(LocalDate.now());
    record.setDueDate(LocalDate.now().plusDays(30));
    record.setStatus(0);
    
    book.setStock(book.getStock() - 1);
    bookRepository.save(book);
    
    return borrowRecordRepository.save(record);
}
`

### 2.4 前端开发

前端开发使用 Vue 3 + Vite 脚手架搭建项目，引入 Element Plus 组件库构建用户界面。

**（1）项目初始化**。使用 Vite 创建 Vue 3 项目，安装 vue-router、pinia、axios、element-plus 等依赖。配置 Vite 构建工具和开发服务器代理，将前端请求代理到后端 Spring Boot 服务器（localhost:8080）。

**（2）项目结构组织**。采用模块化目录结构：src/api/ 存放 API 请求模块（auth、book、borrow、notice、permission、role、statistics、user），src/views/ 存放页面组件（登录页、仪表盘、图书管理、借阅管理、读者管理、通知管理、系统设置、个人中心），src/router/ 存放路由配置，src/store/ 存放状态管理，src/layout/ 存放布局组件，src/utils/ 存放工具函数。

**（3）HTTP 请求封装**。通过 Axios 封装 request.js 工具模块，设置请求基地址、请求拦截器（自动携带 Token 到 Authorization 请求头）、响应拦截器（统一处理 401 未认证跳转登录、业务错误提示）。所有 API 模块统一使用封装后的 request 实例。

**（4）路由守卫实现**。在 router/index.js 中配置路由导航守卫，未登录用户访问受保护路由时自动重定向到登录页。管理员专属路由（系统设置、读者管理）对非管理员用户进行拦截并重定向。

**（5）页面开发**。登录页使用 Element Plus Form 组件实现用户登录表单，提交后调用后端登录接口获取 Token 并存储到 sessionStorage。仪表盘页面通过 StatisticsService 接口获取统计数据，使用 el-card 和 el-row/el-col 布局组件展示图书总数、借阅总数、在借数量、用户总数四个统计卡片，以及最近借阅记录表格和快捷操作按钮。图书管理页实现了分页列表展示、多条件组合搜索（书名、作者、ISBN、出版社、分类）、新增/编辑/删除图书功能。借阅管理页实现了借阅历史查询和归还操作。通知管理页实现了通知的发布、编辑和删除。读者管理页实现了用户信息的增删改查和角色分配。系统设置页集成了角色管理和权限管理功能。

---

## 三、成果展示

### 3.1 功能成果

项目最终交付了一个功能完整的图书管理系统，主要成果体现在以下几个方面：

**（1）完整的图书借阅业务流程**：系统涵盖图书检索与管理、借阅与归还、逾期自动检测、通知公告发布、用户与权限管理等完整的图书借阅业务环节，形成了从图书入库到借阅归还的闭环管理。

**（2）基于 RBAC 的多角色权限体系**：系统搭建了基于 RBAC 的权限模型，支持多角色管理。管理员可进行图书增删改、用户管理、角色分配、通知发布等操作；普通读者可检索图书、自助借阅和查看个人记录。管理员与普通读者之间的功能权限严格隔离，通过 Spring Security 的 @PreAuthorize 注解在接口层面实现细粒度控制。

**（3）智能逾期检测**：定时任务模块每天凌晨 2 点自动运行，无需人工干预即可扫描并标记逾期记录，有效解决了人工追踪逾期图书的困难。

**（4）完善的搜索与分页**：图书管理支持按书名、作者、ISBN、出版社、分类等多条件模糊匹配搜索，列表页面均支持分页展示，方便用户快速定位目标图书。

### 3.2 技术成果

**（1）前后端分离架构**：系统采用前后端分离架构，前后端通过 JSON 格式的 RESTful API 通信，模块之间耦合度低，便于后续独立扩展和维护。

**（2）安全认证机制**：通过 JWT 无状态认证 + BCrypt 密码加密 + 接口级权限注解，形成了从登录到数据访问的完整安全防护链。JWT Token 有效期设置为 7 天，兼顾安全性与用户体验。

**（3）数据库优化**：数据库层面利用 JPA 的自动建表功能（ddl-auto: update）提升开发效率，使用 JOIN FETCH 预加载优化关联查询性能，在关键查询字段上建立索引以提升检索速度。

**（4）标准化开发规范**：统一封装 Result<T> 响应格式，全局异常处理器统一处理各类异常，Knife4j 自动生成接口文档，接口地址、参数说明和响应示例清晰可查，便于团队开发和前后端联调。

### 3.3 系统运行环境

系统运行环境配置如下：

| 环境要求 | 配置 |
|---------|------|
| 后端运行 | JDK 17，Spring Boot 内嵌 Tomcat，端口 8080 |
| 前端开发 | Node.js 18+，Vite 5.0 开发服务器 |
| 数据库 | MySQL 8.x |
| 浏览器 | 现代浏览器（Chrome、Firefox、Edge 等） |

---

## 四、总结反思

### 4.1 项目收获

通过本次项目开发，系统掌握了从需求分析、数据库设计、后端接口开发到前端界面构建的完整项目流程。在技术层面有以下收获：

**（1）Spring Boot 框架应用**：深入理解了 Spring Boot 的自动配置原理，掌握了 Starter 依赖的使用方式，学会了通过 application.yml 配置文件管理不同环境（开发、生产）的配置参数。对 JPA 的懒加载与事务管理机制有了更深入的理解，掌握了 @Transactional、@PrePersist、@PreUpdate 等注解的实际应用。

**（2）安全认证机制**：通过实现 JWT Token 的生成与校验流程，理解了无状态认证的工作原理。掌握了 Spring Security 的过滤器链配置方法，学会了通过 @PreAuthorize 注解在接口层面实现细粒度权限控制。

**（3）前端工程化开发**：通过 Vue 3 组合式 API（Composition API）和 Pinia 状态管理，熟悉了现代前端工程的开发模式。掌握了 Vue Router 路由守卫的实现方式、Axios 请求拦截器的使用，以及 Element Plus 组件库的综合应用。

**（4）工程化能力提升**：Knife4j 自动生成接口文档的能力在实际开发中节省了大量文档编写时间，也体会到了良好 API 设计对前后端协作的促进作用。Vite 作为前端构建工具，其快速的 HMR（热模块替换）功能显著提升了开发体验。

### 4.2 存在不足

虽然系统已经实现了基本的功能需求，但仍存在一些不足之处：

**（1）未实现图书封面上传**：当前图书管理不支持图书封面图片上传功能，图书展示效果较为单一，用户体验有待提升。

**（2）未引入缓存机制**：系统暂未引入 Redis 等缓存技术，在高并发场景下数据库压力较大，查询性能可能成为瓶颈。

**（3）通知推送方式单一**：通知公告模块仅支持系统内查看，暂不支持站内信或邮件推送，读者可能错过重要通知。

**（4）移动端适配不足**：系统主要针对 PC 端浏览器设计，在移动端浏览器中的响应式适配不够完善。

### 4.3 改进方向

针对上述不足，未来的改进方向包括：

**（1）集成文件存储服务**：集成本地文件存储或对象存储服务（如 MinIO、阿里云 OSS），实现图书封面图片的上传与展示功能，丰富图书信息展示内容。

**（2）引入 Redis 缓存**：引入 Redis 缓存图书分类、热门搜索、统计数据等热点数据，降低数据库查询压力，提升系统在高并发场景下的响应性能。

**（3）接入消息推送服务**：接入邮件服务（如 SMTP）或消息推送服务，在图书即将逾期时自动发送提醒通知，实现通知的主动触达。

**（4）优化前端响应式布局**：进一步优化前端响应式布局设计，提升系统在移动端、平板设备上的访问体验，或考虑开发微信小程序版本。

**（5）搜索功能增强**：当前图书搜索仅支持简单的模糊匹配，未来可以考虑引入 Elasticsearch 实现全文检索和分词搜索功能，提升搜索准确性和检索效率。
