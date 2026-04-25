import request, { unwrapData } from '@/utils/request'

export const storageCheckApi = {
  getPage(params) {
    return request.post('/storage-check/page', params).then(unwrapData)
  },
  getDetail(id) {
    return request.get(`/storage-check/${id}`).then(unwrapData)
  },
  create(data) {
    return request.post('/storage-check', data).then(unwrapData)
  },
  update(data) {
    return request.put('/storage-check', data).then(unwrapData)
  },
  delete(id) {
    return request.delete(`/storage-check/${id}`).then(unwrapData)
  }
}
