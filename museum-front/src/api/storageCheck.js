import request from '@/utils/request'

export const storageCheckApi = {
  getPage(params) {
    return request.post('/storage-check/page', params)
  },
  getDetail(id) {
    return request.get(`/storage-check/${id}`)
  },
  create(data) {
    return request.post('/storage-check', data)
  },
  update(data) {
    return request.put('/storage-check', data)
  },
  delete(id) {
    return request.delete(`/storage-check/${id}`)
  }
}
