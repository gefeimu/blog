<script setup>
import { ref, onMounted } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import { getCategories, createCategory, updateCategory, deleteCategory } from '../../api/category'

const list = ref([])
const loading = ref(false)

const dialogVisible = ref(false)
const editingId = ref(null)
const form = ref({ name: '', sort: 0 })
const saving = ref(false)

const load = async () => {
  loading.value = true
  try {
    list.value = await getCategories()
  } catch (e) {
    ElMessage.error(e.message || '加载失败')
  } finally {
    loading.value = false
  }
}

const openCreate = () => {
  editingId.value = null
  form.value = { name: '', sort: list.value.length + 1 }
  dialogVisible.value = true
}

const openEdit = (row) => {
  editingId.value = row.id
  form.value = { name: row.name, sort: row.sort }
  dialogVisible.value = true
}

const onSave = async () => {
  if (!form.value.name.trim()) {
    ElMessage.warning('请输入分类名')
    return
  }
  saving.value = true
  try {
    if (editingId.value) {
      await updateCategory(editingId.value, form.value)
    } else {
      await createCategory(form.value)
    }
    ElMessage.success('保存成功')
    dialogVisible.value = false
    load()
  } catch (e) {
    ElMessage.error(e.message || '保存失败')
  } finally {
    saving.value = false
  }
}

const onDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确定删除分类「${row.name}」吗？`, '删除确认', { type: 'warning' })
  } catch {
    return
  }
  try {
    await deleteCategory(row.id)
    ElMessage.success('已删除')
    load()
  } catch (e) {
    ElMessage.error(e.message || '删除失败')
  }
}

onMounted(load)
</script>

<template>
  <div>
    <el-card shadow="never" class="admin-page-card">
      <div class="page-header">
        <span class="page-title">分类管理</span>
        <el-button type="primary" :icon="Plus" @click="openCreate">新增分类</el-button>
      </div>
      <el-table v-loading="loading" :data="list" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="分类名" min-width="200" />
        <el-table-column prop="sort" label="排序" width="100" />
        <el-table-column label="创建时间" width="180">
          <template #default="{ row }">{{ row.createdAt?.replace('T', ' ') }}</template>
        </el-table-column>
        <el-table-column label="操作" width="140" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="openEdit(row)">编辑</el-button>
            <el-button link type="danger" @click="onDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="editingId ? '编辑分类' : '新增分类'" width="420px">
      <el-form label-width="70px">
        <el-form-item label="分类名" required>
          <el-input v-model="form.name" placeholder="如：开发技术" maxlength="50" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sort" :min="0" :max="999" />
          <span class="sort-tip">数字越小越靠前</span>
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
  color: #303133;
}
.sort-tip {
  margin-left: 8px;
  color: #909399;
  font-size: 12px;
}
</style>
