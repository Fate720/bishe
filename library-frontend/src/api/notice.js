import request from '@/utils/request'

// 获取通知列表
export function getNotices(params) {
  return request.get('/notices', { params })
}

// 创建通知
export function createNotice(data) {
  return request.post('/notices', data)
}

// 更新通知
export function updateNotice(id, data) {
  return request.put(`/notices/${id}`, data)
}

// 删除通知
export function deleteNotice(id) {
  return request.delete(`/notices/${id}`)
}

// 发布通知
export function publishNotice(id) {
  return request.post(`/notices/${id}/publish`)
}
