<template>
  <div class="borrow-container">
    <el-card>
      <div class="header">
        <h3>借阅记录</h3>
        <el-radio-group v-model="statusFilter" @change="fetchData">
          <el-radio-button label="">全部</el-radio-button>
          <el-radio-button label="0">借阅中</el-radio-button>
          <el-radio-button label="1">已归还</el-radio-button>
        </el-radio-group>
      </div>

      <el-table :data="tableData" v-loading="loading" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column label="图书" prop="book.title" />
        <el-table-column label="借阅人" prop="user.username" />
        <el-table-column prop="borrowDate" label="借阅日期" width="120" />
        <el-table-column prop="dueDate" label="应还日期" width="120" />
        <el-table-column prop="returnDate" label="归还日期" width="120" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.status === 0" type="warning">借阅中</el-tag>
            <el-tag v-else-if="row.status === 1" type="success">已归还</el-tag>
            <el-tag v-else type="danger">已逾期</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button 
              v-if="row.status === 0" 
              size="small" 
              type="success"
              @click="handleReturn(row)"
            >
              归还
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
          v-model:current-page="pageNum"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          @current-change="fetchData"
          @size-change="fetchData"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getMyBorrows, returnBook } from '@/api/borrow'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const statusFilter = ref('')

const fetchData = async () => {
  loading.value = true
  try {
    const params = {
      page: pageNum.value - 1,
      size: pageSize.value
    }
    if (statusFilter.value !== '') {
      params.status = statusFilter.value
    }
    const res = await getMyBorrows(params)
    tableData.value = res.data.content
    total.value = res.data.totalElements
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handleReturn = async (row) => {
  try {
    await ElMessageBox.confirm('确认归还该图书吗？', '提示', {
      type: 'warning'
    })
    await returnBook(row.id)
    ElMessage.success('归还成功')
    fetchData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
    }
  }
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.borrow-container {
  padding: 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.header h3 {
  margin: 0;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
