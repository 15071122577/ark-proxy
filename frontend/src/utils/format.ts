/**
 * 数据格式化工具：数字/货币/百分比/日期格式化，AK/SK 脱敏
 * @author WangMiao
 * @date 2026-05-11
 * @version 1.0
 */
import dayjs from 'dayjs'

/** 格式化数字（千分位） */
export function formatNumber(value: number | string): string {
  const num = Number(value)
  if (isNaN(num)) return '0'
  return num.toLocaleString('zh-CN')
}

/** 格式化货币（人民币） */
export function formatCurrency(value: number | string, prefix = '¥'): string {
  const num = Number(value)
  if (isNaN(num)) return `${prefix}0.00`
  return `${prefix}${num.toFixed(2)}`
}

/** 格式化百分比 */
export function formatPercent(value: number | string, decimals = 1): string {
  const num = Number(value)
  if (isNaN(num)) return '0%'
  return `${num.toFixed(decimals)}%`
}

/** 格式化 Token 数量（自动转 K/M） */
export function formatTokens(value: number | string): string {
  const num = Number(value)
  if (isNaN(num)) return '0'
  if (num >= 1_000_000) return `${(num / 1_000_000).toFixed(1)}M`
  if (num >= 1_000) return `${(num / 1_000).toFixed(1)}K`
  return num.toString()
}

/** 格式化日期 */
export function formatDate(value: string | Date, fmt = 'YYYY-MM-DD HH:mm:ss'): string {
  return dayjs(value).format(fmt)
}

/** 格式化相对时间 */
export function formatRelativeTime(value: string | Date): string {
  const diff = dayjs().diff(dayjs(value), 'second')
  if (diff < 60) return '刚刚'
  if (diff < 3600) return `${Math.floor(diff / 60)} 分钟前`
  if (diff < 86400) return `${Math.floor(diff / 3600)} 小时前`
  return `${Math.floor(diff / 86400)} 天前`
}

/** AK 脱敏：保留前4后4 */
export function maskAK(ak: string): string {
  if (!ak || ak.length <= 8) return '****'
  return `${ak.slice(0, 4)}${'*'.repeat(ak.length - 8)}${ak.slice(-4)}`
}

/** SK 脱敏：全部遮盖 */
export function maskSK(sk: string): string {
  if (!sk) return ''
  return '*'.repeat(Math.min(sk.length, 32))
}

/** 格式化响应时间（ms） */
export function formatLatency(ms: number | string): string {
  const num = Number(ms)
  if (isNaN(num)) return '-'
  if (num < 1000) return `${num.toFixed(0)}ms`
  return `${(num / 1000).toFixed(2)}s`
}
