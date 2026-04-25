import { onMounted, ref } from 'vue'

export function usePageQuery({
  queryParams,
  createInitialQueryParams,
  fetcher,
  onError,
  immediate = true
}) {
  const loading = ref(false)
  const tableData = ref([])
  const total = ref(0)
  const dateRange = ref([])

  const syncDateRange = () => {
    if (!('startDate' in queryParams) || !('endDate' in queryParams)) {
      return
    }
    if (dateRange.value && dateRange.value.length === 2) {
      queryParams.startDate = dateRange.value[0]
      queryParams.endDate = dateRange.value[1]
      return
    }
    queryParams.startDate = ''
    queryParams.endDate = ''
  }

  const loadPage = async () => {
    loading.value = true
    try {
      syncDateRange()
      const res = await fetcher(queryParams)
      tableData.value = res.records || []
      total.value = res.total || 0
      return res
    } catch (error) {
      onError?.(error)
      throw error
    } finally {
      loading.value = false
    }
  }

  const handleQuery = () => {
    queryParams.current = 1
    return loadPage()
  }

  const resetQuery = () => {
    dateRange.value = []
    Object.assign(queryParams, createInitialQueryParams())
    return loadPage()
  }

  if (immediate) {
    onMounted(() => {
      loadPage()
    })
  }

  return {
    loading,
    tableData,
    total,
    dateRange,
    loadPage,
    handleQuery,
    resetQuery
  }
}
