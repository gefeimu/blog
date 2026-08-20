<script setup lang="ts">
import { ref, onMounted, onUnmounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import Vditor from 'vditor'
import 'vditor/dist/index.css'
import { getArticle, getTags, createArticle, updateArticle } from '../../api/article'
import { getErrorMessage } from '../../api/request'
import { useTheme } from '../../composables/useTheme'
import type { Tag } from '../../types/blog'

const route = useRoute()
const router = useRouter()
const { theme } = useTheme()

const articleId = route.params.id ? Number(route.params.id) : null
const isEdit = articleId !== null

const saving = ref(false)
const loading = ref(false)
const tags = ref<Tag[]>([])

const form = ref({
  title: '',
  categoryId: null as number | null,
  tagIds: [] as number[],
  summary: '',
  status: 1,
  layout: 'default',
  ext: { cover: '' },
})

let vditor: Vditor | null = null

const initVditor = () => {
  const dark = theme.value === 'dark'
  vditor = new Vditor('vditor', {
    height: 'calc(100vh - 420px)',
    minHeight: 360,
    mode: 'ir',
    // 主题跟随全局暗色模式（可被下方 watch 动态切换）
    theme: dark ? 'dark' : 'classic',
    placeholder: '开始写作…（支持 Markdown，可拖拽/粘贴上传图片）',
    cache: { enable: false },
    toolbarConfig: { pin: true },
    upload: {
      url: '/api/upload/image',
      fieldName: 'file',
      headers: { Authorization: `Bearer ${localStorage.getItem('blog_token')}` },
      format(files, responseText) {
        let res
        try {
          res = JSON.parse(responseText)
        } catch {
          return JSON.stringify({ msg: '响应解析失败', code: 1, data: { errFiles: [], succMap: {} } })
        }
        return JSON.stringify({
          msg: '',
          code: 0,
          data: {
            errFiles: [],
            succMap: { [files[0].name]: res.url },
          },
        })
      },
      error(msg) {
        ElMessage.error(typeof msg === 'string' ? msg : '图片上传失败')
      },
    },
  })
}

const loadArticle = async () => {
  loading.value = true
  try {
    const res = await getArticle(articleId as number)
    form.value.title = res.title
    form.value.categoryId = null
    form.value.tagIds = res.tagIds || []
    form.value.summary = res.summary || ''
    form.value.status = res.status
    form.value.layout = res.layout || 'default'
    form.value.ext = { cover: res.ext?.cover || '' }
    if (vditor) {
      vditor.setValue(res.content || '')
    } else {
      setTimeout(() => vditor?.setValue(res.content || ''), 0)
    }
  } catch (e) {
    ElMessage.error(getErrorMessage(e, '加载文章失败'))
  } finally {
    loading.value = false
  }
}

const onSave = async (status: number | null) => {
  if (!form.value.title.trim()) {
    ElMessage.warning('请输入标题')
    return
  }
  const payload = {
    title: form.value.title.trim(),
    categoryId: form.value.categoryId,
    tagIds: form.value.tagIds,
    summary: form.value.summary?.trim() || '',
    status: status ?? form.value.status,
    layout: form.value.layout,
    // ext 扩展字段：封面图 URL 为空时传空对象，避免写入脏数据
    ext: form.value.ext?.cover?.trim() ? { cover: form.value.ext.cover.trim() } : {},
    content: vditor?.getValue() || '',
  }
  saving.value = true
  try {
    if (isEdit) {
      await updateArticle(articleId, payload)
    } else {
      await createArticle(payload)
    }
    ElMessage.success('保存成功')
    router.push('/admin/articles')
  } catch (e) {
    ElMessage.error(getErrorMessage(e, '保存失败'))
  } finally {
    saving.value = false
  }
}

onMounted(async () => {
  try {
    tags.value = await getTags()
  } catch {
    /* 标签加载失败不阻塞 */
  }
  initVditor()
  if (isEdit) {
    loadArticle()
  }
})

onUnmounted(() => {
  vditor?.destroy()
})

// 全局暗色切换时联动 Vditor 编辑器主题（编辑器/内容/代码三段都切）
watch(theme, (t) => {
  const dark = t === 'dark' ? 'dark' : 'classic'
  vditor?.setTheme(dark, dark, dark)
})
</script>

<template>
  <div v-loading="loading">
    <el-card shadow="never" class="admin-page-card">
      <el-page-header :content="isEdit ? '编辑文章' : '写文章'" @back="router.push('/admin/articles')" />
    </el-card>

    <el-card shadow="never" class="admin-page-card form-card">
      <el-form label-width="70px">
        <el-form-item label="标题" required>
          <el-input v-model="form.title" placeholder="文章标题" maxlength="200" show-word-limit />
        </el-form-item>
        <el-form-item label="标签">
          <el-select v-model="form.tagIds" multiple placeholder="选择标签（可多选）" style="width: 100%; max-width: 560px">
            <el-option v-for="t in tags" :key="t.id" :label="t.name" :value="t.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="摘要">
          <el-input
            v-model="form.summary"
            type="textarea"
            :rows="2"
            maxlength="500"
            show-word-limit
            placeholder="文章摘要（列表页展示）"
          />
        </el-form-item>
        <el-row :gutter="24">
          <el-col :span="8">
            <el-form-item label="状态">
              <el-radio-group v-model="form.status">
                <el-radio :value="1">发布</el-radio>
                <el-radio :value="0">草稿</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="布局">
              <el-select v-model="form.layout" style="width: 100%">
                <el-option label="默认" value="default" />
                <el-option label="极简" value="minimal" />
                <el-option label="横幅" value="banner" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="封面图">
              <el-input
                v-model="form.ext.cover"
                placeholder="图片 URL（横幅布局顶部展示）"
                clearable
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="正文" class="vditor-form-item">
          <div id="vditor" class="vditor-box" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="saving" @click="onSave(null)">保存</el-button>
          <el-button :loading="saving" @click="onSave(0)">存草稿</el-button>
          <el-button @click="router.push('/admin/articles')">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<style scoped>
.form-card {
  min-height: calc(100vh - 116px);
}
.vditor-form-item :deep(.el-form-item__content) {
  display: block;
}
.vditor-box {
  width: 100%;
}
</style>
