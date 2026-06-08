<template>
  <div class="borrow-container">
    <!-- 普通用户：借阅/归还图书 -->
    <template v-if="!isAdmin">
      <el-tabs v-model="activeTab">
        <!-- 借阅图书 -->
        <el-tab-pane label="借阅图书" name="borrow">
          <el-card>
            <el-form :model="borrowForm" label-width="100px">
              <el-form-item label="图书ID">
                <el-input-number v-model="borrowForm.bookId" :min="1" style="width: 200px" />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="handleBorrow" :loading="borrowLoading">
                  确认借阅
                </el-button>
              </el-form-item>
            </el-form>
          </el-card>
        </el-tab-pane>

        <!-- 归还图书 -->
        <el-tab-pane label="归还图书" name="return">
          <el-card>
            <el-alert
              title="提示"
              type="info"
              description="请选择要归还的借阅记录"
              :closable="false"
              style="margin-bottom: 20px"
            />
            <el-form label-width="100px">
              <el-form-item label="选择记录">
                <el-select
                  v-model="returnForm.recordId"
                  placeholder="请选择未归还的借阅记录"
                  style="width: 100%"
                >
                  <el-option
                    v-for="record in unreturnedRecords"
                    :key="record.id"
                    :label="`记录${record.id} - ${record.bookTitle} (借阅日期：${record.borrowDate})`"
                    :value="record.id"
                  />
                </el-select>
              </el-form-item>
              <el-form-item>
                <el-button type="success" @click="handleReturn" :loading="returnLoading" :disabled="!returnForm.recordId">
                  确认归还
                </el-button>
              </el-form-item>
            </el-form>
          </el-card>
        </el-tab-pane>

        <!-- 我的借阅记录 -->
        <el-tab-pane label="我的借阅记录" name="list">
          <el-card>
            <el-table :data="records" v-loading="loading" style="width: 100%">
              <el-table-column prop="id" label="记录ID" width="80" />
              <el-table-column label="图书" prop="bookTitle" />
              <el-table-column prop="borrowDate" label="借阅日期" width="120" />
              <el-table-column prop="dueDate" label="应还日期" width="120" />
              <el-table-column prop="returnDate" label="归还日期" width="120" />
              <el-table-column label="状态" width="100">
                <template #default="{ row }">
                  <el-tag v-if="row.status === 0" type="warning">借阅中</el-tag>
                  <el-tag v-else type="success">已归还</el-tag>
                </template>
              </el-table-column>
              <el-table-column label="操作" width="120">
                <template #default="{ row }">
                  <el-button 
                    v-if="row.status === 0" 
                    size="small" 
                    type="success"
                    @click="handleReturnById(row.id)"
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
                @current-change="fetchRecords"
                @size-change="fetchRecords"
              />
            </div>
          </el-card>
        </el-tab-pane>
      </el-tabs>
    </template>

    <!-- 管理员：查看所有借阅记录 -->
    <template v-else>
      <el-card>
        <div class="header">
          <h3>所有借阅记录</h3>
        </div>
        <el-table :data="allRecords" v-loading="loading" style="width: 100%">
          <el-table-column prop="id" label="记录ID" width="80" />
          <el-table-column label="借阅人" prop="username" />
          <el-table-column label="图书" prop="bookTitle" />
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
          <el-table-column label="操作" width="100" fixed="right">
            <template #default="{ row }">
              <el-button
                v-if="row.status === 0"
                size="small"
                type="success"
                @click="handleReturnById(row.id)"
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
            @current-change="fetchAllRecords"
            @size-change="fetchAllRecords"
          />
        </div>
      </el-card>
    </template>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { borrowBook, returnBook, getMyBorrows, getAllBorrows } from '@/api/borrow'
import { ElMessage, ElMessageBox } from 'element-plus'

const isAdmin = sessionStorage.getItem('isAdmin') === 'true'
const activeTab = ref('borrow')
const loading = ref(false)
const borrowLoading = ref(false)
const returnLoading = ref(false)

const borrowForm = reactive({
  bookId: 1
})

const returnForm = reactive({
  recordId: null
})

// 未归还的记录列表
const unreturnedRecords = ref([])

const records = ref([])
const allRecords = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)

const handleBorrow = async () => {
  borrowLoading.value = true
  try {
    await borrowBook(borrowForm.bookId)
    ElMessage.success('借阅成功')
    fetchRecords()
  } catch (error) {
    console.error(error)
    // 显示具体错误信息给用户
    ElMessage.error(error.response?.data?.message || error.message || '借阅失败')
  } finally {
    borrowLoading.value = false
  }
}

const handleReturn = async () => {
  if (!returnForm.recordId) {
    ElMessage.warning('请选择要归还的记录')
    return
  }
  
  returnLoading.value = true
  try {
    await returnBook(returnForm.recordId)
    ElMessage.success('归还成功')
    returnForm.recordId = null // 清空选择
    // 根据角色刷新数据
    if (isAdmin) {
      fetchAllRecords()
    } else {
      fetchRecords()
    }
  } catch (error) {
    console.error(error)
    // 显示具体错误信息给用户
    ElMessage.error(error.response?.data?.message || error.message || '归还失败')
  } finally {
    returnLoading.value = false
  }
}

const handleReturnById = async (id) => {
  try {
    await ElMessageBox.confirm('确认归还该图书吗？', '提示', {
      type: 'warning'
    })
    await returnBook(id)
    ElMessage.success('归还成功')
    // 根据角色刷新数据
    if (isAdmin) {
      fetchAllRecords()
    } else {
      fetchRecords()
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
      ElMessage.error(error.response?.data?.message || error.message || '归还失败')
    }
  }
}

const fetchRecords = async () => {
  loading.value = true
  try {
    const res = await getMyBorrows({
      page: pageNum.value - 1,
      size: pageSize.value
    })
    records.value = res.content
    total.value = res.totalElements
    // 更新未归还记录列表
    unreturnedRecords.value = res.content.filter(record => record.status === 0)
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const fetchAllRecords = async () => {
  loading.value = true
  try {
    const res = await getAllBorrows({
      page: pageNum.value - 1,
      size: pageSize.value
    })
    allRecords.value = res.content
    total.value = res.totalElements
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  if (isAdmin) {
    fetchAllRecords()
  } else {
    fetchRecords()
  }
})
</script>

<style scoped>
.borrow-container {
  padding: 20px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
