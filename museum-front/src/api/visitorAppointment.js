import request, { unwrapData } from '@/utils/request'

export const visitorAppointmentApi = {
  getList(params) {
    return request.post('/visitor-appointment/page', params).then(unwrapData)
  },
  getDetail(id) {
    return request.get(`/visitor-appointment/${id}`).then(unwrapData)
  },
  create(data) {
    return request.post('/visitor-appointment', data).then(unwrapData)
  },
  update(data) {
    return request.put('/visitor-appointment', data).then(unwrapData)
  },
  delete(id) {
    return request.delete(`/visitor-appointment/${id}`).then(unwrapData)
  },
  approve(id) {
    return request.put(`/visitor-appointment/${id}/approve`).then(unwrapData)
  },
  reject(id, reason) {
    return request.put(`/visitor-appointment/${id}/reject`, reason).then(unwrapData)
  },
  cancel(id) {
    return request.put(`/visitor-appointment/${id}/cancel`).then(unwrapData)
  }
}
