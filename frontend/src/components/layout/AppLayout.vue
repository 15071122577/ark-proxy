<template>
  <el-container class="app-layout">
    <AppSidebar :collapse="sidebarCollapsed" />
    <el-container>
      <AppHeader @toggle-sidebar="sidebarCollapsed = !sidebarCollapsed" />
      <el-main class="app-main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import AppSidebar from './AppSidebar.vue'
import AppHeader from './AppHeader.vue'

const sidebarCollapsed = ref(false)
</script>

<style scoped>
.app-layout {
  height: 100vh;
  overflow: hidden;
}

.app-main {
  background: #f0f2f5;
  padding: 20px;
  overflow-y: auto;
}
</style>
