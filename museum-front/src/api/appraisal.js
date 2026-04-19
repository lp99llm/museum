import request from '@/utils/request'

export const appraisalApi = {
  getPage(params) {
    return request.post('/appraisal/page', params)
  },
  getDetail(id) {
    return request.get(`/appraisal/${id}`)
  },
  create(data) {
    return request.post('/appraisal', data)
  },
  update(data) {
    return request.put('/appraisal', data)
  },
  delete(id) {
    return request.delete(`/appraisal/${id}`)
  }
}
