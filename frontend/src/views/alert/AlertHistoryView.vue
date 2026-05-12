<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="flex-between">
          <span style="font-weight: 600">预警历史</span>
          <el-button size="small" @click="loadData">刷新</el-button>
        </div>
      </template>

      <!-- 筛选栏 -->
      <div class="filter-bar">
        <el-select v-model="filters.severity" placeholder="严重级别" clearable size="small" style="width: 130px" @change="loadData">
          <el-option label="提示" value="info" />
          <el-option label="警告" value="warning" />
          <el-option label="严重" value="critical" />
          <el-option label="紧急" value="emergency" />
        </el-select>

        <el-select v-model="filters.status" placeholder="状态" clearable size="small" style="width: 120px" @change="loadData">
          <el-option label="活跃" value="active" />
          <el-option label="已恢复" value="resolved" />
        </el-select>

        <el-date-picker
          v-model="dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          size="small"
          style="width: 260px"
          @change="loadData"
        />
      </div>

      <el-table :data="historyList" stripe v-loading="loading" style="width: 100%">
        <el-table-column prop="alertId" label="预警 ID" width="180" />
        <el-table-column prop="ruleId" label="规则 ID" width="180" />
        <el-table-column prop="userId" label="用户" width="120" />
        <el-table-column prop="alertType" label="类型" width="100" />
        <el-table-column label="严重级别" width="100">
          <template #default="{ row }">
            <el-tag :type="severityType(row.severity)" size="small">{{ severityLabel(row.severity) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="阈值/实际" width="140">
          <template #default="{ row }">{{ row.thresholdValue }}% / {{ row.actualValue }}%</template>
        </el-table-column>
        <el-table-column prop="message" label="消息" min-width="200" show-overflow-tooltip />
        <el-table-column label="通知" width="80">
          <template #default="{ row }">
            <el-tag :type="notifType(row.notificationStatus)" size="small">
              {{ notifLabel(row.notificationStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 'resolved' ? 'success' : 'danger'" size="small">
              {{ row.status === 'resolved' ? '已恢复' : '活跃' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="触发时间" width="180">
          <template #default="{ row }">{{ formatDate(row.createdAt) }}</template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-bar">
        <el-pagination
          v-model:current-page="page"
          v-model:page-size="size"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next"
          @size-change="loadData"
          @current-change="loadData"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { getAlertHistory } from '@/api/alert'
import { formatDate } from '@/utils/format'
import type { AlertHistoryItem } from '@/types/alert'

const historyList = ref<AlertHistoryItem[]>([])
const loading = ref(false)
const total = ref(0)
const page = ref(1)
const size = ref(10)
const dateRange = ref<[Date, Date] | null>(null)

const filters = reactive({
  severity: '',
  status: '',
})

type TagType = 'success' | 'warning' | 'danger' | 'info'

function severityType(s: string): TagType {
  const map: Record<string, TagType> = { info: 'info', warning: 'warning', critical: 'danger', emergency: 'danger' }
  return map[s] || 'info'
}

function severityLabel(s: string): string {
  const map: Record<string, string> = { info: '提示', warning: '警告', critical: '严重', emergency: '紧急' }
  return map[s] || s
}

function notifType(s: string): TagType {
  const map: Record<string, TagType> = { pending: 'warning', sent: 'success', failed: 'danger' }
  return map[s] || 'info'
}

function notifLabel(s: string): string {
  const map: Record<string, string> = { pending: '待发送', sent: '已发送', failed: '失败' }
  return map[s] || s
}

async function loadData(): Promise<void> {
  loading.value = true
  try {
    const params: Record<string, any> = {
      page: page.value - 1,
      size: size.value,
    }
    if (filters.severity) params.severity = filters.severity
    if (filters.status) params.status = filters.status
    if (dateRange.value) {
      params.startDate = dateRange.value[0].toISOString().slice(0, 10)
      params.endDate = dateRange.value[1].toISOString().slice(0, 10)
    }

    const res = await getAlertHistory(params)
    historyList.value = res.items
    total.value = res.pagination.total
  } catch {
    // handled in interceptor
  } finally {
    loading.value = false
  }
}

onMounted(loadData)
</script>

<style scoped>
.filter-bar {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
  flex-wrap: wrap;
}

.pagination-bar {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}
</style>
