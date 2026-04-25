import request, { unwrapData } from '@/utils/request'

export const warehousingApi = {
  getPage(params) {
    return request.post('/warehousing/page', params).then(unwrapData)
  },
  getDetail(id) {
    return request.get(`/warehousing/${id}`).then(unwrapData)
  },
  create(data) {
    return request.post('/warehousing', data).then(unwrapData)
  },
  update(data) {
    return request.put('/warehousing', data).then(unwrapData)
  },
  delete(id) {
    return request.delete(`/warehousing/${id}`).then(unwrapData)
  }
}
