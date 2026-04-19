import request from '@/utils/request'

export const warehousingApi = {
  getPage(params) {
    return request.post('/warehousing/page', params)
  },
  getDetail(id) {
    return request.get(`/warehousing/${id}`)
  },
  create(data) {
    return request.post('/warehousing', data)
  },
  update(data) {
    return request.put('/warehousing', data)
  },
  delete(id) {
    return request.delete(`/warehousing/${id}`)
  }
}
