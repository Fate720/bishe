import request from '@/utils/request'

// 分页查询角色
export function getRoles(params) {
  return request({
    url: '/roles',
    method: 'get',
    params
  })
}

// 查询所有角色
export function getAllRoles() {
  return request({
    url: '/roles/all',
    method: 'get'
  })
}

// 获取角色详情
export function getRole(id) {
  return request({
    url: `/roles/${id}`,
    method: 'get'
  })
}

// 创建角色
export function createRole(data) {
  return request({
    url: '/roles',
    method: 'post',
    data
  })
}

// 更新角色
export function updateRole(id, data) {
  return request({
    url: `/roles/${id}`,
    method: 'put',
    data
  })
}

// 删除角色
export function deleteRole(id) {
  return request({
    url: `/roles/${id}`,
    method: 'delete'
  })
}
