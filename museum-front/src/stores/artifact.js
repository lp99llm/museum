import { defineStore } from 'pinia'
import { artifactApi } from '@/api/artifact'

export const useArtifactStore = defineStore('artifact', {
  state: () => ({
    list: [],           // 文物列表
    current: null,      // 当前查看/编辑的文物
    total: 0,           // 总数
    loading: false,
    queryParams: {      // 查询参数
      page: 1,
      pageSize: 10,
      keyword: ''
    }
  }),
  actions: {
    async fetchList() {
      this.loading = true
      try {
        const res = await artifactApi.getList(this.queryParams)
        this.list = res.records || []
        this.total = res.total || 0
      } catch (error) {
        console.error('获取文物列表失败', error)
      } finally {
        this.loading = false
      }
    },
    async fetchDetail(id) {
      const res = await artifactApi.getDetail(id)
      this.current = res
      return this.current
    },
    async createArtifact(data) {
      const res = await artifactApi.create(data)
      await this.fetchList()  // 刷新列表
      return res
    },
    async updateArtifact(id, data) {
      const res = await artifactApi.update(id, data)
      await this.fetchList()
      if (this.current?.id === id) this.current = res
      return res
    },
    async deleteArtifact(id) {
      await artifactApi.delete(id)
      await this.fetchList()
    },
    setQueryParams(params) {
      this.queryParams = { ...this.queryParams, ...params }
    }
  }
})
