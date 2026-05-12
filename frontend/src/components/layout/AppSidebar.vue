<template>
  <el-aside :width="collapse ? '64px' : '220px'" class="app-sidebar">
    <div class="sidebar-logo">
      <el-icon :size="28" color="#409eff"><ElIconPromotion /></el-icon>
      <span v-show="!collapse" class="logo-text">方舟中转站</span>
    </div>

    <el-menu
      :default-active="activeMenu"
      :collapse="collapse"
      :collapse-transition="false"
      router
      background-color="#001529"
      text-color="#ffffffa6"
      active-text-color="#409eff"
    >
      <!-- 看板 -->
      <el-sub-menu index="dashboard">
        <template #title>
          <el-icon><ElIconDataAnalysis /></el-icon>
          <span>数据看板</span>
        </template>
        <el-menu-item index="/dashboard/personal">
          <el-icon><ElIconUser /></el-icon>
          <span>个人看板</span>
        </el-menu-item>
        <el-menu-item
          v-if="authStore.hasRole(['admin', 'team_lead'])"
          index="/dashboard/team"
        >
          <el-icon><ElIconUserFilled /></el-icon>
          <span>团队看板</span>
        </el-menu-item>
        <el-menu-item v-if="authStore.isAdmin" index="/dashboard/system">
          <el-icon><ElIconMonitor /></el-icon>
          <span>系统看板</span>
        </el-menu-item>
      </el-sub-menu>

      <!-- AK 管理 -->
      <el-sub-menu index="ak">
        <template #title>
          <el-icon><ElIconKey /></el-icon>
          <span>AK 管理</span>
        </template>
        <el-menu-item index="/ak/apply">
          <el-icon><ElIconPlus /></el-icon>
          <span>AK 申请</span>
        </el-menu-item>
        <el-menu-item index="/ak/applications">
          <el-icon><ElIconDocument /></el-icon>
          <span>我的申请</span>
        </el-menu-item>
        <el-menu-item index="/ak/my">
          <el-icon><ElIconKey /></el-icon>
          <span>我的 AK</span>
        </el-menu-item>
      </el-sub-menu>

      <!-- AK 审批（管理员） -->
      <el-menu-item
        v-if="authStore.isAdmin"
        index="/admin/ak-approval"
      >
        <el-icon><ElIconChecked /></el-icon>
        <span>AK 审批</span>
      </el-menu-item>

      <!-- 预警 -->
      <el-sub-menu index="alert">
        <template #title>
          <el-icon><ElIconBell /></el-icon>
          <span>预警管理</span>
        </template>
        <el-menu-item v-if="authStore.isAdmin" index="/alert/rules">
          <el-icon><ElIconSetting /></el-icon>
          <span>预警规则</span>
        </el-menu-item>
        <el-menu-item index="/alert/history">
          <el-icon><ElIconClock /></el-icon>
          <span>预警历史</span>
        </el-menu-item>
      </el-sub-menu>
    </el-menu>
  </el-aside>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { useAuthStore } from '@/store/auth'

defineProps<{
  collapse: boolean
}>()

const route = useRoute()
const authStore = useAuthStore()

const activeMenu = computed(() => route.path)
</script>

<style scoped>
.app-sidebar {
  background: #001529;
  overflow-y: auto;
  overflow-x: hidden;
  transition: width 0.3s;
}

.sidebar-logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  border-bottom: 1px solid #ffffff1a;
}

.logo-text {
  font-size: 16px;
  font-weight: 700;
  color: #fff;
  white-space: nowrap;
}

.el-menu {
  border-right: none;
}
</style>
