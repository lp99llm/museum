import request from '@/utils/request'

export const statisticsApi = {
  getDashboard() {
    return request.get('/statistics/dashboard').then(res => res.data)
  },
  getArtifactStats(params) {
    return request.post('/statistics/artifacts', params).then(res => res.data)
  },
  getExhibitionStats(params) {
    return request.post('/statistics/exhibitions', params).then(res => res.data)
  },
  getVisitorStats(params) {
    return request.post('/statistics/visitors', params).then(res => res.data)
  },
  getProcessStats(params) {
    return request.post('/statistics/process', params).then(res => res.data)
  },
  getSpaceHeatmapStats() {
    return request.get('/statistics/space-heatmap').then(res => res.data)
  },
  drillDownArtifacts(params) {
    return request.post('/statistics/drilldown/artifacts', params).then(res => res.data)
  },
  getAIAnalysis(queryType) {
    return request.get('/statistics/ai/analysis', { params: { queryType } }).then(res => res.data)
  }
}

export const reportApi = {
  exportArtifactExcel(params) {
    return request.post('/report/export/artifacts/excel', params, {
      responseType: 'blob'
    })
  },
  exportArtifactStatsExcel(params) {
    return request.post('/report/export/artifacts/stats/excel', params, {
      responseType: 'blob'
    })
  },
  exportExhibitionStatsExcel(params) {
    return request.post('/report/export/exhibitions/stats/excel', params, {
      responseType: 'blob'
    })
  },
  exportArtifactPdf(params) {
    return request.post('/report/export/artifacts/pdf', params, {
      responseType: 'blob'
    })
  }
}
