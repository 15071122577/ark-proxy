<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <span style="font-weight: 600">AK 申请</span>
      </template>

      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
        style="max-width: 600px"
      >
        <el-form-item label="用途" prop="purpose">
          <el-select v-model="form.purpose" placeholder="请选择用途" style="width: 100%">
            <el-option label="项目开发" value="development" />
            <el-option label="测试验证" value="testing" />
            <el-option label="生产使用" value="production" />
            <el-option label="学习研究" value="research" />
          </el-select>
        </el-form-item>

        <el-form-item label="厂商" prop="providerId">
          <el-select v-model="form.providerId" placeholder="请选择厂商" style="width: 100%">
            <el-option
              v-for="p in providers"
              :key="p.providerId"
              :label="p.providerName"
              :value="p.providerId"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="模型" prop="model">
          <el-select v-model="form.model" placeholder="请选择模型" style="width: 100%">
            <el-option
              v-for="m in filteredModels"
              :key="m.standardModelName"
              :label="m.standardModelName"
              :value="m.standardModelName"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="预估用量" prop="estimatedUsage">
          <el-input v-model="form.estimatedUsage" placeholder="例如：每天约 1000 次请求，50 万 Token" />
        </el-form-item>

        <el-form-item label="申请理由" prop="reason">
          <el-input
            v-model="form.reason"
            type="textarea"
            :rows="4"
            placeholder="请详细描述申请理由和使用场景"
          />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" :loading="submitting" @click="handleSubmit">
            提交申请
          </el-button>
          <el-button @click="resetForm">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import type { FormInstance, FormRules } from 'element-plus'
import { ElMessage } from 'element-plus'
import { applyAK } from '@/api/ak'
import { getProviders, getModelMappings } from '@/api/provider'
import type { ProviderInfo, ModelMapping } from '@/types/provider'
import type { AKApplyRequest } from '@/types/ak'

const router = useRouter()
const formRef = ref<FormInstance>()
const submitting = ref(false)

const providers = ref<ProviderInfo[]>([])
const models = ref<ModelMapping[]>([])

const form = reactive<AKApplyRequest>({
  purpose: '',
  providerId: '',
  model: '',
  estimatedUsage: '',
  reason: '',
})

const rules: FormRules = {
  purpose: [{ required: true, message: '请选择用途', trigger: 'change' }],
  providerId: [{ required: true, message: '请选择厂商', trigger: 'change' }],
  model: [{ required: true, message: '请选择模型', trigger: 'change' }],
  estimatedUsage: [{ required: true, message: '请输入预估用量', trigger: 'blur' }],
  reason: [{ required: true, message: '请输入申请理由', trigger: 'blur' }],
}

const filteredModels = computed(() => {
  if (!form.providerId) return []
  return models.value.filter((m) => m.providerId === form.providerId && m.enabled)
})

async function handleSubmit(): Promise<void> {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    await applyAK(form)
    ElMessage.success('申请已提交，请等待审批')
    router.push('/ak/applications')
  } catch {
    // handled in interceptor
  } finally {
    submitting.value = false
  }
}

function resetForm(): void {
  formRef.value?.resetFields()
}

async function loadDeps(): Promise<void> {
  try {
    const [p, m] = await Promise.allSettled([getProviders(), getModelMappings()])
    if (p.status === 'fulfilled') providers.value = p.value
    if (m.status === 'fulfilled') models.value = m.value
  } catch {
    // handled in interceptor
  }
}

onMounted(loadDeps)
</script>
