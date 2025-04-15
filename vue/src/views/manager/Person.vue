<template>
  <div class="person-wrapper">
    <div class="card person-card">
      <el-form ref="form" :model="data.user" label-width="70px" style="padding: 20px">
        <el-form-item prop="avatar" label="头像">
          <el-upload
              :action="baseUrl + '/files/upload'"
              :on-success="handleFileUpload"
              :show-file-list="false"
              class="avatar-uploader"
          >
            <img v-if="data.user.avatar" :src="data.user.avatar" class="avatar" />
            <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
          </el-upload>
        </el-form-item>
        <el-form-item prop="username" label="用户名">
          <el-input disabled v-model="data.user.username" placeholder="请输入用户名"></el-input>
        </el-form-item>
        <el-form-item prop="name" label="姓名">
          <el-input v-model="data.user.name" placeholder="请输入姓名"></el-input>
        </el-form-item>
        <el-form-item prop="phone" label="电话">
          <el-input v-model="data.user.phone" placeholder="请输入电话"></el-input>
        </el-form-item>
        <el-form-item prop="email" label="邮箱">
          <el-input v-model="data.user.email" placeholder="请输入邮箱"></el-input>
        </el-form-item>
        <div style="text-align: center; margin-top: 20px;">
          <el-button type="primary" class="submit-btn" @click="update">保 存</el-button>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { reactive } from 'vue';
import request from "@/utils/request.js";
const baseUrl = import.meta.env.VITE_BASE_URL

const data = reactive({
  user: JSON.parse(localStorage.getItem('xm-user')|| '{}')
})

const handleFileUpload = (res) => {
  data.user.avatar = res.data
}

const emit = defineEmits(['updateUser'])
const update = () => {
  let url = data.user.role === 'ADMIN' ? '/admin/update' : '/teacher/update'
  request.put(url, data.user).then(res => {
    if (res.code === '200') {
      ElMessage.success('保存成功')
      localStorage.setItem('xm-user', JSON.stringify(data.user))
      emit('updateUser')
    } else {
      ElMessage.error(res.msg)
    }
  })
}
</script>

<style scoped>
/* 背景渐变，内容居中 */
.person-wrapper {
  width: 100%;
  height: 100%;
  min-height: 420px;
  display: flex;
  align-items: center;
  justify-content: center;
  /* 渐变背景 */
  background: linear-gradient(120deg, #e4f1ff 0%, #ffffff 100%);
  box-sizing: border-box;
  padding: 40px 0;
  overflow: hidden;
}

.person-card {
  width: 100%;
  max-width: 420px;
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(60, 60, 100, 0.13);
  margin: 0 auto;
  padding: 32px 24px 18px 24px;
}

/* 头像上传美化 */
.avatar-uploader {
  height: 130px;
  display: flex;
  align-items: center;
  margin-left: auto;
  margin-right: auto;
}

.avatar-uploader .avatar {
  width: 112px;
  height: 112px;
  display: flex;
  border-radius: 50%;
  object-fit: cover;
  border: 4px solid #2b3f8e;
  box-shadow: 0 3px 12px rgba(0,0,0,0.08);
  background: #fff;
  margin-right: 10px;


}
.avatar-uploader .el-upload {
  border: 1px dashed var(--el-border-color);
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: var(--el-transition-duration-fast);
  border-radius: 50%;
  width: 112px;
  height: 112px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f7f7fa;
}
.avatar-uploader .el-upload:hover {
  border-color: var(--el-color-primary);
}

.el-icon.avatar-uploader-icon {
  font-size: 36px;
  color: #1c2551;
  width: 112px;
  height: 112px;
  display: flex;
  align-items: center;
  justify-content: center;
}
.el-form-item {
  margin-bottom: 20px;
}
.el-input__wrapper {
  border-radius: 8px !important;
}
.submit-btn {
  width: 120px;
  border-radius: 8px;
  font-size: 1.04rem;
  letter-spacing: 2px;
}
</style>
