/**
 * 应用入口：Pinia + Router + ECharts 按需注册
 * @author WangMiao
 * @date 2026-05-11
 * @version 1.0
 */
import { createApp } from 'vue'
import { createPinia } from 'pinia'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

import App from './App.vue'
import router from './router'

// 全局样式
import './assets/styles/global.css'
import './assets/styles/element-variables.scss'

// ECharts 按需注册（必须在 vue-echarts 使用前执行）
import './utils/echarts'

const app = createApp(App)

// 注册 Element Plus 图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(`ElIcon${key}`, component)
}

app.use(createPinia())
app.use(router)

app.mount('#app')
