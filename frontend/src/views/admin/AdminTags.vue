<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import { getTags, createTag, updateTag, deleteTag } from '../../api/tag'
import { getErrorMessage } from '../../api/request'
import type { Tag } from '../../types/blog'

const list = ref<Tag[]>([])
const loading = ref(false)

const dialogVisible = ref(false)
const editingId = ref<number | null>(null)
const form = ref<{ name: string }>({ name: '' })
const saving = ref(false)

const load = async () => {
  loading.value = true
  try {
    list.value = await getTags()
  } catch (e) {
    ElMessage.error(getErrorMessage(e, '加载失败'))
  } finally {
    loading.value = false
  }
}

const openCreate = () => {
  editingId.value = null
  form.value = { name: '' }
  dialogVisible.value = true
}

const openEdit = (row: Tag) => {
  editingId.value = row.id
  form.value = { name: row.name }
  dialogVisible.value = true
}

const onSave = async () => {
  if (!form.value.name.trim()) {
    ElMessage.warning('请输入标签名')
    return
  }
  saving.value = true
  try {
    if (editingId.value) {
      await updateTag(editingId.value, { name: form.value.name.trim() })
    } else {
      await createTag({ name: form.value.name.trim() })
    }
    ElMessage.success('保存成功')
    dialogVisible.value = false
    load()
  } catch (e) {
    ElMessage.error(getErrorMessage(e, '保存失败'))
  } finally {
    saving.value = false
  }
}

const onDelete = async (row: Tag) => {
  if (row.count) {
    ElMessage.warning(`「${row.name}」下还有 ${row.count} 篇已发布文章，不能删除`)
    return
  }
  try {
    await ElMessageBox.confirm(`确定删除标签「${row.name}」吗？`, '删除确认', { type: 'warning' })
  } catch {
    return
  }
  try {
    await deleteTag(row.id)
    ElMessage.success('已删除')
    load()
  } catch (e) {
    // 后端二次校验（并发场景），显示后端错误信息
    ElMessage.error(getErrorMessage(e, '删除失败'))
  }
}

onMounted(load)
</script>

<template>
  <div>
    <el-card shadow="never" class="admin-page-card">
      <div class="page-header">
        <span class="page-title">标签管理</span>
        <el-button type="primary" :icon="Plus" @click="openCreate">新增标签</el-button>
      </div>
      <el-table v-loading="loading" :data="list" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="标签名" min-width="200" />
        <el-table-column prop="count" label="文章数" width="100">
          <template #default="{ row }">{{ (row as Tag).count ?? 0 }}</template>
        </el-table-column>
        <el-table-column label="创建时间" width="180">
          <template #default="{ row }">{{ (row as Tag).createdAt?.replace('T', ' ') }}</template>
        </el-table-column>
        <el-table-column label="操作" width="140" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="openEdit(row as Tag)">编辑</el-button>
            <el-button link type="danger" @click="onDelete(row as Tag)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="editingId ? '编辑标签' : '新增标签'" width="420px">
      <el-form label-width="70px">
        <el-form-item label="标签名" required>
          <el-input v-model="form.name" placeholder="如：Spring Boot" maxlength="50" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="onSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}
.page-title {
  font-size: 18px;
  font-weight: 600;
  color: var(--el-text-color-primary);
}
</style>
