<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="flex-between">
          <span style="font-weight: 600">我的申请</span>
          <el-button type="primary" size="small" @click="$router.push('/ak/apply')">
            新建申请
          </el-button>
        </div>
      </template>

      <el-table :data="applications" stripe v-loading="loading" style="width: 100%">
        <el-table-column prop="applicationId" label="申请编号" width="180" />
        <el-table-column prop="purpose" label="用途" width="120" />
        <el-table-column prop="providerId" label="厂商" width="120" />
        <el-table-column prop="model" label="模型" width="160" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)" size="small">{{ statusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="申请时间" width="180">
          <template #default="{ row }">{{ formatDate(row.createdAt) }}</template>
        </el-table-column>
        <el-table-column prop="comment" label="审批意见" min-width="160">
          <template #default="{ row }">{{ row.comment || '-' }}</template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { getMyApplications } from '@/api/ak'
import { formatDate } from '@/utils/format'
import type { AKApplication } from '@/types/ak'

const applications = ref<AKApplication[]>([])
const loading = ref(false)
let timer: ReturnType<typeof setInterval> | null = null

function statusType(status: string): 'success' | 'warning' | 'danger' | 'info' {
  const map: Record<string, 'success' | 'warning' | 'danger' | 'info'> = { pending: 'warning', approved: 'success', rejected: 'danger' }
  return map[status] || 'info'
}

function statusLabel(status: string): string {
  const map: Record<string, string> = { pending: '待审批', approved: '已通过', rejected: '已拒绝' }
  return map[status] || status
}

async function loadData(): Promise<void> {
  loading.value = true
  try {
    applications.value = await getMyApplications()
  } catch {
    // handled in interceptor
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadData()
  timer = setInterval(loadData, 30000)
})

onUnmounted(() => {
  if (timer) clearInterval(timer)
})
</script>
