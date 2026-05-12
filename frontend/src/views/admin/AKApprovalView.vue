<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="flex-between">
          <span style="font-weight: 600">AK 审批</span>
          <el-button size="small" @click="loadData">刷新</el-button>
        </div>
      </template>

      <el-table :data="pendingList" stripe v-loading="loading" style="width: 100%">
        <el-table-column prop="applicationId" label="申请编号" width="180" />
        <el-table-column prop="applicantName" label="申请人" width="120" />
        <el-table-column prop="purpose" label="用途" width="120" />
        <el-table-column prop="providerId" label="厂商" width="120" />
        <el-table-column prop="model" label="模型" width="160" />
        <el-table-column prop="createdAt" label="申请时间" width="180">
          <template #default="{ row }">{{ formatDate(row.createdAt) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="success" size="small" @click="handleApprove(row.applicationId, 'approve')">
              通过
            </el-button>
            <el-button type="danger" size="small" @click="handleApprove(row.applicationId, 'reject')">
              拒绝
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 审批对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogAction === 'approve' ? '通过审批' : '拒绝审批'" width="480px">
      <el-form>
        <el-form-item label="审批意见">
          <el-input
            v-model="comment"
            type="textarea"
            :rows="3"
            :placeholder="dialogAction === 'approve' ? '审批意见（可选）' : '请填写拒绝理由'"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button
          :type="dialogAction === 'approve' ? 'success' : 'danger'"
          :loading="approving"
          @click="submitApproval"
        >
          确认
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getPendingApplications, approveAK } from '@/api/ak'
import { formatDate } from '@/utils/format'
import type { AKApplication } from '@/types/ak'

const pendingList = ref<AKApplication[]>([])
const loading = ref(false)
const dialogVisible = ref(false)
const dialogAction = ref<'approve' | 'reject'>('approve')
const currentApplicationId = ref('')
const comment = ref('')
const approving = ref(false)

async function loadData(): Promise<void> {
  loading.value = true
  try {
    pendingList.value = await getPendingApplications()
  } catch {
    // handled in interceptor
  } finally {
    loading.value = false
  }
}

function handleApprove(applicationId: string, action: 'approve' | 'reject'): void {
  currentApplicationId.value = applicationId
  dialogAction.value = action
  comment.value = ''
  dialogVisible.value = true
}

async function submitApproval(): Promise<void> {
  if (dialogAction.value === 'reject' && !comment.value.trim()) {
    ElMessage.warning('拒绝审批请填写理由')
    return
  }

  approving.value = true
  try {
    await approveAK({
      applicationId: currentApplicationId.value,
      action: dialogAction.value,
      comment: comment.value,
    })
    ElMessage.success(dialogAction.value === 'approve' ? '已通过' : '已拒绝')
    dialogVisible.value = false
    loadData()
  } catch {
    // handled in interceptor
  } finally {
    approving.value = false
  }
}

onMounted(loadData)
</script>
