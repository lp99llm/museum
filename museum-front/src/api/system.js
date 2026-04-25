import request from '@/utils/request'

export const operationLogApi = {
  getPage(params) {
    return request.get('/operation-log/page', { params }).then((res) => res.data)
  },
  getDetail(id) {
    return request.get(`/operation-log/${id}`).then((res) => res.data)
  },
  export(params) {
    return request.get('/operation-log/export', {
      params,
      responseType: 'blob'
    })
  }
}

export const securityApi = {
  getOverview() {
    return request.get('/security/overview').then((res) => res.data)
  },
  triggerManualBackup() {
    return request.post('/security/backup/manual').then((res) => res.data)
  },
  triggerRestore(data) {
    return request.post('/security/backup/restore', data).then((res) => res.data)
  },
  getSecurityReportEntry() {
    return request.get('/security/report').then((res) => res.data)
  }
}
