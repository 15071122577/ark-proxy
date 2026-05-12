/**
 * 剪贴板工具：复制文本到剪贴板
 * @author WangMiao
 * @date 2026-05-11
 * @version 1.0
 */
import { ElMessage } from 'element-plus'

/** 复制文本到剪贴板 */
export async function copyToClipboard(text: string, tip = '已复制到剪贴板'): Promise<void> {
  try {
    await navigator.clipboard.writeText(text)
    ElMessage.success(tip)
  } catch {
    // 降级方案：使用 execCommand
    const textarea = document.createElement('textarea')
    textarea.value = text
    textarea.style.position = 'fixed'
    textarea.style.opacity = '0'
    document.body.appendChild(textarea)
    textarea.select()
    try {
      document.execCommand('copy')
      ElMessage.success(tip)
    } catch {
      ElMessage.error('复制失败，请手动复制')
    } finally {
      document.body.removeChild(textarea)
    }
  }
}
