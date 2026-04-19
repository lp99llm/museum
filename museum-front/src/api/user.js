import request from '@/utils/request'

export const userApi = {
  getPage(params) {
    return request.get('/user/page', { params })
  },
  getDetail(id) {
    return request.get(`/user/${id}`)
  },
  create(data) {
    return request.post('/user', data)
  },
  update(data) {
    return request.put('/user', data)
  },
  delete(id) {
    return request.delete(`/user/${id}`)
  },
  getUserRoles(userId) {
    return request.get(`/user/${userId}/roles`)
  },
  assignRoles(userId, roleIds) {
    return request.post(`/user/${userId}/roles`, roleIds)
  }
}
