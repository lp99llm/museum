import request from '@/utils/request'

export const exhibitionEvaluationApi = {
  getList(params) {
    return request.post('/exhibition-evaluation/page', params)
  },
  getDetail(id) {
    return request.get(`/exhibition-evaluation/${id}`)
  },
  create(data) {
    return request.post('/exhibition-evaluation', data)
  },
  update(data) {
    return request.put('/exhibition-evaluation', data)
  },
  delete(id) {
    return request.delete(`/exhibition-evaluation/${id}`)
  }
}
