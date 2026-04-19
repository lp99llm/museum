import request from '@/utils/request'

export const exhibitionApi = {
  getList(params) {
    return request.post('/exhibition/page', params)
  },
  getDetail(id) {
    return request.get(`/exhibition/${id}`)
  },
  create(data) {
    return request.post('/exhibition', data)
  },
  update(data) {
    return request.put('/exhibition', data)
  },
  delete(id) {
    return request.delete(`/exhibition/${id}`)
  },
  addArtifact(exhibitionId, artifactId, displayOrder, remark) {
    return request.post(`/exhibition/${exhibitionId}/artifact/${artifactId}`, null, {
      params: { displayOrder, remark }
    })
  },
  removeArtifact(exhibitionId, artifactId) {
    return request.delete(`/exhibition/${exhibitionId}/artifact/${artifactId}`)
  },
  getArtifactIds(exhibitionId) {
    return request.get(`/exhibition/${exhibitionId}/artifacts`)
  },
  getArtifactDetails(exhibitionId) {
    return request.get(`/exhibition/${exhibitionId}/artifact-details`)
  }
}
