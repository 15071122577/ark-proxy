<template>
  <div class="page-container">
    <el-row :gutter="16" class="stat-row">
      <el-col :xs="12" :sm="8" :md="4" v-for="item in statCards" :key="item.title">
        <StatCard v-bind="item" />
      </el-col>
    </el-row>

    <el-row :gutter="16">
      <el-col :span="16">
        <ChartCard title="实时 QPS" :option="qpsOption" height="320px">
          <template #actions>
            <el-tag size="small" :type="autoRefresh ? 'success' : 'info'">
              {{ autoRefresh ? '自动刷新 5s' : '已暂停' }}
            </el-tag>
          </template>
        </ChartCard>
      </el-col>
      <el-col :span="8">
        <ChartCard title="系统可用性" :option="availOption" height="320px" />
      </el-col>
    </el-row>

    <el-row :gutter="16" style="margin-top: 16px">
      <el-col :span="24">
        <el-card>
          <template #header>
            <span style="font-weight: 600">厂商健康状态</span>
          </template>
          <el-table :data="vendorHealth" stripe style="width: 100%">
            <el-table-column prop="providerName" label="厂商" width="180" />
            <el-table-column label="状态" width="120">
              <template #default="{ row }">
                <el-tag :type="statusType(row.status)" size="small">{{ statusLabel(row.status) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="latency" label="延迟(ms)" width="120">
              <template #default="{ row }">{{ row.latency.toFixed(0) }}ms</template>
            </el-table-column>
            <el-table-column prop="errorRate" label="错误率" width="120">
              <template #default="{ row }">{{ (row.errorRate * 100).toFixed(2) }}%</template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import StatCard from '@/components/common/StatCard.vue'
import ChartCard from '@/components/common/ChartCard.vue'
import { getSystemRealtime, getVendorHealth } from '@/api/dashboard'
import { formatNumber, formatPercent, formatLatency } from '@/utils/format'
import type { SystemRealtime, VendorHealth } from '@/types/dashboard'

const realtime = ref<SystemRealtime>({
  currentQps: 0,
  systemAvailability: 0,
  errorRate: 0,
  p95ResponseTime: 0,
  p99ResponseTime: 0,
  activeAlerts: 0,
  totalKeys: 0,
  healthyKeys: 0,
})
const vendorHealth = ref<VendorHealth[]>([])
const autoRefresh = ref(true)
let timer: ReturnType<typeof setInterval> | null = null

// QPS 历史记录（最近 60 个点）
const qpsHistory = ref<number[]>([])
const qpsLabels = ref<string[]>([])

const statCards = computed(() => [
  { title: '当前 QPS', value: realtime.value.currentQps, icon: 'ElIconOdometer', color: '#409eff' },
  { title: '可用性', value: realtime.value.systemAvailability, icon: 'ElIconCircleCheck', color: '#67c23a', formatter: (v: any) => formatPercent(v) },
  { title: '错误率', value: realtime.value.errorRate, icon: 'ElIconWarning', color: '#f56c6c', formatter: (v: any) => formatPercent(v) },
  { title: 'P95 响应', value: realtime.value.p95ResponseTime, icon: 'ElIconTimer', color: '#e6a23c', formatter: formatLatency },
  { title: '活跃预警', value: realtime.value.activeAlerts, icon: 'ElIconBell', color: '#f56c6c', formatter: formatNumber },
  { title: '健康 Key', value: `${realtime.value.healthyKeys}/${realtime.value.totalKeys}`, icon: 'ElIconKey', color: '#409eff' },
])

// QPS 实时折线图
const qpsOption = computed(() => ({
  tooltip: { trigger: 'axis' },
  grid: { left: 50, right: 20, top: 20, bottom: 30 },
  xAxis: { type: 'category', data: qpsLabels.value },
  yAxis: { type: 'value', name: 'QPS' },
  series: [{
    type: 'line',
    data: qpsHistory.value,
    smooth: true,
    areaStyle: { color: 'rgba(64,158,255,0.15)' },
    itemStyle: { color: '#409eff' },
  }],
}))

// 可用性仪表盘
const availOption = computed(() => ({
  series: [{
    type: 'gauge',
    startAngle: 200,
    endAngle: -20,
    min: 0,
    max: 100,
    detail: { formatter: '{value}%', fontSize: 24, offsetCenter: [0, '70%'] },
    data: [{ value: realtime.value.systemAvailability, name: '系统可用性' }],
    axisLine: { lineStyle: { width: 14, color: [[0.95, '#f56c6c'], [0.99, '#e6a23c'], [1, '#67c23a']] } },
    pointer: { width: 5 },
  }],
}))

function statusType(status: string): 'success' | 'warning' | 'danger' | 'info' {
  const map: Record<string, 'success' | 'warning' | 'danger' | 'info'> = { healthy: 'success', degraded: 'warning', down: 'danger' }
  return map[status] || 'info'
}

function statusLabel(status: string): string {
  const map: Record<string, string> = { healthy: '正常', degraded: '降级', down: '不可用' }
  return map[status] || status
}

async function loadData(): Promise<void> {
  try {
    const [rt, vh] = await Promise.allSettled([getSystemRealtime(), getVendorHealth()])
    if (rt.status === 'fulfilled') {
      realtime.value = rt.value
      // 记录 QPS 历史
      const now = new Date()
      const label = `${String(now.getHours()).padStart(2, '0')}:${String(now.getMinutes()).padStart(2, '0')}:${String(now.getSeconds()).padStart(2, '0')}`
      qpsHistory.value.push(rt.value.currentQps)
      qpsLabels.value.push(label)
      if (qpsHistory.value.length > 60) {
        qpsHistory.value.shift()
        qpsLabels.value.shift()
      }
    }
    if (vh.status === 'fulfilled') vendorHealth.value = vh.value
  } catch {
    // handled in interceptor
  }
}

onMounted(() => {
  loadData()
  timer = setInterval(loadData, 5000)
})

onUnmounted(() => {
  if (timer) clearInterval(timer)
})
</script>

<style scoped>
.stat-row {
  margin-bottom: 16px;
}
</style>
