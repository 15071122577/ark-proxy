<template>
  <el-header class="app-header">
    <div class="header-left">
      <el-icon class="collapse-btn" :size="20" @click="$emit('toggle-sidebar')">
        <ElIconFold />
      </el-icon>
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item v-if="currentTitle">{{ currentTitle }}</el-breadcrumb-item>
      </el-breadcrumb>
    </div>

    <div class="header-right">
      <el-tag :type="roleTagType" size="small" class="role-tag">
        {{ roleLabel }}
      </el-tag>

      <el-dropdown @command="handleCommand">
        <span class="user-dropdown">
          <el-icon><ElIconUserFilled /></el-icon>
          <span class="username">{{ authStore.username }}</span>
          <el-icon class="el-icon--right"><ElIconArrowDown /></el-icon>
        </span>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item command="logout" divided>
              <el-icon><ElIconSwitchButton /></el-icon>
              退出登录
            </el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </el-header>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessageBox } from 'element-plus'
import { useAuthStore } from '@/store/auth'

defineEmits<{
  'toggle-sidebar': []
}>()

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const currentTitle = computed(() => (route.meta.title as string) || '')

const roleLabel = computed(() => {
  const map: Record<string, string> = {
    admin: '管理员',
    team_lead: '组长',
    user: '普通用户',
    guest: '访客',
  }
  return map[authStore.userRole] || authStore.userRole
})

const roleTagType = computed(() => {
  const map: Record<string, string> = {
    admin: 'danger',
    team_lead: 'warning',
    user: '',
    guest: 'info',
  }
  return (map[authStore.userRole] || 'info') as any
})

async function handleCommand(command: string): Promise<void> {
  if (command === 'logout') {
    try {
      await ElMessageBox.confirm('确定退出登录？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      })
      authStore.doLogout()
      router.push('/login')
    } catch {
      // 取消
    }
  }
}
</script>

<style scoped>
.app-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 60px;
  padding: 0 20px;
  background: #fff;
  border-bottom: 1px solid #e8e8e8;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.collapse-btn {
  cursor: pointer;
  color: #606266;
  transition: color 0.2s;
}

.collapse-btn:hover {
  color: #409eff;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.role-tag {
  font-size: 12px;
}

.user-dropdown {
  display: flex;
  align-items: center;
  gap: 6px;
  cursor: pointer;
  color: #606266;
  font-size: 14px;
}

.user-dropdown:hover {
  color: #409eff;
}

.username {
  max-width: 100px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
</style>
