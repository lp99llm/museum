import request from '@/utils/request'

export const roleApi = {
  getPage(params) {
    return request.get('/role/page', { params }).then(res => res.data)
  },
  getDetail(id) {
    return request.get(`/role/${id}`).then(res => res.data)
  },
  create(data) {
    return request.post('/role', data).then(res => res.data)
  },
  update(data) {
    return request.put('/role', data).then(res => res.data)
  },
  delete(id) {
    return request.delete(`/role/${id}`).then(res => res.data)
  },
  getPermissions(roleId) {
    return request.get(`/role/${roleId}/permissions`).then(res => res.data)
  },
  assignPermissions(roleId, permissionIds) {
    return request.post(`/role/${roleId}/permissions`, permissionIds).then(res => res.data)
  }
}

export const permissionApi = {
  getTree() {
    return request.get('/permission/tree').then(res => res.data)
  },
  getList() {
    return request.get('/permission/list').then(res => res.data)
  },
  getDetail(id) {
    return request.get(`/permission/${id}`).then(res => res.data)
  },
  create(data) {
    return request.post('/permission', data).then(res => res.data)
  },
  update(data) {
    return request.put('/permission', data).then(res => res.data)
  },
  delete(id) {
    return request.delete(`/permission/${id}`).then(res => res.data)
  },
  getPermissionsWithFlag(roleId) {
    return request.get(`/permission/role/${roleId}`).then(res => res.data)
  }
}
