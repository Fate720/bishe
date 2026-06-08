import request from '@/utils/request'

// 鐧诲綍
export function login(data) {
  return request.post('/auth/login', data)
}

// 娉ㄥ唽
export function register(data) {
  return request.post('/auth/register', data)
}

// 鑾峰彇褰撳墠鐢ㄦ埛淇℃伅
export function getCurrentUser() {
  return request.get('/auth/me')
}