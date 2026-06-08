<template>
  <div class="layout-container">
    <el-container>
      <el-aside width="220px" class="sidebar">
        <div class="logo">
          <el-icon class="logo-icon"><Reading /></el-icon>
          <h3>图书管理系统</h3>
        </div>
        
        <el-menu 
          :default-active="$route.path" 
          router
          class="sidebar-menu"
          background-color="#304156"
          text-color="#bfcbd9"
          active-text-color="#409EFF"
          :unique-opened="true"
        >
          <el-menu-item index="/dashboard">
            <el-icon><HomeFilled /></el-icon>
            <span>首页</span>
          </el-menu-item>
          
          <el-menu-item index="/book">
            <el-icon><Reading /></el-icon>
            <span>图书管理</span>
          </el-menu-item>
          
          <el-menu-item index="/borrow">
            <el-icon><Tickets /></el-icon>
            <span>借阅管理</span>
          </el-menu-item>
          
          <el-menu-item v-if="isAdmin" index="/reader">
            <el-icon><User /></el-icon>
            <span>读者管理</span>
          </el-menu-item>
          
          <el-menu-item index="/notice">
            <el-icon><Bell /></el-icon>
            <span>通知管理</span>
          </el-menu-item>
          
          <el-menu-item v-if="isAdmin" index="/system">
            <el-icon><Setting /></el-icon>
            <span>系统设置</span>
          </el-menu-item>

          <el-menu-item index="/profile">
            <el-icon><Avatar /></el-icon>
            <span>个人中心</span>
          </el-menu-item>
        </el-menu>
        
        <!-- 用户信息 -->
        <div class="user-info">
          <el-icon class="user-icon"><User /></el-icon>
          <span class="user-name">{{ username }}</span>
          <el-tag v-if="isAdmin" size="small" type="warning" class="role-tag">管理员</el-tag>
        </div>
      </el-aside>
      
      <el-container>
        <el-header>
          <div class="header-content">
            <span>欢迎使用图书管理系统</span>
            <el-button type="danger" @click="handleLogout">退出登录</el-button>
          </div>
        </el-header>
        
        <el-main>
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { HomeFilled, Reading, Tickets, User, Bell, Setting } from '@element-plus/icons-vue'

const router = useRouter()

const isAdmin = sessionStorage.getItem('isAdmin') === 'true'
const username = ref(sessionStorage.getItem('username') || '用户')

const handleLogout = () => {
  sessionStorage.removeItem('token')
  sessionStorage.removeItem('isAdmin')
  sessionStorage.removeItem('username')
  ElMessage.success('已退出登录')
  router.push('/login')
}

onMounted(() => {
  // 从 sessionStorage 获取用户名
  const storedUsername = sessionStorage.getItem('username')
  if (storedUsername) {
    username.value = storedUsername
  }
})
</script>

<style scoped>
.layout-container {
  height: 100vh;
}

.sidebar {
  background-color: #304156;
  color: white;
  display: flex;
  flex-direction: column;
  box-shadow: 2px 0 6px rgba(0, 21, 41, 0.35);
  height: 100vh;
  overflow: hidden;
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #1a2332 0%, #263445 100%);
  border-bottom: 1px solid #1f2d3d;
}

.logo-icon {
  font-size: 24px;
  margin-right: 8px;
  color: #409EFF;
}

.logo h3 {
  margin: 0;
  color: white;
  font-size: 16px;
  font-weight: 600;
  letter-spacing: 1px;
}

.sidebar-menu {
  border-right: none;
  flex: 1;
  overflow-y: auto;
}

.sidebar-menu .el-menu-item {
  height: 50px;
  min-height: 50px;
  line-height: 50px;
  margin: 4px 8px;
  border-radius: 4px;
  transition: all 0.3s;
}

.sidebar-menu .el-menu-item:hover {
  background-color: #263445 !important;
}

.sidebar-menu .el-menu-item.is-active {
  background-color: #409EFF !important;
  color: white !important;
}

.user-info {
  padding: 16px;
  background-color: #263445;
  border-top: 1px solid #1f2d3d;
  display: flex;
  align-items: center;
  gap: 8px;
  flex-shrink: 0;
}

.user-icon {
  font-size: 20px;
  color: #409EFF;
}

.user-name {
  flex: 1;
  color: #bfcbd9;
  font-size: 14px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.role-tag {
  font-size: 12px;
}

.el-header {
  background-color: white;
  border-bottom: 1px solid #e6e6e6;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
}

.header-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
}
</style>
