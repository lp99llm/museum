import request, { unwrapData } from '@/utils/request'

export const acquisitionApi = {
  getPage(params) {
    return request.post('/acquisition/page', params).then(unwrapData)
  },
  getDetail(id) {
    return request.get(`/acquisition/${id}`).then(unwrapData)
  },
  create(data) {
    return request.post('/acquisition', data).then(unwrapData)
  },
  update(data) {
    return request.put('/acquisition', data).then(unwrapData)
  },
  delete(id) {
    return request.delete(`/acquisition/${id}`).then(unwrapData)
  }
}
