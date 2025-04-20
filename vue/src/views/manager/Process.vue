<template>
  <div class="process-page">
    <!-- 头部 Banner 区 -->
    <div class="header-banner">
      <div class="banner-content">
        <img src="@/assets/imgs/Cquptlogo.jpg" alt="科研过程管理" class="banner-img" />
        <div>
          <h2 class="banner-title">项目过程记录</h2>
          <p class="banner-desc">高效记录、追踪与分析科研项目进展，助力团队协作与创新。</p>
        </div>
      </div>
    </div>

    <!-- 查询与操作区 -->
    <div class="card search-bar">
      <el-input
          v-model="data.projectName"
          prefix-icon="Search"
          style="width: 260px;"
          placeholder="请输入项目名称查询"
          clearable
      ></el-input>
      <el-button type="primary" @click="load" :icon="Search">查询</el-button>
      <el-button type="info" @click="reset" :icon="Refresh">重置</el-button>
      <el-button v-if="data.user.role === 'TEACHER'" type="success" @click="handleAdd" :icon="Plus" style="margin-left: 16px">新增</el-button>
      <el-button v-if="data.user.role === 'ADMIN'" type="danger" @click="delBatch" :icon="Delete" style="margin-left: 8px">批量删除</el-button>
    </div>

    <!-- 科研小贴士 -->
    <div class="card tip-card">
      <el-icon color="#409EFF" style="vertical-align: middle"><i-ep-info-filled /></el-icon>
      <span class="tip-text">温馨提示：科学记录过程，定期总结问题与方案，有助于团队知识沉淀！</span>
    </div>

    <!-- 数据表格 -->
    <div class="card">
      <el-table
          class="custom-table"
          :data="data.tableData"
          stripe
          highlight-current-row
          @selection-change="handleSelectionChange"
          empty-text="暂无数据，赶快记录你的科研过程吧！"
      >
        <el-table-column v-if="data.user.role === 'ADMIN'" type="selection" width="50" />
        <el-table-column prop="projectName" label="项目名称" />
        <el-table-column prop="projectCode" label="编号" width="120"/>
        <el-table-column prop="teacherName" label="教师" width="100"/>
        <el-table-column prop="time" label="日期" width="110"/>
        <el-table-column prop="content" label="内容" show-overflow-tooltip min-width="160"/>
        <el-table-column prop="question" label="问题" show-overflow-tooltip min-width="110"/>
        <el-table-column prop="solution" label="解决方案" show-overflow-tooltip min-width="140"/>
        <el-table-column label="操作" width="100" fixed="right">
          <template v-slot="scope">
            <el-tooltip v-if="data.user.role === 'TEACHER'" content="编辑" placement="top">
              <el-button type="primary" circle :icon="Edit" @click="handleEdit(scope.row)" />
            </el-tooltip>
            <el-tooltip v-if="data.user.role === 'ADMIN'" content="删除" placement="top">
              <el-button type="danger" circle :icon="Delete" @click="del(scope.row.id)" />
            </el-tooltip>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 分页 -->
    <div class="card pagination-bar" v-if="data.total">
      <el-pagination
          background
          layout="prev, pager, next"
          :page-size="data.pageSize"
          v-model:current-page="data.pageNum"
          :total="data.total"
          @current-change="load"
      />
    </div>

    <!-- 编辑/新增弹窗 -->
    <el-dialog title="科研过程记录" v-model="data.formVisible" width="40%" destroy-on-close>
      <el-form ref="formRef" :rules="rules" :model="data.form" label-width="90px" style="padding: 18px 0">
        <el-form-item prop="projectId" label="科研项目">
          <el-select v-model="data.form.projectId" placeholder="请选择科研项目">
            <el-option v-for="item in data.projectData" :key="item.id" :label="item.name" :value="item.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item prop="content" label="工作内容">
          <el-input type="textarea" :rows="3" v-model="data.form.content" placeholder="请输入工作内容"></el-input>
        </el-form-item>
        <el-form-item prop="question" label="遇到的问题">
          <el-input v-model="data.form.question" placeholder="请输入遇到的问题"></el-input>
        </el-form-item>
        <el-form-item prop="solution" label="解决方案">
          <el-input type="textarea" :rows="2" v-model="data.form.solution" placeholder="请输入解决方案"></el-input>
        </el-form-item>
        <el-form-item prop="time" label="报告日期">
          <el-date-picker style="width: 100%" v-model="data.form.time" value-format="YYYY-MM-DD" type="date" placeholder="请选择日期"></el-date-picker>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="data.formVisible = false">取消</el-button>
          <el-button type="primary" @click="save">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import {reactive, ref, computed} from "vue";
import request from "@/utils/request.js";
import {ElMessage, ElMessageBox} from "element-plus";
import {Delete, Edit, Plus, Search, Refresh} from "@element-plus/icons-vue";

const data = reactive({
  user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
  formVisible: false,
  form: {},
  tableData: [],
  pageNum: 1,
  pageSize: 10,
  total: 0,
  projectName: null,
  ids: [],
  projectData: []
})


const formRef = ref()
const rules = reactive({
  projectId: [ {required: true, message: '请选择科研项目', trigger: 'blur'} ],
  content:   [ {required: true, message: '请输入工作内容', trigger: 'blur'} ],
  time:      [ {required: true, message: '请选择报告日期', trigger: 'blur'} ],
})

const loadProject = () => {
  request.get('/project/selectAll', {
    params: {
      teacherId: data.user.id,
      status: '审核通过'
    }
  }).then(res => {
    if (res.code === '200') {
      data.projectData = res.data
    } else {
      ElMessage.error(res.msg)
    }
  })
}
const load = () => {
  request.get('/process/selectPage', {
    params: {
      pageNum: data.pageNum,
      pageSize: data.pageSize,
      projectName: data.projectName
    }
  }).then(res => {
    if (res.code === '200') {
      data.tableData = res.data?.list || []
      data.total = res.data?.total
    }
  })
}
const handleAdd = () => {
  data.form = {}
  data.formVisible = true
}
const handleEdit = (row) => {
  data.form = JSON.parse(JSON.stringify(row))
  data.formVisible = true
}
const add = () => {
  request.post('/process/add', data.form).then(res => {
    if (res.code === '200') {
      ElMessage.success('操作成功')
      data.formVisible = false
      load()
    } else {
      ElMessage.error(res.msg)
    }
  })
}
const update = () => {
  request.put('/process/update', data.form).then(res => {
    if (res.code === '200') {
      ElMessage.success('操作成功')
      data.formVisible = false
      load()
    }
  })
}
const save = () => {
  data.form.id ? update() : add()
}
const del = (id) => {
  ElMessageBox.confirm('删除后数据无法恢复，您确定删除吗？', '删除确认', { type: 'warning' }).then(res => {
    request.delete('/process/delete/' + id).then(res => {
      if (res.code === '200') {
        ElMessage.success("删除成功")
        load()
      } else {
        ElMessage.error(res.msg)
      }
    })
  }).catch(err => {
    // do nothing
  })
}
const delBatch = () => {
  if (!data.ids.length) {
    ElMessage.warning("请选择数据")
    return
  }
  ElMessageBox.confirm('删除后数据无法恢复，您确定删除吗？', '删除确认', { type: 'warning' }).then(res => {
    request.delete("/process/delete/batch", {data: data.ids}).then(res => {
      if (res.code === '200') {
        ElMessage.success('操作成功')
        load()
      } else {
        ElMessage.error(res.msg)
      }
    })
  }).catch(err => {
    // do nothing
  })
}
const handleSelectionChange = (rows) => {
  data.ids = rows.map(v => v.id)
}
const reset = () => {
  data.projectName = null
  load()
}

load()
loadProject()
</script>

<style scoped>
.process-page {
  max-width: 1200px;
  margin: 0 auto 40px;
  padding: 0 12px;
}

.header-banner {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: linear-gradient(90deg, #e3f2fd 60%, #bbdefb 100%);
  border-radius: 12px;
  padding: 28px 32px 18px 22px;
  margin-bottom: 16px;
  box-shadow: 0 3px 12px 0 #e3f2fd40;
}
.banner-content {
  display: flex;
  align-items: center;
}
.banner-img {
  width: 62px;
  margin-right: 18px;
}
.banner-title {
  font-size: 1.8rem;
  font-weight: bold;
  color: #1565c0;
  margin-bottom: 3px;
}
.banner-desc {
  color: #448aff;
  font-size: 1rem;
}

.banner-tips .highlight {
  color: #2e7d32;
  font-weight: bold;
  margin: 0 4px;
}

.card {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 1px 6px #b3c0d640;
  padding: 18px 22px;
  margin-bottom: 18px;
}
.search-bar {
  display: flex;
  align-items: center;
  gap: 14px;
  background: linear-gradient(90deg, #f8fafc 80%, #f3e5f5 100%);
  margin-bottom: 12px;
}
.tip-card {
  background: #e3f2fd;
  color: #1976d2;
  font-size: 1.06rem;
  margin-bottom: 16px;
  display: flex;
  align-items: center;
  border-left: 5px solid #1976d2;
  padding: 12px 22px;
}
.tip-text {
  margin-left: 9px;
}

.custom-table .el-table__row {
  font-size: 1rem;
  min-height: 48px;
  transition: background 0.3s;
}
.custom-table .el-table__row:hover {
  background: #e3f2fd !important;
}
.custom-table .el-table__cell {
  padding: 12px 8px;
}
.custom-table .el-button {
  transition: box-shadow 0.18s;
}
.custom-table .el-button:hover {
  box-shadow: 0 3px 8px #80d6ff50;
}
.pagination-bar {
  text-align: right;
  padding-top: 6px;
  padding-bottom: 2px;
}
.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 18px;
}

/* 适配移动端 */
@media (max-width: 700px) {
  .header-banner, .card {
    padding: 12px 8px;
  }
  .banner-img {
    width: 40px;
  }
  .banner-title {
    font-size: 1.1rem;
  }
}
</style>
