import request from '@/utils/request'

// 分页查询借阅记录（管理员查看所有）
export function getMyBorrows(params) {
  return request.get('/borrows/my', { params })
}

// 分页查询所有借阅记录（管理员）
export function getAllBorrows(params) {
  return request.get('/borrows', { params })
}

// 借阅图书
export function borrowBook(bookId) {
  return request.post(`/borrows/${bookId}`)
}

// 归还图书
export function returnBook(recordId) {
  return request.put(`/borrows/return/${recordId}`)
}
