export const formatDateTime = (value) => {
  if (!value) return '-'
  return String(value).replace('T', ' ').slice(0, 19)
}

export const formatDateRange = (start, end) => `${formatDateTime(start)} - ${formatDateTime(end)}`
