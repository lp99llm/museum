import request from '@/utils/request'

export const userApi = {
  getPage(params) {
    return request.get('/user/page', { params }).then(res => res.data)
  },
  getDetail(id) {
    return request.get(`/user/${id}`).then(res => res.data)
  },
  create(data) {
    return request.post('/user', data).then(res => res.data)
  },
  update(data) {
    return request.put('/user', data).then(res => res.data)
  },
  delete(id) {
    return request.delete(`/user/${id}`).then(res => res.data)
  },
  getUserRoles(userId) {
    return request.get(`/user/${userId}/roles`).then(res => res.data)
  },
  assignRoles(userId, roleIds) {
    return request.post(`/user/${userId}/roles`, roleIds).then(res => res.data)
  },
  resetPassword(id, data) {
    return request.post(`/user/${id}/reset-password`, data).then(res => res.data)
  },
  export(params) {
    return request.get('/user/export', {
      params,
      responseType: 'blob'
    })
  },
  getLogs(id) {
    return request.get(`/user/${id}/logs`).then(res => res.data)
  }
}
