import request from '@/utils/request'

export const artifactApi = {
  getList(params) {
    return request.post('/artifact/page', params)
  },
  getDetail(id) {
    return request.get(`/artifact/${id}`)
  },
  create(data) {
    return request.post('/artifact', data)
  },
  update(data) {
    return request.put('/artifact', data)
  },
  delete(id) {
    return request.delete(`/artifact/${id}`)
  },
  getByCode(code) {
    return request.get(`/artifact/code/${code}`)
  },
  updateState(id, targetState, remark) {
    return request.put(`/artifact/state/${id}`, null, {
      params: { targetState, remark }
    })
  }
}