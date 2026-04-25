import request from '@/utils/request'

export const restorationApi = {
  getPage(params) {
    return request.post('/restoration/page', params).then((res) => res.data)
  },
  getDetail(id) {
    return request.get(`/restoration/${id}`).then((res) => res.data)
  },
  create(data) {
    return request.post('/restoration', data).then((res) => res.data)
  },
  update(data) {
    return request.put('/restoration', data).then((res) => res.data)
  },
  delete(id) {
    return request.delete(`/restoration/${id}`).then((res) => res.data)
  },
  approve(params) {
    return request.post('/restoration/approve', params).then((res) => res.data)
  },
  getFlowHistory(id) {
    return request.get(`/restoration/${id}/flow`).then((res) => res.data)
  }
}
