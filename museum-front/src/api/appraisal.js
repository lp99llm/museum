import request, { unwrapData } from '@/utils/request'

export const appraisalApi = {
  getPage(params) {
    return request.post('/appraisal/page', params).then(unwrapData)
  },
  getDetail(id) {
    return request.get(`/appraisal/${id}`).then(unwrapData)
  },
  create(data) {
    return request.post('/appraisal', data).then(unwrapData)
  },
  update(data) {
    return request.put('/appraisal', data).then(unwrapData)
  },
  delete(id) {
    return request.delete(`/appraisal/${id}`).then(unwrapData)
  }
}
