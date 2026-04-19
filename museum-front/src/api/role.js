import request from '@/utils/request'

export const roleApi = {
  getPage(params) {
    return request.get('/role/page', { params })
  },
  getDetail(id) {
    return request.get(`/role/${id}`)
  },
  create(data) {
    return request.post('/role', data)
  },
  update(data) {
    return request.put('/role', data)
  },
  delete(id) {
    return request.delete(`/role/${id}`)
  },
  getPermissions(roleId) {
    return request.get(`/role/${roleId}/permissions`)
  },
  assignPermissions(roleId, permissionIds) {
    return request.post(`/role/${roleId}/permissions`, permissionIds)
  }
}

export const permissionApi = {
  getTree() {
    return request.get('/permission/tree')
  },
  getList() {
    return request.get('/permission/list')
  },
  getDetail(id) {
    return request.get(`/permission/${id}`)
  },
  create(data) {
    return request.post('/permission', data)
  },
  update(data) {
    return request.put('/permission', data)
  },
  delete(id) {
    return request.delete(`/permission/${id}`)
  },
  getPermissionsWithFlag(roleId) {
    return request.get(`/permission/role/${roleId}`)
  }
}
