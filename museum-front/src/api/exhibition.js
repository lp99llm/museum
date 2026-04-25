import request from '@/utils/request'

export const exhibitionApi = {
  getList(params) {
    return request.post('/exhibition/page', params).then((res) => res.data)
  },
  getDetail(id) {
    return request.get(`/exhibition/${id}`).then((res) => res.data)
  },
  create(data) {
    return request.post('/exhibition', data).then((res) => res.data)
  },
  update(data) {
    return request.put('/exhibition', data).then((res) => res.data)
  },
  delete(id) {
    return request.delete(`/exhibition/${id}`).then((res) => res.data)
  },
  addArtifact(exhibitionId, artifactId, displayOrder, remark) {
    return request.post(`/exhibition/${exhibitionId}/artifact/${artifactId}`, null, {
      params: { displayOrder, remark }
    }).then((res) => res.data)
  },
  removeArtifact(exhibitionId, artifactId) {
    return request.delete(`/exhibition/${exhibitionId}/artifact/${artifactId}`).then((res) => res.data)
  },
  getArtifactIds(exhibitionId) {
    return request.get(`/exhibition/${exhibitionId}/artifacts`).then((res) => res.data)
  },
  getArtifactDetails(exhibitionId) {
    return request.get(`/exhibition/${exhibitionId}/artifact-details`).then((res) => res.data)
  },
  exportArtifacts(id) {
    return request.get(`/exhibition/${id}/artifacts/export`, {
      responseType: 'blob'
    })
  },
  getEvaluationReport(id) {
    return request.get(`/exhibition/${id}/evaluation-report`, {
      responseType: 'blob'
    })
  },
  uploadMaterial(id, data) {
    return request.post(`/exhibition/${id}/materials`, data).then((res) => res.data)
  },
  uploadMaterialFile(id, formData) {
    return request.post(`/exhibition/${id}/materials/file`, formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    }).then((res) => res.data)
  },
  getMaterials(id) {
    return request.get(`/exhibition/${id}/materials`).then((res) => res.data)
  },
  deleteMaterial(id, materialId) {
    return request.delete(`/exhibition/${id}/materials/${materialId}`).then((res) => res.data)
  },
  downloadMaterial(id, materialId) {
    return request.get(`/exhibition/${id}/materials/${materialId}/download`, {
      responseType: 'blob'
    })
  },
  getVisitorStats(id) {
    return request.get(`/exhibition/${id}/visitor-stats`).then((res) => res.data)
  }
}
