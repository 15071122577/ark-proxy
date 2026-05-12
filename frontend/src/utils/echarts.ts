/**
 * ECharts 按需注册：仅注册项目用到的图表类型
 * @author WangMiao
 * @date 2026-05-11
 * @version 1.0
 */
import { use } from 'echarts/core'

// 图表类型
import { LineChart, PieChart, BarChart, GaugeChart } from 'echarts/charts'

// 组件
import {
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent,
  DatasetComponent,
  TransformComponent,
  ToolboxComponent,
} from 'echarts/components'

// 渲染器
import { CanvasRenderer } from 'echarts/renderers'

// 标签布局
import { LabelLayout, UniversalTransition } from 'echarts/features'

// 按需注册
use([
  LineChart,
  PieChart,
  BarChart,
  GaugeChart,
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent,
  DatasetComponent,
  TransformComponent,
  ToolboxComponent,
  LabelLayout,
  UniversalTransition,
  CanvasRenderer,
])
