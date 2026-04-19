import request from '@/utils/request'

export const loanApi = {
  getPage(params) {
    return request.post('/loan/page', params)
  },
  getDetail(id) {
    return request.get(`/loan/${id}`)
  },
  create(data) {
    return request.post('/loan', data)
  },
  update(data) {
    return request.put('/loan', data)
  },
  delete(id) {
    return request.delete(`/loan/${id}`)
  },
  returnLoan(params) {
    return request.post('/loan/return', params)
  },
  getCurrentByArtifact(artifactId) {
    return request.get(`/loan/artifact/${artifactId}/current`)
  },
  approve(params) {
    return request.post('/loan/approve', params)
  },
  getFlowHistory(id) {
    return request.get(`/loan/${id}/flow`)
  }
}
