<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="flex-between">
          <span style="font-weight: 600">预警规则</span>
          <el-button type="primary" size="small" @click="showCreateDialog">
            新建规则
          </el-button>
        </div>
      </template>

      <el-table :data="rules" stripe v-loading="loading" style="width: 100%">
        <el-table-column prop="ruleName" label="规则名称" width="160" />
        <el-table-column prop="ruleType" label="规则类型" width="120">
          <template #default="{ row }">{{ ruleTypeLabel(row.ruleType) }}</template>
        </el-table-column>
        <el-table-column prop="targetType" label="目标类型" width="120">
          <template #default="{ row }">{{ targetTypeLabel(row.targetType) }}</template>
        </el-table-column>
        <el-table-column label="提醒阈值" width="100">
          <template #default="{ row }">{{ row.reminderThreshold }}%</template>
        </el-table-column>
        <el-table-column label="警告阈值" width="100">
          <template #default="{ row }">{{ row.warningThreshold }}%</template>
        </el-table-column>
        <el-table-column label="阻断阈值" width="100">
          <template #default="{ row }">{{ row.blockThreshold != null ? row.blockThreshold + '%' : '-' }}</template>
        </el-table-column>
        <el-table-column label="状态" width="80">
          <template #default="{ row }">
            <el-switch
              :model-value="row.enabled"
              size="small"
              @change="(val: any) => handleToggle(row.ruleId, val)"
            />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-button link type="danger" size="small" @click="handleDelete(row.ruleId)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 新建规则对话框 -->
    <el-dialog v-model="dialogVisible" title="新建预警规则" width="560px">
      <el-form ref="formRef" :model="form" :rules="formRules" label-width="100px">
        <el-form-item label="规则名称" prop="ruleName">
          <el-input v-model="form.ruleName" placeholder="例如：配额预警" />
        </el-form-item>

        <el-form-item label="规则类型" prop="ruleType">
          <el-select v-model="form.ruleType" style="width: 100%">
            <el-option label="配额预警" value="quota" />
            <el-option label="费用预警" value="cost" />
            <el-option label="错误率预警" value="error_rate" />
          </el-select>
        </el-form-item>

        <el-form-item label="目标类型" prop="targetType">
          <el-select v-model="form.targetType" style="width: 100%">
            <el-option label="全局" value="global" />
            <el-option label="用户" value="user" />
            <el-option label="团队" value="team" />
          </el-select>
        </el-form-item>

        <el-form-item v-if="form.targetType !== 'global'" label="目标 ID" prop="targetId">
          <el-input v-model="form.targetId" placeholder="用户 ID 或团队 ID" />
        </el-form-item>

        <el-form-item label="提醒阈值" prop="reminderThreshold">
          <el-input-number v-model="form.reminderThreshold" :min="0" :max="100" /> %
        </el-form-item>

        <el-form-item label="警告阈值" prop="warningThreshold">
          <el-input-number v-model="form.warningThreshold" :min="0" :max="100" /> %
        </el-form-item>

        <el-form-item label="阻断阈值">
          <el-input-number v-model="form.blockThreshold" :min="0" :max="100" /> %
          <span style="color: #909399; font-size: 12px; margin-left: 8px">留空表示不阻断</span>
        </el-form-item>

        <el-form-item label="通知渠道" prop="notificationChannels">
          <el-select v-model="form.notificationChannels" style="width: 100%">
            <el-option label="邮件" value="email" />
            <el-option label="企业微信" value="wechat" />
            <el-option label="钉钉" value="dingtalk" />
            <el-option label="飞书" value="feishu" />
          </el-select>
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="creating" @click="handleCreate">创建</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAlertRules, createAlertRule, toggleAlertRule, deleteAlertRule } from '@/api/alert'
import type { AlertRule, AlertRuleCreateRequest } from '@/types/alert'

const rules = ref<AlertRule[]>([])
const loading = ref(false)
const dialogVisible = ref(false)
const creating = ref(false)
const formRef = ref<FormInstance>()

const form = reactive<AlertRuleCreateRequest>({
  ruleName: '',
  ruleType: 'quota',
  targetType: 'global',
  targetId: '',
  reminderThreshold: 70,
  warningThreshold: 90,
  blockThreshold: 100,
  notificationChannels: 'email',
})

const formRules: FormRules = {
  ruleName: [{ required: true, message: '请输入规则名称', trigger: 'blur' }],
  ruleType: [{ required: true, message: '请选择规则类型', trigger: 'change' }],
  targetType: [{ required: true, message: '请选择目标类型', trigger: 'change' }],
  targetId: [{ required: true, message: '请输入目标 ID', trigger: 'blur' }],
  reminderThreshold: [{ required: true, message: '请设置提醒阈值', trigger: 'change' }],
  warningThreshold: [{ required: true, message: '请设置警告阈值', trigger: 'change' }],
  notificationChannels: [{ required: true, message: '请选择通知渠道', trigger: 'change' }],
}

function ruleTypeLabel(type: string): string {
  const map: Record<string, string> = { quota: '配额', cost: '费用', error_rate: '错误率' }
  return map[type] || type
}

function targetTypeLabel(type: string): string {
  const map: Record<string, string> = { global: '全局', user: '用户', team: '团队' }
  return map[type] || type
}

async function loadData(): Promise<void> {
  loading.value = true
  try {
    rules.value = await getAlertRules()
  } catch {
    // handled in interceptor
  } finally {
    loading.value = false
  }
}

function showCreateDialog(): void {
  form.ruleName = ''
  form.ruleType = 'quota'
  form.targetType = 'global'
  form.targetId = ''
  form.reminderThreshold = 70
  form.warningThreshold = 90
  form.blockThreshold = 100
  form.notificationChannels = 'email'
  dialogVisible.value = true
}

async function handleCreate(): Promise<void> {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  creating.value = true
  try {
    await createAlertRule(form)
    ElMessage.success('规则创建成功')
    dialogVisible.value = false
    loadData()
  } catch {
    // handled in interceptor
  } finally {
    creating.value = false
  }
}

async function handleToggle(ruleId: string, enabled: boolean): Promise<void> {
  try {
    await toggleAlertRule(ruleId, enabled)
    ElMessage.success(enabled ? '已启用' : '已禁用')
    loadData()
  } catch {
    // handled in interceptor
  }
}

async function handleDelete(ruleId: string): Promise<void> {
  try {
    await ElMessageBox.confirm('确定删除此规则？', '提示', { type: 'warning' })
    await deleteAlertRule(ruleId)
    ElMessage.success('已删除')
    loadData()
  } catch {
    // cancelled or handled in interceptor
  }
}

onMounted(loadData)
</script>
