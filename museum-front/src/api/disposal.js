import request from '@/utils/request'

export const disposalApi = {
  getPage(params) {
    return request.post('/disposal/page', params).then((res) => res.data)
  },
  getDetail(id) {
    return request.get(`/disposal/${id}`).then((res) => res.data)
  },
  create(data) {
    return request.post('/disposal', data).then((res) => res.data)
  },
  update(data) {
    return request.put('/disposal', data).then((res) => res.data)
  },
  delete(id) {
    return request.delete(`/disposal/${id}`).then((res) => res.data)
  },
  approve(params) {
    return request.post('/disposal/approve', params).then((res) => res.data)
  },
  getFlowHistory(id) {
    return request.get(`/disposal/${id}/flow`).then((res) => res.data)
  }
}
