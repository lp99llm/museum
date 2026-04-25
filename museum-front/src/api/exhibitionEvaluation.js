import request, { unwrapData } from '@/utils/request'

export const exhibitionEvaluationApi = {
  getList(params) {
    return request.post('/exhibition-evaluation/page', params).then(unwrapData)
  },
  getDetail(id) {
    return request.get(`/exhibition-evaluation/${id}`).then(unwrapData)
  },
  create(data) {
    return request.post('/exhibition-evaluation', data).then(unwrapData)
  },
  update(data) {
    return request.put('/exhibition-evaluation', data).then(unwrapData)
  },
  delete(id) {
    return request.delete(`/exhibition-evaluation/${id}`).then(unwrapData)
  }
}
