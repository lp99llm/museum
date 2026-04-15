import request from '@/utils/request'

export function getUserPage(params) {
  return request({
    url: '/user/page',
    method: 'get',
    params
  })
}

export function getUser(id) {
  return request({
    url: `/user/${id}`,
    method: 'get'
  })
}

export function updateUser(data) {
  return request({
    url: '/user',
    method: 'put',
    data
  })
}

export function deleteUser(id) {
  return request({
    url: `/user/${id}`,
    method: 'delete'
  })
}

export function getUserRoles(userId) {
  return request({
    url: `/user/${userId}/roles`,
    method: 'get'
  })
}

export function assignRoles(userId, roleIds) {
  return request({
    url: `/user/${userId}/roles`,
    method: 'post',
    data: roleIds
  })
}