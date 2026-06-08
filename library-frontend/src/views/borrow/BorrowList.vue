<template>
  <div class="borrow-container">
    <!-- 普通用户：借阅/归还图书 -->
    <template v-if="!isAdmin">
      <el-tabs v-model="activeTab">
        <!-- 借阅图书 -->
        <el-tab-pane label="借阅图书" name="borrow">
          <el-card>
            <el-form :model="borrowForm" label-width="100px">
              <el-form-item label="搜索图书">
                <el-select
                  v-model="borrowForm.selectedBookId"
                  filterable
                  remote
                  reserve-keyword
                  clearable
                  :remote-method="searchForBorrow"
                  :loading="searchLoading"
                  placeholder="输入书名、作者或ISBN搜索图书"
                  style="width: 100%"
                  @change="onBookSelected"
                >
                  <el-option
                    v-for="book in searchResults"
                    :key="book.id"
                    :label="book.title + ' (' + book.author + ')'"
                    :value="book.id"
                  >
                    <div style="display: flex; justify-content: space-between">
                      <span>{{ book.title }}<span style="color: #909399; margin-left: 8px">{{ book.author }}</span></span>
                      <span style="color: #c0c4cc; font-size: 12px">{{ book.isbn || '' }}</span>
                    </div>
                  </el-option>
                </el-select>
              </el-form-item>

              <!-- 图书详情 -->
              <el-form-item v-if="selectedBook" label="图书信息">
                <el-card shadow="never" style="width: 100%; background: #f5f7fa">
                  <el-descriptions :column="2" border size="small">
                    <el-descriptions-item label="书名" :span="2">{{ selectedBook.title }}</el-descriptions-item>
                    <el-descriptions-item label="作者">{{ selectedBook.author }}</el-descriptions-item>
                    <el-descriptions-item label="ISBN">{{ selectedBook.isbn || '无' }}</el-descriptions-item>
                    <el-descriptions-item label="出版社">{{ selectedBook.publisher || '无' }}</el-descriptions-item>
                    <el-descriptions-item label="库存">
                      <el-tag :type="selectedBook.stock > 0 ? 'success' : 'danger'" size="small">
                        {{ selectedBook.stock > 0 ? selectedBook.stock + ' 本可借' : '暂无库存' }}
                      </el-tag>
                    </el-descriptions-item>
                  </el-descriptions>
                </el-card>
              </el-form-item>

              <el-form-item>
                <el-button
                  type="primary"
                  @click="handleBorrow"
                  :loading="borrowLoading"
                  :disabled="!borrowForm.selectedBookId || (selectedBook && selectedBook.stock <= 0)"
                >
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
                    :label="record.bookTitle + ' (作者: ' + (record.bookAuthor || '未知') + ' | ISBN: ' + (record.bookIsbn || '无') + ' | 借阅: ' + record.borrowDate + ')'"
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
            <el-table :data="records" v-loading="loading" style="width: 100%" @sort-change="handleSortChange">
              <el-table-column prop="id" label="记录ID" width="80" />
              <el-table-column label="图书信息" min-width="200">
                <template #default="{ row }">
                  <div>
                    <div style="font-weight: 500">{{ row.bookTitle }}</div>
                    <div style="font-size: 12px; color: #909399; margin-top: 2px">
                      {{ row.bookAuthor || '' }}{{ row.bookAuthor && row.bookIsbn ? ' | ' : '' }}{{ row.bookIsbn || '' }}
                    </div>
                  </div>
                </template>
              </el-table-column>
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
        <div style="margin-bottom: 16px">
            <span style="margin-right: 12px; font-size: 14px; color: #606266">状态筛选：</span>
            <el-radio-group v-model="statusFilter" @change="onStatusFilterChange">
              <el-radio-button :label="null">全部</el-radio-button>
              <el-radio-button :label="0">借阅中</el-radio-button>
              <el-radio-button :label="1">已归还</el-radio-button>
            </el-radio-group>
          </div>
        <el-table :data="allRecords" v-loading="loading" style="width: 100%" @sort-change="handleSortChange">
          <el-table-column prop="id" label="记录ID" width="80" />
          <el-table-column label="借阅人" prop="username" width="100" sortable="custom" />
          <el-table-column label="图书信息" min-width="200">
            <template #default="{ row }">
              <div>
                <div style="font-weight: 500">{{ row.bookTitle }}</div>
                <div style="font-size: 12px; color: #909399; margin-top: 2px">
                  {{ row.bookAuthor || '' }}{{ row.bookAuthor && row.bookIsbn ? ' | ' : '' }}{{ row.bookIsbn || '' }}
                </div>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="borrowDate" label="借阅日期" width="120" />
          <el-table-column prop="dueDate" label="应还日期" width="120" />
          <el-table-column prop="returnDate" label="归还日期" width="120" />
          <el-table-column label="状态" width="100">
            <template #default="{ row }">
              <el-tag v-if="row.status === 0" type="warning">借阅中</el-tag>
              <el-tag v-else-if="row.status === 1" type="success">已归还</el-tag>
              <el-tag v-else type="danger">已过期</el-tag>
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
import { useRoute } from 'vue-router'
import { borrowBook, returnBook, getMyBorrows, getAllBorrows } from '@/api/borrow'
import { searchBooks } from '@/api/book'
import { ElMessage, ElMessageBox } from 'element-plus'

const route = useRoute()
const isAdmin = sessionStorage.getItem('isAdmin') === 'true'
const activeTab = ref('borrow')
const loading = ref(false)
const borrowLoading = ref(false)
const returnLoading = ref(false)
const searchLoading = ref(false)
const sortProp = ref('id')
const sortOrder = ref('descending')
const searchResults = ref([])
const selectedBook = ref(null)

const borrowForm = reactive({
  selectedBookId: null
})

const returnForm = reactive({
  recordId: null
})

const unreturnedRecords = ref([])
const records = ref([])
const allRecords = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const bookDetailCache = ref({})
const statusFilter = ref(null)

const searchForBorrow = async (query) => {
  if (!query || query.trim() === '') {
    searchResults.value = []
    return
  }
  searchLoading.value = true
  try {
    const res = await searchBooks({ keyword: query, size: 10 })
    searchResults.value = res.content || []
  } catch (error) {
    console.error('搜索失败:', error)
    searchResults.value = []
  } finally {
    searchLoading.value = false
  }
}

const onBookSelected = async (bookId) => {
  if (!bookId) {
    selectedBook.value = null
    return
  }
  if (bookDetailCache.value[bookId]) {
    selectedBook.value = bookDetailCache.value[bookId]
    return
  }
  try {
    const { getBook } = await import('@/api/book')
    const book = await getBook(bookId)
    selectedBook.value = book
    bookDetailCache.value[bookId] = book
  } catch (error) {
    console.error('获取图书详情失败:', error)
    selectedBook.value = null
  }
}

const handleBorrow = async () => {
  if (!borrowForm.selectedBookId) {
    ElMessage.warning('请先搜索并选择要借阅的图书')
    return
  }
  borrowLoading.value = true
  try {
    await borrowBook(borrowForm.selectedBookId)
    ElMessage.success('借阅成功')
    borrowForm.selectedBookId = null
    selectedBook.value = null
    searchResults.value = []
    fetchRecords()
  } catch (error) {
    console.error('借阅失败:', error)
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
    returnForm.recordId = null
    if (isAdmin) { fetchAllRecords() } else { fetchRecords() }
  } catch (error) {
    console.error('归还失败:', error)
  } finally {
    returnLoading.value = false
  }
}

const handleReturnById = async (id) => {
  try {
    await ElMessageBox.confirm('确认归还该图书吗？', '提示', { type: 'warning' })
    await returnBook(id)
    ElMessage.success('归还成功')
    if (isAdmin) { fetchAllRecords() } else { fetchRecords() }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('归还失败:', error)
    }
  }
}

const fetchRecords = async () => {
  loading.value = true
  try {
    const res = await getMyBorrows({ page: pageNum.value - 1, size: pageSize.value })
    records.value = res.content
    total.value = res.totalElements
    unreturnedRecords.value = res.content.filter(r => r.status === 0)
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handleSortChange = function(column) {
  if (column && column.prop && column.order) {
    sortProp.value = column.prop
    sortOrder.value = column.order
  } else {
    sortProp.value = 'id'
    sortOrder.value = 'descending'
  }
  if (isAdmin) { fetchAllRecords() } else { fetchRecords() }
}

const fetchAllRecords = async () => {
  loading.value = true
  try {
    var sortMapping = { 'id': 'id', 'bookTitle': 'book.title', 'username': 'user.username' }
    var sortField = sortMapping[sortProp.value] || sortProp.value
    var sortDir = sortOrder.value === 'descending' ? 'desc' : 'asc'
    var params = { page: pageNum.value - 1, size: pageSize.value }
    if (sortField) { params.sort = sortField + ',' + sortDir }
    if (statusFilter.value !== null) { params.status = statusFilter.value }
    const res = await getAllBorrows(params)
    allRecords.value = res.content
    total.value = res.totalElements
    unreturnedRecords.value = res.content.filter(function(r) { return r.status === 0 })
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const onStatusFilterChange = function() {
  fetchAllRecords()
}

onMounted(() => {
  if (route.query.status) {
    statusFilter.value = parseInt(route.query.status)
  }
  if (isAdmin) { fetchAllRecords() } else { fetchRecords() }
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
