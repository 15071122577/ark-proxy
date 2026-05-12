<template>
  <el-card class="chart-card" :body-style="{ padding: '16px' }">
    <template #header>
      <div class="chart-header">
        <span class="chart-title">{{ title }}</span>
        <slot name="actions" />
      </div>
    </template>
    <v-chart :option="option" :autoresize="true" :style="{ height: height }" />
  </el-card>
</template>

<script setup lang="ts">
import VChart from 'vue-echarts'

defineProps<{
  title: string
  option: Record<string, any>
  height?: string
}>()
</script>

<style scoped>
.chart-card {
  border-radius: 8px;
}

.chart-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.chart-title {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
}
</style>
