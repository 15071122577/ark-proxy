<template>
  <div class="page-container">
    <el-row :gutter="16" class="stat-row">
      <el-col :xs="12" :sm="8" :md="4" v-for="item in statCards" :key="item.title">
        <StatCard v-bind="item" />
      </el-col>
    </el-row>

    <el-row :gutter="16">
      <el-col :span="16">
        <ChartCard title="团队趋势" :option="trendOption" height="320px" />
      </el-col>
      <el-col :span="8">
        <ChartCard title="部门分布" :option="deptDistOption" height="320px" />
      </el-col>
    </el-row>

    <el-row :gutter="16" style="margin-top: 16px">
      <el-col :span="24">
        <ChartCard title="成员排行 (Top 10)" :option="rankingOption" height="360px" />
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import StatCard from '@/components/common/StatCard.vue'
import ChartCard from '@/components/common/ChartCard.vue'
import { getTeamSummary, getTeamTrend, getMemberRanking, getTeamDistribution } from '@/api/dashboard'
import { formatNumber, formatCurrency, formatTokens } from '@/utils/format'
import type { TeamSummary, TrendPoint, MemberRanking, DistributionItem } from '@/types/dashboard'

const summary = ref<TeamSummary>({
  dailyRequests: 0,
  dailyTokens: 0,
  dailyCost: 0,
  budgetUsageRate: 0,
  memberCount: 0,
  avgResponseTime: 0,
})
const trendData = ref<TrendPoint[]>([])
const ranking = ref<MemberRanking[]>([])
const deptDist = ref<DistributionItem[]>([])

const statCards = computed(() => [
  { title: '今日请求', value: summary.value.dailyRequests, icon: 'ElIconConnection', color: '#409eff', formatter: formatNumber },
  { title: '今日Token', value: summary.value.dailyTokens, icon: 'ElIconCoin', color: '#67c23a', formatter: formatTokens },
  { title: '今日费用', value: summary.value.dailyCost, icon: 'ElIconMoney', color: '#e6a23c', formatter: (v: any) => formatCurrency(v) },
  { title: '预算使用率', value: summary.value.budgetUsageRate, icon: 'ElIconPieChart', color: '#f56c6c', formatter: (v: any) => `${Number(v).toFixed(1)}%` },
  { title: '团队成员', value: summary.value.memberCount, icon: 'ElIconUserFilled', color: '#909399', formatter: formatNumber },
  { title: '平均响应', value: summary.value.avgResponseTime, icon: 'ElIconTimer', color: '#409eff', formatter: (v: any) => `${v}ms` },
])

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
    { name: '请求数', type: 'line', smooth: true, data: trendData.value.map((p) => p.requests) },
    { name: 'Token数', type: 'line', smooth: true, data: trendData.value.map((p) => p.tokens) },
    { name: '费用', type: 'line', smooth: true, yAxisIndex: 1, data: trendData.value.map((p) => p.cost) },
  ],
}))

const deptDistOption = computed(() => ({
  tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
  series: [{
    type: 'pie',
    radius: ['40%', '70%'],
    data: deptDist.value.length ? deptDist.value : [{ name: '暂无数据', value: 0 }],
  }],
}))

const rankingOption = computed(() => ({
  tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
  legend: { data: ['请求数', 'Token数', '费用'] },
  grid: { left: 80, right: 20, top: 40, bottom: 30 },
  xAxis: { type: 'value' },
  yAxis: { type: 'category', data: ranking.value.map((r) => r.username).reverse() },
  series: [
    { name: '请求数', type: 'bar', data: ranking.value.map((r) => r.requestCount).reverse(), itemStyle: { color: '#409eff' } },
    { name: 'Token数', type: 'bar', data: ranking.value.map((r) => r.tokenCount).reverse(), itemStyle: { color: '#67c23a' } },
    { name: '费用', type: 'bar', data: ranking.value.map((r) => r.cost).reverse(), itemStyle: { color: '#e6a23c' } },
  ],
}))

async function loadData(): Promise<void> {
  try {
    const [s, trend, rank, dept] = await Promise.allSettled([
      getTeamSummary(),
      getTeamTrend(7),
      getMemberRanking(),
      getTeamDistribution(),
    ])
    if (s.status === 'fulfilled') summary.value = s.value
    if (trend.status === 'fulfilled') trendData.value = trend.value
    if (rank.status === 'fulfilled') ranking.value = rank.value
    if (dept.status === 'fulfilled') deptDist.value = dept.value
  } catch {
    // handled in interceptor
  }
}

onMounted(loadData)
</script>

<style scoped>
.stat-row {
  margin-bottom: 16px;
}
</style>
