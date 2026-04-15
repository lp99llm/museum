import request from '@/utils/request'

export const artifactApi = {
  getList(params) {
    return request.get('/artifacts', { params })
  },
  getDetail(id) {
    return request.get(`/artifacts/${id}`)
  },
  create(data) {
    return request.post('/artifacts', data)
  },
  update(id, data) {
    return request.put(`/artifacts/${id}`, data)
  },
  delete(id) {
    return request.delete(`/artifacts/${id}`)
  }
}