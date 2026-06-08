# 图书管理系统前端

基于 Vue3 + Vite + Element Plus 开发的图书管理系统前端项目

## 技术栈

- Vue 3 - 渐进式 JavaScript 框架
- Vite - 下一代前端构建工具
- Element Plus - 基于 Vue 3 的组件库
- Vue Router - 官方路由管理器
- Pinia - Vue 状态管理库
- Axios - HTTP 客户端

## 项目结构

```
library-web/
├── src/
│   ├── api/              # API 接口封装
│   │   ├── auth.js       # 认证相关接口
│   │   ├── book.js       # 图书相关接口
│   │   └── borrow.js     # 借阅相关接口
│   ├── assets/           # 静态资源
│   ├── components/       # 公共组件
│   ├── layout/           # 布局组件
│   ├── router/           # 路由配置
│   ├── utils/            # 工具函数
│   ├── views/            # 页面组件
│   │   ├── login/        # 登录页
│   │   ├── dashboard/    # 仪表盘
│   │   ├── book/         # 图书管理
│   │   └── borrow/       # 借阅管理
│   ├── App.vue           # 根组件
│   └── main.js           # 入口文件
├── index.html
├── package.json
└── vite.config.js
```

## 快速开始

### 安装依赖

```bash
npm install
```

### 开发模式

```bash
npm run dev
```

访问 http://localhost:3000

### 生产构建

```bash
npm run build
```

### 预览生产构建

```bash
npm run preview
```

## 功能特性

- ✅ 用户登录认证
- ✅ JWT Token 自动管理
- ✅ 图书增删改查
- ✅ 借阅管理
- ✅ 分页查询
- ✅ 路由守卫
- ✅ 响应式布局

## API 代理配置

在 `vite.config.js` 中配置了后端 API 代理：

```javascript
server: {
  port: 3000,
  proxy: {
    '/api': {
      target: 'http://localhost:8080',
      changeOrigin: true
    }
  }
}
```

## 注意事项

1. 确保后端服务在 8080 端口运行
2. 首次使用需要先在后端数据库中创建测试用户
3. 所有 API 请求会自动携带 JWT Token
