import request from '@/utils/request'

export function getRolePage(params) {
  return request({
    url: '/role/page',
    method: 'get',
    params
  })
}

export function getRole(id) {
  return request({
    url: `/role/${id}`,
    method: 'get'
  })
}

export function addRole(data) {
  return request({
    url: '/role',
    method: 'post',
    data
  })
}

export function updateRole(data) {
  return request({
    url: '/role',
    method: 'put',
    data
  })
}

export function deleteRole(id) {
  return request({
    url: `/role/${id}`,
    method: 'delete'
  })
}

export function getRolePermissions(roleId) {
  return request({
    url: `/role/${roleId}/permissions`,
    method: 'get'
  })
}

export function assignPermissions(roleId, permissionIds) {
  return request({
    url: `/role/${roleId}/permissions`,
    method: 'post',
    data: permissionIds
  })
}