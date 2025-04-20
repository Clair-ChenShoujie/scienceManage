<template>
  <div class="type-page">

    <!-- 1. 顶部大 Banner 轮播图 -->
    <el-carousel height="180px" class="banner-carousel" indicator-position="outside" interval="4000">
      <el-carousel-item v-for="(item, idx) in carouselImages" :key="idx">
        <div
            class="carousel-content"
            :style="{backgroundImage: `url(${item.img})`}">
          <div class="carousel-title">{{ item.title }}</div>
          <div class="carousel-desc">{{ item.desc }}</div>
        </div>
      </el-carousel-item>
    </el-carousel>

    <!-- 2. 查询区和操作区：模糊玻璃特效卡片 -->
    <div class="glass-card query-action-card">
      <div class="query-section">
        <el-input
            v-model="data.name"
            prefix-icon="Search"
            style="width: 240px; margin-right: 10px"
            size="large"
            clearable
            placeholder="请输入类型名称查询"
        ></el-input>
        <el-button type="primary" icon="Search" size="large" @click="load">查询</el-button>
        <el-button type="warning" icon="Refresh" plain size="large" style="margin: 0 10px" @click="reset">重置</el-button>
      </div>
      <div class="action-section">
        <el-button type="success" icon="Plus" size="large" round @click="handleAdd">新增类型</el-button>
        <el-button type="danger" icon="Delete" plain size="large" round style="margin-left: 14px" @click="delBatch">批量删除</el-button>
      </div>
    </div>





    <!-- 4. 表格区：温和阴影卡片 -->
    <transition name="fade">
      <div class="card table-card">
        <el-table
            stripe border
            :data="data.tableData"
            @selection-change="handleSelectionChange"
            height="360"
            style="border-radius: 16px; overflow: hidden"
        >
          <el-table-column type="selection" width="55" />
          <el-table-column prop="name" label="类型名称" min-width="160" />
          <el-table-column prop="description" label="类型描述" min-width="260" show-overflow-tooltip/>
          <el-table-column label="操作" width="110" fixed="right">
            <template #default="scope">
              <el-tooltip effect="dark" content="编辑" placement="top">
                <el-button type="primary" circle :icon="Edit" size="small" @click="handleEdit(scope.row)" />
              </el-tooltip>
              <el-tooltip effect="dark" content="删除" placement="top">
                <el-button type="danger" circle :icon="Delete" size="small" @click="del(scope.row.id)" />
              </el-tooltip>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </transition>

    <!-- 5. 分页区 -->
    <div class="pagination-bar" v-if="data.total">
      <el-pagination
          @current-change="load"
          background
          layout="prev, pager, next, jumper"
          :page-size="data.pageSize"
          v-model:current-page="data.pageNum"
          :total="data.total"
      />
    </div>

    <!-- 6. 弹窗对话框：带插图和卡通色块 -->
    <el-dialog title="成果类型" v-model="data.formVisible" width="430px" destroy-on-close class="dialog-super">
<!--      <div class="dialog-banner"></div>-->
      <el-form ref="form" :model="data.form" label-width="90px" class="dialog-form">
        <el-form-item prop="name" label="类型名称">
          <el-input v-model="data.form.name" placeholder="请输入类型名称"></el-input>
        </el-form-item>
        <el-form-item prop="description" label="类型描述">
          <el-input type="textarea" :rows="3" v-model="data.form.description" placeholder="请输入类型描述"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="data.formVisible = false">取 消</el-button>
          <el-button type="primary" @click="save">确 定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import {reactive} from "vue";
import request from "@/utils/request.js";
import {ElMessage, ElMessageBox} from "element-plus";
import {Delete, Edit, Plus, Search, Refresh} from "@element-plus/icons-vue";

// 轮播图内容
import tech1 from '@/assets/imgs/tech1.jpg';
import tech2 from '@/assets/imgs/tech2.jpg';
import tech3 from '@/assets/imgs/tech3.jpg';

const carouselImages = [
  {
    img: tech1,
    title: '成果类型多元化',
    desc: '大力推进成果高质量转化，发展新质生产力',
  },
  {
    img: tech2,
    title: '质量优先策略',
    desc: '质量为主，数量为辅，走向高水平科技自立自强',
  },
  {
    img: tech3,
    title: '产学研结合',
    desc: '走近市场，走进需求',
  },
];


const data = reactive({
  formVisible: false,
  form: {},
  tableData: [],
  pageNum: 1,
  pageSize: 10,
  total: 0,
  name: null,
  ids: [],
});
const load = () => {
  request.get('/type/selectPage', {
    params: {
      pageNum: data.pageNum,
      pageSize: data.pageSize,
      name: data.name
    }
  }).then(res => {
    if (res.code === '200') {
      data.tableData = res.data?.list || []
      data.total = res.data?.total
    }
  })
};
const handleAdd = () => {
  data.form = {}
  data.formVisible = true
};
const handleEdit = (row) => {
  data.form = JSON.parse(JSON.stringify(row))
  data.formVisible = true
};
const add = () => {
  request.post('/type/add', data.form).then(res => {
    if (res.code === '200') {
      ElMessage.success('操作成功')
      data.formVisible = false
      load()
    } else {
      ElMessage.error(res.msg)
    }
  })
};
const update = () => {
  request.put('/type/update', data.form).then(res => {
    if (res.code === '200') {
      ElMessage.success('操作成功')
      data.formVisible = false
      load()
    }
  })
};
const save = () => {
  data.form.id ? update() : add()
};
const del = (id) => {
  ElMessageBox.confirm('删除后数据无法恢复，您确定删除吗？', '删除确认', { type: 'warning' }).then(res => {
    request.delete('/type/delete/' + id).then(res => {
      if (res.code === '200') {
        ElMessage.success("删除成功")
        load()
      } else {
        ElMessage.error(res.msg)
      }
    })
  }).catch(()=>{});
};
const delBatch = () => {
  if (!data.ids.length) {
    ElMessage.warning("请选择数据")
    return
  }
  ElMessageBox.confirm('删除后数据无法恢复，您确定删除吗？', '删除确认', { type: 'warning' }).then(res => {
    request.delete("/type/delete/batch", {data: data.ids}).then(res => {
      if (res.code === '200') {
        ElMessage.success('操作成功')
        load()
      } else {
        ElMessage.error(res.msg)
      }
    })
  }).catch(()=>{});
};
const handleSelectionChange = (rows) => {
  data.ids = rows.map(v => v.id)
};
const reset = () => {
  data.name = null
  load()
};
load()
</script>

<style scoped>
.type-page {
  background: linear-gradient(120deg, #f5f8fd 55%, #e0edfa 100%);
  padding: 24px 0 40px 0;
  min-height: 100vh;
}
.banner-carousel {
  border-radius: 20px;
  overflow: hidden;
  margin: 0 auto 24px auto;
  max-width: 1100px;
  box-shadow: 0 6px 40px #a2bbde34;
}
.carousel-content {
  display: flex;
  flex-direction: column;
  justify-content: flex-end;
  align-items: flex-start;
  background-size: cover;
  background-position: center;
  height: 100%;
  padding: 24px 34px 22px 34px;
  color: #fff;
  position: relative;
  min-height: 140px;
  box-sizing: border-box;
  font-family: 'Helvetica Neue', PingFangSC, 'Microsoft Yahei', Arial, sans-serif;
}
.carousel-title {
  font-weight: bolder;
  font-size: 25px;
  text-shadow: 0 2px 12px #5872a084;
}
.carousel-desc {
  margin-top: 10px;
  font-size: 15px;
  font-weight: 300;
  text-shadow: 0 2px 8px #5872a080;
}
.glass-card {
  display: flex;
  align-items: center;
  background: rgba(252, 253, 255, 0.43);
  backdrop-filter: blur(8px);
  border-radius: 13px;
  padding: 22px 26px 18px 28px;
  margin: 0 auto 19px auto;
  max-width: 1100px;
  max-height: 100px;
}
.query-card {
  justify-content: center;
}
.action-card {
  justify-content: flex-start;
  gap: 10px;
}
.card.table-card {
  margin: 0 auto 18px auto;
  max-width: 1100px;
  background: #fffffffa;
  border-radius: 21px;
  box-shadow: 0 6px 36px #a2bbde33;
  padding: 24px 24px 5px 24px;
}
.pagination-bar {
  display: flex;
  justify-content: center;
  margin: 22px 0 0 0;
}
.fade-enter-active, .fade-leave-active {
  transition: opacity 0.44s;
}
.fade-enter-from, .fade-leave-to {
  opacity: 0;
}

.query-action-card {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: rgba(252, 253, 255, 0.43);
  backdrop-filter: blur(8px);
  border-radius: 13px;
  padding: 22px 26px 18px 28px;
  margin: 0 auto 19px auto;
  max-width: 1100px;
}

.query-section {
  display: flex;
  align-items: center;
  gap: 10px;
}

.action-section {
  display: flex;
  align-items: center;
  gap: 14px;
}

.table-pagination-card {
  margin: 0 auto 0 auto;
  max-width: 1100px;
  background: #fffffffa;
  border-radius: 21px;
  box-shadow: 0 6px 36px #a2bbde33;
  padding: 24px 24px 24px 24px;
}

.table-section {
  margin-bottom: 20px;
}

.pagination-section {
  display: flex;
  justify-content: center;
  padding-top: 16px;
  border-top: 1px solid #e3eaf5;
}


/* Dialog 美化 */
.dialog-super >>> .el-dialog__wrapper {
  background: rgba(130,170,230,.07)!important;
  backdrop-filter: blur(2px)!important;
}
.dialog-super .el-dialog {
  border-radius: 18px;
  overflow: hidden;
  box-shadow: 0 6px 30px #a2bbde46;
  background: #f9fbff;
  border: 1px solid #e3eaf5;
}

@keyframes banneranime {
  to{
    background-position-x:200px;
  }
}
.dialog-form {
  padding: 10px 12px 2px 12px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 16px;
}

/* 按钮自定义（可选） */
.el-button.is-round {
  font-weight: 500;
  letter-spacing: 2px;
}

</style>
