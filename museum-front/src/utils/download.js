export const downloadResponseBlob = (response, fallbackName) => {
  const blob = new Blob([response.data], {
    type: response.headers['content-type'] || 'application/octet-stream'
  })
  const link = document.createElement('a')
  const url = URL.createObjectURL(blob)
  const disposition = response.headers['content-disposition'] || ''
  const matchedName = disposition.match(/filename\*?=(?:UTF-8''|")?([^";]+)/i)

  link.href = url
  link.download = matchedName ? decodeURIComponent(matchedName[1].replace(/"/g, '')) : fallbackName
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
  URL.revokeObjectURL(url)
}
