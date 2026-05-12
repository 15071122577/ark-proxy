<template>
  <div class="stat-card" :style="{ borderLeftColor: color }">
    <div class="stat-icon" :style="{ backgroundColor: color + '1a', color: color }">
      <el-icon :size="24"><component :is="icon" /></el-icon>
    </div>
    <div class="stat-content">
      <div class="stat-value">{{ displayValue }}</div>
      <div class="stat-title">{{ title }}</div>
    </div>
    <div v-if="trend !== undefined" class="stat-trend" :class="trendClass">
      <el-icon :size="12">
        <ElIconTop v-if="trend > 0" />
        <ElIconBottom v-else-if="trend < 0" />
      </el-icon>
      <span>{{ Math.abs(trend).toFixed(1) }}%</span>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'

const props = defineProps<{
  title: string
  value: number | string
  icon: any
  color?: string
  trend?: number
  formatter?: (val: number | string) => string
}>()

const color = computed(() => props.color || '#409eff')

const displayValue = computed(() => {
  if (props.formatter) return props.formatter(props.value)
  return props.value
})

const trendClass = computed(() => ({
  'trend-up': (props.trend || 0) > 0,
  'trend-down': (props.trend || 0) < 0,
}))
</script>

<style scoped>
.stat-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
  background: #fff;
  border-radius: 8px;
  border-left: 4px solid;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.06);
  transition: box-shadow 0.3s;
}

.stat-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.stat-content {
  flex: 1;
  min-width: 0;
}

.stat-value {
  font-size: 24px;
  font-weight: 700;
  color: #303133;
  line-height: 1.2;
}

.stat-title {
  font-size: 13px;
  color: #909399;
  margin-top: 4px;
}

.stat-trend {
  display: flex;
  align-items: center;
  gap: 2px;
  font-size: 12px;
  font-weight: 600;
}

.trend-up {
  color: #f56c6c;
}

.trend-down {
  color: #67c23a;
}
</style>
