import request from '@/utils/request'

export const visitorAppointmentApi = {
  getList(params) {
    return request.post('/visitor-appointment/page', params)
  },
  getDetail(id) {
    return request.get(`/visitor-appointment/${id}`)
  },
  create(data) {
    return request.post('/visitor-appointment', data)
  },
  update(data) {
    return request.put('/visitor-appointment', data)
  },
  delete(id) {
    return request.delete(`/visitor-appointment/${id}`)
  },
  approve(id) {
    return request.put(`/visitor-appointment/${id}/approve`)
  },
  reject(id, reason) {
    return request.put(`/visitor-appointment/${id}/reject`, reason)
  },
  cancel(id) {
    return request.put(`/visitor-appointment/${id}/cancel`)
  }
}
