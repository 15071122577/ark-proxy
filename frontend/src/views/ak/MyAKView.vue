<template>
  <div class="page-container">
    <el-card v-loading="loading">
      <template #header>
        <span style="font-weight: 600">我的 AK</span>
      </template>

      <el-empty v-if="!akInfo" description="暂未分配 AK，请先提交申请" />

      <template v-else>
        <el-descriptions :column="1" border size="large">
          <el-descriptions-item label="AK">
            <div class="ak-field">
              <code>{{ showAK ? akInfo.ak : maskAK(akInfo.ak) }}</code>
              <el-button link type="primary" @click="showAK = !showAK">
                {{ showAK ? '隐藏' : '显示' }}
              </el-button>
              <el-button link type="primary" @click="copy(akInfo.ak, 'AK 已复制')">复制</el-button>
            </div>
          </el-descriptions-item>

          <el-descriptions-item label="SK">
            <div class="ak-field">
              <code>{{ showSK ? akInfo.sk : maskSK(akInfo.sk) }}</code>
              <el-button link type="primary" @click="showSK = !showSK">
                {{ showSK ? '隐藏' : '显示' }}
              </el-button>
              <el-button link type="primary" @click="copy(akInfo.sk, 'SK 已复制')">复制</el-button>
            </div>
          </el-descriptions-item>

          <el-descriptions-item label="状态">
            <el-tag :type="akStatusType" size="small">{{ akStatusLabel }}</el-tag>
          </el-descriptions-item>
        </el-descriptions>

        <!-- 配额进度 -->
        <div style="margin-top: 24px">
          <h4 style="margin-bottom: 12px">配额使用</h4>
          <el-progress
            :percentage="quotaPercent"
            :color="quotaColor"
            :stroke-width="18"
            :text-inside="true"
            :format="() => `${formatTokens(akInfo!.quota.usedTokens)} / ${formatTokens(akInfo!.quota.totalTokens)}`"
          />
          <p style="color: #909399; font-size: 12px; margin-top: 8px">
            重置时间：{{ formatDate(akInfo!.quota.resetAt, 'YYYY-MM-DD') }}
          </p>
        </div>

        <!-- 配置引导 -->
        <div style="margin-top: 24px">
          <h4 style="margin-bottom: 12px">配置引导</h4>
          <el-alert type="info" :closable="false" show-icon>
            <pre class="config-guide">{{ akInfo.configGuide }}</pre>
          </el-alert>
        </div>
      </template>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { getMyAK } from '@/api/ak'
import { copyToClipboard } from '@/utils/clipboard'
import { maskAK, maskSK, formatDate, formatTokens } from '@/utils/format'
import type { MyAKInfo } from '@/types/ak'

const akInfo = ref<MyAKInfo | null>(null)
const loading = ref(false)
const showAK = ref(false)
const showSK = ref(false)

const quotaPercent = computed(() => {
  if (!akInfo.value) return 0
  const { totalTokens, usedTokens } = akInfo.value.quota
  return totalTokens > 0 ? Math.round((usedTokens / totalTokens) * 100) : 0
})

const quotaColor = computed(() => {
  const p = quotaPercent.value
  if (p >= 100) return '#f56c6c'
  if (p >= 90) return '#e6a23c'
  return '#409eff'
})

const akStatusType = computed(() => {
  const map: Record<string, string> = { active: 'success', quota_exhausted: 'warning', revoked: 'danger' }
  return (map[akInfo.value?.status || ''] || 'info') as any
})

const akStatusLabel = computed(() => {
  const map: Record<string, string> = { active: '正常', quota_exhausted: '配额耗尽', revoked: '已撤销' }
  return map[akInfo.value?.status || ''] || akInfo.value?.status || ''
})

async function copy(text: string, tip: string): Promise<void> {
  await copyToClipboard(text, tip)
}

async function loadData(): Promise<void> {
  loading.value = true
  try {
    akInfo.value = await getMyAK()
  } catch {
    akInfo.value = null
  } finally {
    loading.value = false
  }
}

onMounted(loadData)
</script>

<style scoped>
.ak-field {
  display: flex;
  align-items: center;
  gap: 8px;
}

.ak-field code {
  font-family: 'SFMono-Regular', Consolas, monospace;
  font-size: 13px;
  background: #f5f7fa;
  padding: 4px 8px;
  border-radius: 4px;
}

.config-guide {
  margin: 0;
  font-size: 13px;
  white-space: pre-wrap;
  word-break: break-all;
}
</style>
