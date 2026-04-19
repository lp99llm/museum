import request from '@/utils/request'

export const disposalApi = {
  getPage(params) {
    return request.post('/disposal/page', params)
  },
  getDetail(id) {
    return request.get(`/disposal/${id}`)
  },
  create(data) {
    return request.post('/disposal', data)
  },
  update(data) {
    return request.put('/disposal', data)
  },
  delete(id) {
    return request.delete(`/disposal/${id}`)
  },
  approve(params) {
    return request.post('/disposal/approve', params)
  },
  getFlowHistory(id) {
    return request.get(`/disposal/${id}/flow`)
  }
}
