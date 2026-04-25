import request from '@/utils/request'

export const artifactApi = {
  getList(params) {
    return request.post('/artifact/page', params).then(res => res.data)
  },
  getDetail(id) {
    return request.get(`/artifact/${id}`).then(res => res.data)
  },
  create(data) {
    return request.post('/artifact', data).then(res => res.data)
  },
  update(data) {
    return request.put('/artifact', data).then(res => res.data)
  },
  delete(id) {
    return request.delete(`/artifact/${id}`).then(res => res.data)
  },
  getByCode(code) {
    return request.get(`/artifact/code/${code}`).then(res => res.data)
  },
  updateState(id, targetState, remark) {
    return request.put(`/artifact/state/${id}`, null, {
      params: { targetState, remark }
    }).then(res => res.data)
  },
  importArtifacts(data) {
    return request.post('/artifact/import', data).then(res => res.data)
  },
  export(data) {
    return request.post('/artifact/export', data, {
      responseType: 'blob'
    })
  },
  exportBatch(data) {
    return request.post('/artifact/export/batch', data, {
      responseType: 'blob'
    })
  },
  exportLabel(id) {
    return request.get(`/artifact/${id}/label`, {
      responseType: 'blob'
    })
  },
  exportReport(id) {
    return request.get(`/artifact/${id}/report`, {
      responseType: 'blob'
    })
  },
  joinExhibition(id, data) {
    return request.post(`/artifact/${id}/join-exhibition`, data).then(res => res.data)
  },
  getRelations(id) {
    return request.get(`/artifact/${id}/relations`).then(res => res.data)
  }
}
