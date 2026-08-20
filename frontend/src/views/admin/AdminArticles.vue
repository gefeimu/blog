<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getArticles, deleteArticle } from '../../api/article'
import { getCategories } from '../../api/category'
import { formatDate } from '../../utils/format'

const router = useRouter()

const loading = ref(false)
const list = ref([])
const total = ref(0)
const query = ref({ page: 1, size: 10, keyword: '', status: '', categoryId: '' })
const categories = ref([])

const load = async () => {
  loading.value = true
  try {
    const params = { page: query.value.page, size: query.value.size }
    if (query.value.keyword) params.keyword = query.value.keyword
    if (query.value.status !== '') params.status = query.value.status
    if (query.value.categoryId) params.categoryId = query.value.categoryId
    const res = await getArticles(params)
    list.value = res.list
    total.value = res.total
  } catch (e) {
    ElMessage.error(e.message || '加载失败')
  } finally {
    loading.value = false
  }
}

const onSearch = () => {
  query.value.page = 1
  load()
}

const onEdit = (row) => router.push(`/admin/articles/${row.id}/edit`)

const onDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确定删除「${row.title}」吗？删除后不可恢复`, '删除确认', {
      type: 'warning',
    })
  } catch {
    return
  }
  try {
    await deleteArticle(row.id)
    ElMessage.success('已删除')
    // 当前页删空则回退一页
    if (list.value.length === 1 && query.value.page > 1) {
      query.value.page -= 1
    }
    load()
  } catch (e) {
    ElMessage.error(e.message || '删除失败')
  }
}

onMounted(async () => {
  load()
  try {
    categories.value = await getCategories()
  } catch {
    /* 分类加载失败不影响列表 */
  }
})
</script>

<template>
  <div>
    <el-card shadow="never">
      <el-form inline class="filter-form">
        <el-form-item label="标题">
          <el-input
            v-model="query.keyword"
            placeholder="搜索标题"
            clearable
            style="width: 200px"
            @keyup.enter="onSearch"
            @clear="onSearch"
          />
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="query.categoryId" placeholder="全部分类" clearable style="width: 140px" @change="onSearch">
            <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.status" placeholder="全部状态" clearable style="width: 120px" @change="onSearch">
            <el-option label="已发布" :value="1" />
            <el-option label="草稿" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="onSearch">查询</el-button>
          <el-button @click="router.push('/admin/articles/new')">写文章</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card shadow="never" class="table-card">
      <el-table v-loading="loading" :data="list" stripe>
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="title" label="标题" min-width="220" show-overflow-tooltip />
        <el-table-column prop="categoryName" label="分类" width="110" />
        <el-table-column label="标签" width="160">
          <template #default="{ row }">
            <el-tag v-for="t in row.tags || []" :key="t" size="small" class="tag-item">{{ t }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
              {{ row.status === 1 ? '已发布' : '草稿' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="viewCount" label="浏览" width="80" />
        <el-table-column label="创建时间" width="160">
          <template #default="{ row }">{{ formatDate(row.createdAt) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="130" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="onEdit(row)">编辑</el-button>
            <el-button link type="danger" @click="onDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        v-model:current-page="query.page"
        v-model:page-size="query.size"
        :total="total"
        layout="total, prev, pager, next, sizes"
        :page-sizes="[10, 20, 50]"
        class="pagination"
        @current-change="load"
        @size-change="onSearch"
      />
    </el-card>
  </div>
</template>

<style scoped>
.filter-form {
  margin-bottom: -18px;
}
.table-card {
  margin-top: 16px;
}
.tag-item {
  margin-right: 4px;
}
.pagination {
  margin-top: 16px;
  justify-content: flex-end;
}
</style>
