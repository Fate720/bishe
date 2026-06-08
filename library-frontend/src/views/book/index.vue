<template>
  <div class="book-container">
    <el-card>
      <div class="header">
        <h3>图书列表</h3>
        <el-button v-if="isAdmin" type="primary" @click="handleAdd">新增图书</el-button>
      </div>

      <!-- 搜索表单 -->
      <el-form :model="searchForm" inline class="search-form">
        <el-form-item label="书名">
          <el-input v-model="searchForm.title" placeholder="请输入书名" clearable />
        </el-form-item>
        <el-form-item label="作者">
          <el-input v-model="searchForm.author" placeholder="请输入作者" clearable />
        </el-form-item>
        <el-form-item label="ISBN">
          <el-input v-model="searchForm.isbn" placeholder="请输入ISBN" clearable />
        </el-form-item>
        <el-form-item label="出版社">
          <el-input v-model="searchForm.publisher" placeholder="请输入出版社" clearable />
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="searchForm.category" placeholder="请选择分类" clearable style="width: 150px">
            <el-option label="文学" value="文学" />
            <el-option label="科技" value="科技" />
            <el-option label="历史" value="历史" />
            <el-option label="哲学" value="哲学" />
            <el-option label="艺术" value="艺术" />
            <el-option label="教育" value="教育" />
            <el-option label="经济" value="经济" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" native-type="button" @click="handleSearch">搜索</el-button>
          <el-button native-type="button" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" v-loading="loading" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="书名" />
        <el-table-column prop="author" label="作者" />
        <el-table-column prop="isbn" label="ISBN" />
        <el-table-column prop="publisher" label="出版社" />
        <el-table-column prop="category" label="分类" width="80">
          <template #default="{ row }">
            <el-tag v-if="row.category" size="small">{{ row.category }}</el-tag>
            <span v-else style="color: #999">-</span>
          </template>
        </el-table-column>
        <el-table-column prop="price" label="价格" width="100">
          <template #default="{ row }">
            ¥{{ row.price }}
          </template>
        </el-table-column>
        <el-table-column prop="stock" label="库存" width="80" />
        <el-table-column label="操作" width="200" fixed="right" v-if="isAdmin">
          <template #default="{ row }">
            <el-button size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
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
          @current-change="handlePageChange"
          @size-change="handlePageChange"
        />
      </div>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑图书' : '新增图书'" width="600px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="书名" prop="title">
          <el-input v-model="form.title" />
        </el-form-item>
        <el-form-item label="作者" prop="author">
          <el-input v-model="form.author" />
        </el-form-item>
        <el-form-item label="ISBN" prop="isbn">
          <el-input v-model="form.isbn" />
        </el-form-item>
        <el-form-item label="出版社" prop="publisher">
          <el-input v-model="form.publisher" />
        </el-form-item>
        <el-form-item label="分类" prop="category">
          <el-select v-model="form.category" placeholder="请选择分类" style="width: 100%">
            <el-option label="文学" value="文学" />
            <el-option label="科技" value="科技" />
            <el-option label="历史" value="历史" />
            <el-option label="哲学" value="哲学" />
            <el-option label="艺术" value="艺术" />
            <el-option label="教育" value="教育" />
            <el-option label="经济" value="经济" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
        <el-form-item label="馆藏位置" prop="location">
          <el-input v-model="form.location" placeholder="例如：A区-3排-5号" />
        </el-form-item>
        <el-form-item label="简介" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="价格" prop="price">
          <el-input-number v-model="form.price" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item label="库存" prop="stock">
          <el-input-number v-model="form.stock" :min="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getBooks, searchBooks, createBook, updateBook, deleteBook } from '@/api/book'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const tableData = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const formRef = ref(null)
const isSearchMode = ref(false)
let searchTimer = null

const searchForm = reactive({
  title: '',
  author: '',
  isbn: '',
  publisher: '',
  category: ''
})

const form = reactive({
  id: null,
  title: '',
  author: '',
  isbn: '',
  publisher: '',
  category: '',
  description: '',
  location: '',
  price: 0,
  stock: 0
})

const isAdmin = sessionStorage.getItem('isAdmin') === 'true'

const rules = {
  title: [{ required: true, message: '请输入书名', trigger: 'blur' }],
  author: [{ required: true, message: '请输入作者', trigger: 'blur' }]
}

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getBooks({
      page: pageNum.value - 1,
      size: pageSize.value
    })
    tableData.value = res.content
    total.value = res.totalElements
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  // 防抖处理，避免频繁请求
  if (searchTimer) clearTimeout(searchTimer)
  searchTimer = setTimeout(() => {
    doSearch()
  }, 300)
}

const doSearch = async () => {
  isSearchMode.value = true
  pageNum.value = 1
  loading.value = true
  try {
    const res = await searchBooks({
      title: searchForm.title || undefined,
      author: searchForm.author || undefined,
      isbn: searchForm.isbn || undefined,
      publisher: searchForm.publisher || undefined,
      category: searchForm.category || undefined,
      page: 0,
      size: pageSize.value
    })
    tableData.value = res.content
    total.value = res.totalElements
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handlePageChange = async () => {
  if (isSearchMode.value) {
    loading.value = true
    try {
      const res = await searchBooks({
        title: searchForm.title || undefined,
        author: searchForm.author || undefined,
        isbn: searchForm.isbn || undefined,
        publisher: searchForm.publisher || undefined,
        category: searchForm.category || undefined,
        page: pageNum.value - 1,
        size: pageSize.value
      })
      tableData.value = res.content
      total.value = res.totalElements
    } catch (error) {
      console.error(error)
    } finally {
      loading.value = false
    }
  } else {
    fetchData()
  }
}

const handleReset = () => {
  searchForm.title = ''
  searchForm.author = ''
  searchForm.isbn = ''
  searchForm.publisher = ''
  searchForm.category = ''
  isSearchMode.value = false
  pageNum.value = 1
  fetchData()
}

const handleAdd = () => {
  isEdit.value = false
  Object.assign(form, { id: null, title: '', author: '', isbn: '', publisher: '', category: '', description: '', location: '', price: 0, stock: 0 })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        if (isEdit.value) {
          await updateBook(form.id, form)
          ElMessage.success('更新成功')
        } else {
          await createBook(form)
          ElMessage.success('新增成功')
        }
        dialogVisible.value = false
        fetchData()
      } catch (error) {
        console.error(error)
      } finally {
        submitLoading.value = false
      }
    }
  })
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该图书吗？', '提示', {
      type: 'warning'
    })
    await deleteBook(row.id)
    ElMessage.success('删除成功')
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
.book-container {
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

.search-form {
  margin-bottom: 20px;
}
</style>
