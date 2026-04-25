import { ref } from 'vue'

export function useFlowDialogs({ loadHistory, initApprove, initReturn } = {}) {
  const currentRow = ref(null)
  const flowHistory = ref([])
  const detailVisible = ref(false)
  const approveVisible = ref(false)
  const returnVisible = ref(false)

  const openDetail = async (row) => {
    currentRow.value = row
    if (loadHistory) {
      try {
        flowHistory.value = (await loadHistory(row)) || []
      } catch {
        flowHistory.value = []
      }
    }
    detailVisible.value = true
  }

  const openApprove = (row) => {
    currentRow.value = row
    initApprove?.(row)
    approveVisible.value = true
  }

  const openReturn = (row) => {
    currentRow.value = row
    initReturn?.(row)
    returnVisible.value = true
  }

  const closeDetail = () => {
    detailVisible.value = false
  }

  const closeApprove = () => {
    approveVisible.value = false
  }

  const closeReturn = () => {
    returnVisible.value = false
  }

  return {
    currentRow,
    flowHistory,
    detailVisible,
    approveVisible,
    returnVisible,
    openDetail,
    openApprove,
    openReturn,
    closeDetail,
    closeApprove,
    closeReturn
  }
}
