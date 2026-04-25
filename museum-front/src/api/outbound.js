import request from '@/utils/request'

export const outboundApi = {
  getPage(params) {
    return request.post('/outbound/page', params).then((res) => res.data)
  },
  getDetail(id) {
    return request.get(`/outbound/${id}`).then((res) => res.data)
  },
  create(data) {
    return request.post('/outbound', data).then((res) => res.data)
  },
  update(data) {
    return request.put('/outbound', data).then((res) => res.data)
  },
  delete(id) {
    return request.delete(`/outbound/${id}`).then((res) => res.data)
  },
  approve(params) {
    return request.post('/outbound/approve', params).then((res) => res.data)
  },
  returnArtifact(params) {
    return request.post('/outbound/return', params).then((res) => res.data)
  },
  getFlowHistory(id) {
    return request.get(`/outbound/${id}/flow`).then((res) => res.data)
  }
}
