import request from '@/utils/request'

export const statisticsApi = {
  getDashboard() {
    return request.get('/statistics/dashboard')
  },
  getArtifactStats(params) {
    return request.post('/statistics/artifacts', params)
  },
  getExhibitionStats(params) {
    return request.post('/statistics/exhibitions', params)
  },
  getVisitorStats(params) {
    return request.post('/statistics/visitors', params)
  },
  drillDownArtifacts(params) {
    return request.post('/statistics/drilldown/artifacts', params)
  },
  getAIAnalysis(queryType) {
    return request.get('/statistics/ai/analysis', { params: { queryType } })
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
