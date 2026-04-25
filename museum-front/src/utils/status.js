const normalizeKey = (value) => String(value || '').trim().toUpperCase()

const createAliasNormalizer = (aliases) => (value) => {
  const raw = String(value || '').trim()
  if (!raw) return ''
  return aliases[normalizeKey(raw)] || raw
}

export const ARTIFACT_CURRENT_STATE_LABELS = {
  IN_STORAGE: '在库',
  IN_EXHIBITION: '展出中',
  IN_RESTORATION: '修复中',
  ON_LOAN: '外借中',
  OUTBOUND: '出库中'
}

export const ARTIFACT_CURRENT_STATE_TYPES = {
  IN_STORAGE: 'success',
  IN_EXHIBITION: 'info',
  IN_RESTORATION: 'warning',
  ON_LOAN: 'primary',
  OUTBOUND: 'danger'
}

export const PRESERVATION_STATUS_LABELS = {
  完好: '完好',
  轻微损伤: '轻微损伤',
  待修复: '待修复',
  修复中: '修复中',
  未知: '未知'
}

export const PRESERVATION_STATUS_TYPES = {
  完好: 'success',
  轻微损伤: 'warning',
  待修复: 'danger',
  修复中: 'warning',
  未知: 'info'
}

export const APPRAISAL_LEVEL_LABELS = {
  一级: '一级',
  二级: '二级',
  三级: '三级',
  四级: '四级',
  一般文物: '一般文物'
}

export const APPRAISAL_LEVEL_TYPES = {
  一级: 'danger',
  二级: 'warning',
  三级: 'success',
  四级: 'info',
  一般文物: 'info'
}

export const EXHIBITION_STATUS_LABELS = {
  PLANNING: '筹划中',
  ONGOING: '进行中',
  FINISHED: '已结束'
}

export const EXHIBITION_STATUS_TYPES = {
  PLANNING: 'warning',
  ONGOING: 'success',
  FINISHED: 'info'
}

const artifactStateAliases = {
  ACQUIRED: 'IN_STORAGE',
  IN_STORAGE: 'IN_STORAGE',
  ON_EXHIBITION: 'IN_EXHIBITION',
  IN_EXHIBITION: 'IN_EXHIBITION',
  RESTORATION: 'IN_RESTORATION',
  IN_RESTORATION: 'IN_RESTORATION',
  ON_LOAN: 'ON_LOAN',
  OUTBOUND: 'OUTBOUND'
}

const preservationStatusAliases = {
  良好: '完好',
  稳定: '完好',
  完好: '完好',
  轻微锈蚀: '轻微损伤',
  局部脱彩: '轻微损伤',
  轻微损伤: '轻微损伤',
  漆面开裂: '待修复',
  脆化: '待修复',
  待修复: '待修复',
  修复中: '修复中',
  '??': '未知',
  未知: '未知'
}

const appraisalLevelAliases = {
  LEVEL1: '一级',
  LEVEL_1: '一级',
  一级文物: '一级',
  一级: '一级',
  LEVEL2: '二级',
  LEVEL_2: '二级',
  二级文物: '二级',
  二级: '二级',
  LEVEL3: '三级',
  LEVEL_3: '三级',
  三级文物: '三级',
  三级: '三级',
  LEVEL4: '四级',
  LEVEL_4: '四级',
  四级文物: '四级',
  四级: '四级',
  一般文物: '一般文物',
  '??': ''
}

const exhibitionStatusAliases = {
  OPEN: 'ONGOING',
  ONGOING: 'ONGOING',
  PLANNING: 'PLANNING',
  FINISHED: 'FINISHED'
}

export const normalizeArtifactState = createAliasNormalizer(artifactStateAliases)
export const normalizePreservationStatus = createAliasNormalizer(preservationStatusAliases)
export const normalizeAppraisalLevel = createAliasNormalizer(appraisalLevelAliases)
export const normalizeExhibitionStatus = createAliasNormalizer(exhibitionStatusAliases)

export const getStatusLabel = (map, status, normalizer = (value) => value) => {
  const normalized = normalizer(status)
  return map[normalized] || normalized || '-'
}

export const getStatusType = (map, status, normalizer = (value) => value) => {
  const normalized = normalizer(status)
  return map[normalized] || ''
}

export const getArtifactStateLabel = (status) =>
  getStatusLabel(ARTIFACT_CURRENT_STATE_LABELS, status, normalizeArtifactState)

export const getArtifactStateType = (status) =>
  getStatusType(ARTIFACT_CURRENT_STATE_TYPES, status, normalizeArtifactState)

export const getPreservationStatusLabel = (status) =>
  getStatusLabel(PRESERVATION_STATUS_LABELS, status, normalizePreservationStatus)

export const getPreservationStatusType = (status) =>
  getStatusType(PRESERVATION_STATUS_TYPES, status, normalizePreservationStatus)

export const getAppraisalLevelLabel = (level) =>
  getStatusLabel(APPRAISAL_LEVEL_LABELS, level, normalizeAppraisalLevel)

export const getAppraisalLevelType = (level) =>
  getStatusType(APPRAISAL_LEVEL_TYPES, level, normalizeAppraisalLevel)

export const getExhibitionStatusLabel = (status) =>
  getStatusLabel(EXHIBITION_STATUS_LABELS, status, normalizeExhibitionStatus)

export const getExhibitionStatusType = (status) =>
  getStatusType(EXHIBITION_STATUS_TYPES, status, normalizeExhibitionStatus)

export const VISITOR_APPOINTMENT_STATUS_LABELS = {
  PENDING: '待审核',
  APPROVED: '已通过',
  REJECTED: '已拒绝',
  CANCELLED: '已取消'
}

export const VISITOR_APPOINTMENT_STATUS_TYPES = {
  PENDING: 'warning',
  APPROVED: 'success',
  REJECTED: 'danger',
  CANCELLED: 'info'
}

export const WAREHOUSING_STATUS_LABELS = {
  PENDING: '待确认',
  CONFIRMED: '已确认',
  REJECTED: '已拒绝'
}

export const WAREHOUSING_STATUS_TYPES = {
  PENDING: 'warning',
  CONFIRMED: 'success',
  REJECTED: 'danger'
}

export const STORAGE_CHECK_TYPE_LABELS = {
  DAILY: '日常',
  MONTHLY: '月度',
  QUARTERLY: '季度',
  HALF_YEAR: '半年度',
  YEARLY: '年度'
}

export const STORAGE_CHECK_TYPE_TYPES = {
  DAILY: 'info',
  MONTHLY: '',
  QUARTERLY: 'success',
  HALF_YEAR: 'warning',
  YEARLY: 'danger'
}

export const STORAGE_CHECK_RESULT_LABELS = {
  NORMAL: '正常',
  ABNORMAL: '异常',
  NEED_REPAIR: '需修复'
}

export const STORAGE_CHECK_RESULT_TYPES = {
  NORMAL: 'success',
  ABNORMAL: 'danger',
  NEED_REPAIR: 'warning'
}

export const DISPOSAL_TYPE_LABELS = {
  TRANSFER: '移交',
  DESTROY: '销毁',
  REMOVE: '退藏',
  OTHER: '其他'
}

export const DISPOSAL_STAGE_LABELS = {
  APPLY: '申请中',
  EVALUATION: '专家评估',
  PUBLIC: '公示',
  RECORD: '备案',
  EXECUTION: '执行',
  ARCHIVE: '归档',
  COMPLETED: '完成'
}

export const DISPOSAL_STAGE_TYPES = {
  APPLY: 'info',
  EVALUATION: 'warning',
  PUBLIC: 'primary',
  RECORD: 'success',
  EXECUTION: 'danger',
  ARCHIVE: 'success',
  COMPLETED: 'success'
}

export const DISPOSAL_STATUS_LABELS = {
  PENDING: '待审批',
  APPROVED: '已批准',
  REJECTED: '已驳回'
}

export const DISPOSAL_STATUS_TYPES = {
  PENDING: 'warning',
  APPROVED: 'success',
  REJECTED: 'danger'
}

export const LOAN_STAGE_LABELS = {
  APPLY: '申请中',
  AGREEMENT: '协议签订',
  LOANED: '已借出',
  RETURNED: '已归还'
}

export const LOAN_STAGE_TYPES = {
  APPLY: 'info',
  AGREEMENT: 'warning',
  LOANED: 'danger',
  RETURNED: 'success'
}

export const LOAN_STATUS_LABELS = {
  PENDING: '待审批',
  LOANED: '已借出',
  RETURNED: '已归还',
  REJECTED: '已驳回'
}

export const LOAN_STATUS_TYPES = {
  PENDING: 'warning',
  LOANED: 'danger',
  RETURNED: 'success',
  REJECTED: 'danger'
}

export const OUTBOUND_STAGE_LABELS = {
  STAGE1: '部门审批',
  STAGE2: '分管审批',
  STAGE3: '馆长审批',
  APPROVED: '已通过',
  RETURNED: '已归还'
}

export const OUTBOUND_STAGE_TYPES = {
  STAGE1: '',
  STAGE2: 'warning',
  STAGE3: 'danger',
  APPROVED: 'success',
  RETURNED: 'info'
}

export const OUTBOUND_STATUS_LABELS = {
  PENDING: '待审批',
  APPROVED: '已通过',
  REJECTED: '已驳回',
  OUTBOUND: '已出库',
  RETURNED: '已归还'
}

export const OUTBOUND_STATUS_TYPES = {
  PENDING: 'warning',
  APPROVED: 'success',
  REJECTED: 'danger',
  OUTBOUND: '',
  RETURNED: 'info'
}

export const RESTORATION_TYPE_LABELS = {
  CLEANING: '除尘清理',
  REINFORCEMENT: '加固',
  RESTORATION: '补配',
  AGING: '做旧',
  PROTECTION: '防护处理',
  OTHER: '其他'
}

export const RESTORATION_STAGE_LABELS = {
  APPLY: '申请中',
  PLAN: '方案编制',
  EXECUTE: '修复执行',
  ACCEPTANCE: '验收中',
  COMPLETED: '已完成'
}

export const RESTORATION_STAGE_TYPES = {
  APPLY: 'info',
  PLAN: '',
  EXECUTE: 'warning',
  ACCEPTANCE: 'danger',
  COMPLETED: 'success'
}

export const RESTORATION_STATUS_LABELS = {
  PENDING: '待审批',
  COMPLETED: '已完成',
  REJECTED: '已驳回'
}

export const RESTORATION_STATUS_TYPES = {
  PENDING: 'warning',
  COMPLETED: 'success',
  REJECTED: 'danger'
}
