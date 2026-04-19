import request from '@/utils/request'

export const acquisitionApi = {
  getPage(params) {
    return request.post('/acquisition/page', params)
  },
  getDetail(id) {
    return request.get(`/acquisition/${id}`)
  },
  create(data) {
    return request.post('/acquisition', data)
  },
  update(data) {
    return request.put('/acquisition', data)
  },
  delete(id) {
    return request.delete(`/acquisition/${id}`)
  }
}
