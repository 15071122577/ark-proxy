<template>
  <div class="page-container">
    <el-row :gutter="16" class="stat-row">
      <el-col :xs="12" :sm="8" :md="4" v-for="item in statCards" :key="item.title">
        <StatCard v-bind="item" />
      </el-col>
    </el-row>

    <el-row :gutter="16">
      <el-col :span="16">
        <ChartCard title="请求趋势" :option="trendOption" height="320px" />
      </el-col>
      <el-col :span="8">
        <ChartCard title="功能分布" :option="funcDistOption" height="320px" />
      </el-col>
    </el-row>

    <el-row :gutter="16" style="margin-top: 16px">
      <el-col :span="8">
        <ChartCard title="模型分布" :option="modelDistOption" height="280px" />
      </el-col>
      <el-col :span="8">
        <ChartCard title="配额使用" :option="quotaOption" height="280px" />
      </el-col>
      <el-col :span="8">
        <ChartCard title="成功率" :option="successRateOption" height="280px" />
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import StatCard from '@/components/common/StatCard.vue'
import ChartCard from '@/components/common/ChartCard.vue'
import {
  getPersonalSummary,
  getPersonalTrend,
  getPersonalDistribution,
  getPersonalQuotaUsage,
} from '@/api/dashboard'
import { formatNumber, formatCurrency, formatPercent, formatTokens, formatLatency } from '@/utils/format'
import type { PersonalSummary, TrendPoint, DistributionItem } from '@/types/dashboard'

const summary = ref<PersonalSummary>({
  dailyRequests: 0,
  monthlyRequests: 0,
  dailyTokens: 0,
  monthlyTokens: 0,
  dailyCost: 0,
  monthlyCost: 0,
  quotaUsageRate: 0,
  requestSuccessRate: 0,
  avgResponseTime: 0,
})

const trendData = ref<TrendPoint[]>([])
const funcDist = ref<DistributionItem[]>([])
const modelDist = ref<DistributionItem[]>([])
const quotaRate = ref(0)

const statCards = computed(() => [
  { title: '今日请求', value: summary.value.dailyRequests, icon: 'ElIconConnection', color: '#409eff', formatter: formatNumber },
  { title: '今日Token', value: summary.value.dailyTokens, icon: 'ElIconCoin', color: '#67c23a', formatter: formatTokens },
  { title: '今日费用', value: summary.value.dailyCost, icon: 'ElIconMoney', color: '#e6a23c', formatter: (v: any) => formatCurrency(v) },
  { title: '月度请求', value: summary.value.monthlyRequests, icon: 'ElIconTrendCharts', color: '#909399', formatter: formatNumber },
  { title: '配额使用率', value: summary.value.quotaUsageRate, icon: 'ElIconPieChart', color: '#f56c6c', formatter: (v: any) => formatPercent(v) },
  { title: '平均响应', value: summary.value.avgResponseTime, icon: 'ElIconTimer', color: '#409eff', formatter: formatLatency },
])

// 趋势图
const trendOption = computed(() => ({
  tooltip: { trigger: 'axis' },
  legend: { data: ['请求数', 'Token数', '费用'] },
  grid: { left: 50, right: 20, top: 40, bottom: 30 },
  xAxis: { type: 'category', data: trendData.value.map((p) => p.timestamp.slice(5, 10)) },
  yAxis: [
    { type: 'value', name: '请求/Token' },
    { type: 'value', name: '费用(¥)', position: 'right' },
  ],
  series: [
    { name: '请求数', type: 'line', smooth: true, data: trendData.value.map((p) => p.requests), itemStyle: { color: '#409eff' } },
    { name: 'Token数', type: 'line', smooth: true, data: trendData.value.map((p) => p.tokens), itemStyle: { color: '#67c23a' } },
    { name: '费用', type: 'line', smooth: true, yAxisIndex: 1, data: trendData.value.map((p) => p.cost), itemStyle: { color: '#e6a23c' } },
  ],
}))

// 功能分布饼图
const funcDistOption = computed(() => ({
  tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
  series: [{
    type: 'pie',
    radius: ['40%', '70%'],
    data: funcDist.value.length ? funcDist.value : [{ name: '暂无数据', value: 0 }],
    emphasis: { itemStyle: { shadowBlur: 10, shadowOffsetX: 0, shadowColor: 'rgba(0,0,0,0.2)' } },
  }],
}))

// 模型分布饼图
const modelDistOption = computed(() => ({
  tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
  series: [{
    type: 'pie',
    radius: ['40%', '70%'],
    data: modelDist.value.length ? modelDist.value : [{ name: '暂无数据', value: 0 }],
  }],
}))

// 配额仪表盘
const quotaOption = computed(() => ({
  series: [{
    type: 'gauge',
    startAngle: 200,
    endAngle: -20,
    min: 0,
    max: 100,
    detail: { formatter: '{value}%', fontSize: 20, offsetCenter: [0, '70%'] },
    data: [{ value: quotaRate.value || summary.value.quotaUsageRate, name: '配额使用率' }],
    axisLine: { lineStyle: { width: 12, color: [[0.7, '#67c23a'], [0.9, '#e6a23c'], [1, '#f56c6c']] } },
    pointer: { width: 4 },
  }],
}))

// 成功率仪表盘
const successRateOption = computed(() => ({
  series: [{
    type: 'gauge',
    startAngle: 200,
    endAngle: -20,
    min: 0,
    max: 100,
    detail: { formatter: '{value}%', fontSize: 20, offsetCenter: [0, '70%'] },
    data: [{ value: summary.value.requestSuccessRate, name: '请求成功率' }],
    axisLine: { lineStyle: { width: 12, color: [[0.95, '#f56c6c'], [0.99, '#e6a23c'], [1, '#67c23a']] } },
    pointer: { width: 4 },
  }],
}))

async function loadData(): Promise<void> {
  try {
    const [s, trend, fn, mdl, q] = await Promise.allSettled([
      getPersonalSummary(),
      getPersonalTrend(7),
      getPersonalDistribution('function'),
      getPersonalDistribution('model'),
      getPersonalQuotaUsage(),
    ])
    if (s.status === 'fulfilled') summary.value = s.value
    if (trend.status === 'fulfilled') trendData.value = trend.value
    if (fn.status === 'fulfilled') funcDist.value = fn.value
    if (mdl.status === 'fulfilled') modelDist.value = mdl.value
    if (q.status === 'fulfilled') quotaRate.value = q.value
  } catch {
    // 各接口已在拦截器中处理
  }
}

onMounted(loadData)
</script>

<style scoped>
.stat-row {
  margin-bottom: 16px;
}
</style>
