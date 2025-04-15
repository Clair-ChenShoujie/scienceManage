<template>
  <div class="project-bg">
    <!-- 顶部Banner -->
    <div class="project-banner">
      <img src="@/assets/imgs/Cquptlogo.jpg"
           class="banner-img" alt="科研项目"/>
      <div class="banner-content">
        <h1>
          <el-icon style="color:#4f8eff; vertical-align: middle;"><View /></el-icon>
          科研项目管理系统
        </h1>
        <p>赋能创新，助力科研，轻松管理您的学术项目</p>
      </div>
    </div>

    <!-- 查询区域 -->
    <div class="project-card search-card">
      <el-input v-model="data.code" prefix-icon="Search" style="width:200px; margin-right:12px" placeholder="项目编号"/>
      <el-input v-model="data.name" prefix-icon="Search" style="width:200px; margin-right:12px" placeholder="项目名称"/>
      <el-button type="primary" plain @click="load" icon="Search">查询</el-button>
      <el-button type="info" plain @click="reset" icon="Refresh">重置</el-button>
      <div v-if="data.user.role === 'TEACHER'" style="padding-left: 250px">
        <el-button type="success" plain icon="Plus" @click="handleAdd">新增科研项目</el-button>
      </div>
    </div>



    <!-- 表格卡片 -->
    <div class="project-card table-card">
      <el-table
          :data="data.tableData"
          stripe
          highlight-current-row
          class="custom-table"
          v-loading="!data.tableData.length && !data.total"
          empty-text="暂无数据，快来创建您的第一个科研项目吧！"
      >
        <el-table-column prop="code" label="项目编号" width="110"/>
        <el-table-column prop="name" label="项目名称" width="200"/>
        <el-table-column prop="status" label="审核状态" width="110">
          <template #default="scope">
            <el-tag v-if="scope.row.status === '审核通过'" type="success" effect="dark">{{ scope.row.status }}</el-tag>
            <el-tag v-else-if="scope.row.status === '待审核'" type="warning" effect="dark">{{ scope.row.status }}</el-tag>
            <el-tag v-else type="danger" effect="dark">{{ scope.row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="source" label="项目来源" width="120"/>
        <el-table-column prop="level" label="项目等级" width="90"/>
        <el-table-column prop="subject" label="学科" width="120"/>
        <el-table-column prop="price" label="预算(元)" width="110"/>
        <el-table-column prop="start" label="开始时间" width="120"/>
        <el-table-column prop="end" label="结束时间" width="120"/>
        <el-table-column prop="file" label="申请材料" width="110">
          <template #default="scope">
            <el-tooltip content="下载申请材料">
              <el-button type="primary" size="small" @click="down(scope.row.file)" icon="Download"/>
            </el-tooltip>
          </template>
        </el-table-column>
        <el-table-column prop="teacherName" label="申请教师" width="110"/>
        <el-table-column prop="reason" label="审核原因" width="130" show-overflow-tooltip/>
        <el-table-column prop="time" label="审核时间" width="130" show-overflow-tooltip/>
        <el-table-column label="操作" width="110" fixed="right">
          <template #default="scope">
            <el-tooltip content="编辑" v-if="data.user.role === 'TEACHER' && scope.row.status === '待审核'">
              <el-button type="primary" circle size="small" :icon="Edit" @click="handleEdit(scope.row)"/>
            </el-tooltip>
            <el-tooltip content="审核" v-if="data.user.role === 'ADMIN'">
              <el-button type="warning" circle size="small" :icon="View" @click="handCheck(scope.row)"/>
            </el-tooltip>
            <el-tooltip content="删除">
              <el-button type="danger" circle size="small" :icon="Delete" @click="del(scope.row.id)"/>
            </el-tooltip>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 分页 -->
    <div v-if="data.total" class="project-card pager-card">
      <el-pagination
          @current-change="load"
          background
          layout="prev, pager, next"
          :page-size="data.pageSize"
          v-model:current-page="data.pageNum"
          :total="data.total"
          style="text-align:center"
      />
    </div>

    <!-- 新增/编辑弹窗 -->
    <el-dialog title="科研项目提交" v-model="data.formVisible" width="480px" destroy-on-close>
      <el-form ref="formRef" :rules="rules" :model="data.form" label-width="90px" class="dialog-form">
        <el-form-item prop="file" label="申请材料">
          <el-upload :action="baseUrl + '/files/upload'" :on-success="handleFileUpload" :show-file-list="false">
            <el-button type="primary" icon="UploadFilled">点击上传</el-button>
          </el-upload>
        </el-form-item>
        <el-form-item prop="source" label="项目来源">
          <el-input v-model="data.form.source" placeholder="请输入项目来源"/>
        </el-form-item>
        <el-form-item prop="name" label="项目名称">
          <el-input v-model="data.form.name" placeholder="请输入项目名称"/>
        </el-form-item>
        <el-form-item prop="level" label="项目等级">
          <el-select v-model="data.form.level" placeholder="请选择项目等级">
            <el-option label="一级" value="一级"/>
            <el-option label="二级" value="二级"/>
            <el-option label="三级" value="三级"/>
          </el-select>
        </el-form-item>
        <el-form-item prop="subject" label="申请学科">
          <el-input v-model="data.form.subject" placeholder="请输入申请学科"/>
        </el-form-item>
        <el-form-item prop="start" label="开始日期">
          <el-date-picker style="width:100%" v-model="data.form.start" value-format="YYYY-MM-DD" type="date" placeholder="请选择日期"/>
        </el-form-item>
        <el-form-item prop="end" label="结束日期">
          <el-date-picker style="width:100%" v-model="data.form.end" value-format="YYYY-MM-DD" type="date" placeholder="请选择日期"/>
        </el-form-item>
        <el-form-item prop="price" label="项目预算">
          <el-input-number v-model="data.form.price" :min="1" :max="1000000" label="预算"/>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="data.formVisible = false">取 消</el-button>
        <el-button type="primary" @click="save">确 定</el-button>
      </template>
    </el-dialog>

    <!-- 审核弹窗 -->
    <el-dialog title="科研项目审核" v-model="data.checkVisible" width="400px" destroy-on-close>
      <el-form :model="data.form" label-width="80px" class="dialog-form">
        <el-form-item prop="status" label="提交状态">
          <el-select v-model="data.form.status" placeholder="请选择审核结果">
            <el-option label="待审核" value="待审核"/>
            <el-option label="审核通过" value="审核通过"/>
            <el-option label="不通过" value="不通过"/>
          </el-select>
        </el-form-item>
        <el-form-item prop="reason" label="审核理由">
          <el-input v-model="data.form.reason" placeholder="请输入审核理由"/>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="data.checkVisible = false">取 消</el-button>
        <el-button type="primary" @click="submit">提 交</el-button>
      </template>
    </el-dialog>
  </div>
</template>



<style scoped>
.project-bg {
  min-height: 100vh;
  background: linear-gradient(135deg, #e9f2ff 0%, #f7fbff 100%);
  padding-bottom: 40px;
}

.project-banner {
  display: flex;
  align-items: center;
  background: #fff;
  border-radius: 18px;
  box-shadow: 0 4px 24px 0 rgba(0,64,128,0.07), 0 0.5px 2px 0 rgba(0,64,128,0.06);
  padding: 32px 40px 28px 32px;
  margin: 32px auto 24px auto;
  max-width: 1100px;
}

.banner-img {
  width: 110px;
  height: 110px;
  object-fit: cover;
  border-radius: 14px;
  margin-right: 32px;
  box-shadow: 0 2px 14px rgba(80,100,200,0.12);
}

.banner-content h1 {
  font-size: 2.1rem;
  font-weight: 700;
  color: #2458a6;
  margin-bottom: 10px;
  letter-spacing: 1px;
}
.banner-content p {
  font-size: 1.13rem;
  color: #4670a6;
  margin: 0;
}

.project-card {
  max-width: 1100px;
  margin: 0 auto 24px auto;
  background: #fff;
  padding: 22px 32px 18px 32px;
  border-radius: 16px;
  box-shadow: 0 2px 16px 0 rgba(70,120,200,0.07), 0 0.5px 2px 0 rgba(70,120,200,0.06);
}

.search-card {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 16px;
  padding-bottom: 16px;
}

.add-card {
  text-align: right;
  padding-top: 6px;
  padding-bottom: 15px;
}

.table-card {
  padding-top: 14px;
  padding-bottom: 20px;
}

.pager-card {
  text-align: center;
  padding: 14px 0 10px 0;
  background: transparent;
  box-shadow: none;
}

.custom-table .el-table__header th {
  background: #f1f5fd !important;
  color: #3460b1;
  font-weight: bold;
  font-size: 15px;
}

.custom-table .el-table__row {
  font-size: 15px;
  transition: background 0.2s;
}
.custom-table .el-table__row:hover {
  background: #f0f5fe !important;
}

.dialog-form {
  padding: 10px 10px 0 10px;
}

.el-dialog__body {
  padding-top: 0 !important;
}

.el-dialog__footer {
  text-align: right;
}
</style>


<script setup>

import { reactive, ref } from 'vue';
import request from "@/utils/request.js";
import { ElMessage } from 'element-plus';
import { Delete, Edit, View } from '@element-plus/icons-vue';
const baseUrl = import.meta.env.VITE_BASE_URL
const formRef = ref()

const data = reactive({
  user: JSON.parse(localStorage.getItem("xm-user") || '{}'),
  formVisible: false,
  checkVisible: false,
  form: {},
  tableData: [],
  pageNum: 1,
  pageSize: 10,
  total: 0,
  code: null,
  name: null,
})

const rules = reactive({
  source: [
    {required: true, message: '请输入项目来源', trigger: 'blur'},
  ],
  name: [
    {required: true, message: '请输入项目名称', trigger: 'blur'},
  ],
  level: [
    {required: true, message: '请选择项目等级', trigger: 'blur'},
  ],
  subject: [
    {required: true, message: '请输入申请学科', trigger: 'blur'},
  ],
  start: [
    {required: true, message: '请选择开始日期', trigger: 'blur'},
  ],
  end: [
    {required: true, message: '请选择结束日期', trigger: 'blur'},
  ],
})

const load = () => {
  request.get('/project/selectPage', {
    params: {
      pageNum: data.pageNum,
      pageSize: data.pageSize,
      code: data.code,
      name: data.name,
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

const handCheck = (row) => {
  data.form = JSON.parse(JSON.stringify(row))
  data.checkVisible = true

}

const add = () => {
  request.post('/project/add', data.form).then(res => {
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
  request.put('/project/update', data.form).then(res => {
    if (res.code === '200') {
      ElMessage.success('操作成功')
      data.formVisible = false
      load()
    }
  })
}

const save = () => {
  formRef.value.validate(valid => {
    if (valid){
      data.form.id ? update() : add()
    }
  })
}

const submit = () =>{
  request.put('/project/check', data.form).then(res =>{
    if(res.code === '200'){
      ElMessage.success('操作成功')
      data.checkVisible = false
      load()
    }else{
      ElMessage.error(res.msg)
    }
  }

  )
}

const del = (id) => {
  ElMessageBox.confirm('删除后数据无法恢复，您确定删除吗？', '删除确认', { type: 'warning' }).then(res => {
    request.delete('/project/delete/' + id).then(res => {
      if (res.code === '200') {
        ElMessage.success("删除成功")
        load()
      } else {
        ElMessage.error(res.msg)
      }
    })
  }).catch(err => {
    console.error(err)
  })
}

const reset = () => {
  data.code = null
  data.name = null
  load()
}

const handleFileUpload = (res) => {
  data.form.file = res.data
}
const down = (url) => {
  window.open(url)
}

load()

</script>
