import request from '@/utils/request'

export const outboundApi = {
  getPage(params) {
    return request.post('/outbound/page', params)
  },
  getDetail(id) {
    return request.get(`/outbound/${id}`)
  },
  create(data) {
    return request.post('/outbound', data)
  },
  update(data) {
    return request.put('/outbound', data)
  },
  delete(id) {
    return request.delete(`/outbound/${id}`)
  },
  approve(params) {
    return request.post('/outbound/approve', params)
  },
  returnArtifact(params) {
    return request.post('/outbound/return', params)
  },
  getFlowHistory(id) {
    return request.get(`/outbound/${id}/flow`)
  }
}
