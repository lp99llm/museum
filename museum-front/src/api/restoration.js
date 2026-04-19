import request from '@/utils/request'

export const restorationApi = {
  getPage(params) {
    return request.post('/restoration/page', params)
  },
  getDetail(id) {
    return request.get(`/restoration/${id}`)
  },
  create(data) {
    return request.post('/restoration', data)
  },
  update(data) {
    return request.put('/restoration', data)
  },
  delete(id) {
    return request.delete(`/restoration/${id}`)
  },
  approve(params) {
    return request.post('/restoration/approve', params)
  },
  getFlowHistory(id) {
    return request.get(`/restoration/${id}/flow`)
  }
}
