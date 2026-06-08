import request from '@/utils/request'

// 获取图书列表
export function getBooks(params) {
  return request.get('/books', { params })
}

// 搜索图书
export function searchBooks(params) {
  return request.get('/books/search', { params })
}

// 获取图书详情
export function getBook(id) {
  return request.get(`/books/${id}`)
}

// 创建图书
export function createBook(data) {
  return request.post('/books', data)
}

// 更新图书
export function updateBook(id, data) {
  return request.put(`/books/${id}`, data)
}

// 删除图书
export function deleteBook(id) {
  return request.delete(`/books/${id}`)
}
