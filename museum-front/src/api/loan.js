import request from '@/utils/request'

export const loanApi = {
  getPage(params) {
    return request.post('/loan/page', params).then((res) => res.data)
  },
  getDetail(id) {
    return request.get(`/loan/${id}`).then((res) => res.data)
  },
  create(data) {
    return request.post('/loan', data).then((res) => res.data)
  },
  update(data) {
    return request.put('/loan', data).then((res) => res.data)
  },
  delete(id) {
    return request.delete(`/loan/${id}`).then((res) => res.data)
  },
  returnLoan(params) {
    return request.post('/loan/return', params).then((res) => res.data)
  },
  getCurrentByArtifact(artifactId) {
    return request.get(`/loan/artifact/${artifactId}/current`).then((res) => res.data)
  },
  approve(params) {
    return request.post('/loan/approve', params).then((res) => res.data)
  },
  getFlowHistory(id) {
    return request.get(`/loan/${id}/flow`).then((res) => res.data)
  }
}
