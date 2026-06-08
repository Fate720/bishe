<template>
  <div class="book-detail">
    <el-page-header @back="$router.back()" title="返回图书列表">
      <template #content>
        <span class="page-title">图书详情</span>
      </template>
    </el-page-header>

    <el-card style="margin-top: 20px" v-loading="loading">
      <el-descriptions title="图书信息" :column="2" border>
        <el-descriptions-item label="图书ID">{{ book.id }}</el-descriptions-item>
        <el-descriptions-item label="书名">{{ book.title }}</el-descriptions-item>
        <el-descriptions-item label="作者">{{ book.author }}</el-descriptions-item>
        <el-descriptions-item label="ISBN">{{ book.isbn || '暂无' }}</el-descriptions-item>
        <el-descriptions-item label="出版社">{{ book.publisher || '暂无' }}</el-descriptions-item>
        <el-descriptions-item label="价格">¥{{ book.price }}</el-descriptions-item>
        <el-descriptions-item label="库存">{{ book.stock }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ book.createdTime }}</el-descriptions-item>
      </el-descriptions>

      <div style="margin-top: 20px; text-align: center">
        <el-button type="primary" @click="$router.push('/book')">返回列表</el-button>
        <el-button @click="handleEdit">编辑图书</el-button>
        <el-button type="success" @click="handleBorrow">借阅图书</el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getBook } from '@/api/book'
import { borrowBook } from '@/api/borrow'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const book = ref({})

const handleEdit = () => {
  router.push(`/book/edit/${book.value.id}`)
}

const handleBorrow = async () => {
  try {
    await borrowBook(book.value.id)
    ElMessage.success('借阅成功')
    fetchData()
  } catch (error) {
    console.error(error)
  }
}

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getBook(route.params.id)
    book.value = res
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.book-detail {
  padding: 20px;
}

.page-title {
  font-size: 18px;
  font-weight: bold;
}
</style>
