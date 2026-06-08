<template>
  <div class="dashboard-container">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #409EFF">
              <el-icon :size="30"><Reading /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.bookCount }}</div>
              <div class="stat-label">图书总数</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #67C23A">
              <el-icon :size="30"><Tickets /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.borrowCount }}</div>
              <div class="stat-label">借阅总数</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #E6A23C">
              <el-icon :size="30"><Clock /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.activeBorrowCount }}</div>
              <div class="stat-label">在借图书</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #F56C6C">
              <el-icon :size="30"><User /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.userCount }}</div>
              <div class="stat-label">用户总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>最近借阅记录</span>
            </div>
          </template>
          <el-table :data="recentBorrows" style="width: 100%" empty-text="暂无借阅记录">
            <el-table-column prop="bookTitle" label="图书" />
            <el-table-column v-if="isAdmin" prop="username" label="借阅人" />
            <el-table-column prop="borrowDate" label="借阅日期" />
          </el-table>
        </el-card>
      </el-col>

      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>快捷操作</span>
            </div>
          </template>
          <div class="quick-actions">
            <el-button type="primary" @click="$router.push('/book')">图书管理</el-button>
            <el-button type="success" @click="$router.push('/borrow')">借阅管理</el-button>
            <el-button type="warning" @click="$router.push('/profile')">个人中心</el-button>
          </div>

          <el-divider />

          <div v-if="stats.activeBorrowCount > 0" class="overdue-tip">
            <el-alert
              title="借阅提醒"
              :description="`您当前有 ${stats.activeBorrowCount} 本图书在借中，记得按时归还哦！`"
              type="info"
              :closable="false"
              show-icon
            />
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Reading, Tickets, Clock, User } from '@element-plus/icons-vue'
import { getStatistics } from '@/api/statistics'

const isAdmin = sessionStorage.getItem('isAdmin') === 'true'

const stats = ref({
  bookCount: 0,
  borrowCount: 0,
  activeBorrowCount: 0,
  userCount: 0
})

const recentBorrows = ref([])

const fetchStatistics = async () => {
  try {
    const res = await getStatistics()
    stats.value = res
    // 获取最近借阅记录
    if (res.recentBorrows) {
      recentBorrows.value = res.recentBorrows
    }
  } catch (error) {
    console.error(error)
  }
}

onMounted(() => {
  fetchStatistics()
})
</script>

<style scoped>
.dashboard-container {
  padding: 20px;
}

.stat-card {
  margin-bottom: 20px;
}

.stat-content {
  display: flex;
  align-items: center;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  margin-right: 15px;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #333;
}

.stat-label {
  font-size: 14px;
  color: #999;
  margin-top: 5px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.quick-actions {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.quick-actions .el-button {
  flex: 1;
  min-width: 120px;
}
</style>
