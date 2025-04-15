<template>
  <div class="password-panel">
    <div class="banner">
<!--      <img src="https://img.zcool.cn/community/01f1a65d7e6f7fa801219c775d4b8e.jpg" alt="安全密码" />-->
      <h2>修改密码</h2>
      <p>请填写您的原密码并设置新密码</p>
    </div>
    <div class="card password-card">
      <el-form ref="formRef" :rules="data.rules" :model="data.user" label-width="80px" style="padding: 20px">
        <el-form-item label="原密码" prop="password">
          <el-input v-model="data.user.password" placeholder="请输入原密码" show-password></el-input>
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="data.user.newPassword" placeholder="请输入新密码" show-password></el-input>
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="data.user.confirmPassword" placeholder="请确认新密码" show-password></el-input>
        </el-form-item>
        <div style="text-align: center; margin-top: 20px;">
          <el-button type="primary" class="submit-btn" @click="updatePassword">保 存</el-button>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import {reactive, ref} from "vue";
import request from "@/utils/request.js";
import {ElMessage} from "element-plus";
import router from "@/router/index.js";

const formRef = ref()

const validatePass = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请确认密码'))
  } else {
    if (value !== data.user.newPassword) {
      callback(new Error("确认密码跟原密码不一致!"))
    }
    callback()
  }
}
const data = reactive({
  user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
  rules: {
    password: [
      { required: true, message: '请输入原密码', trigger: 'blur' },
    ],
    newPassword: [
      { required: true, message: '请输入新密码', trigger: 'blur' },
    ],
    confirmPassword: [
      { validator: validatePass, trigger: 'blur' }
    ]
  }
})

const updatePassword = () => {
  formRef.value.validate(valid => {
    if (valid) {
      request.put('/updatePassword', data.user).then(res => {
        if (res.code === '200') {
          ElMessage.success('保存成功')
          logout()
        } else {
          ElMessage.error(res.msg)
        }
      })
    }
  })
}

const logout = () => {
  localStorage.removeItem('xm-user')
  router.push('/login')
}
</script>

<style scoped>
/* 主体内容面板自适应父容器高度和宽度，居中不溢出 */
.password-panel {
  width: 90%;
  height: 80%;
  min-height: 350px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  /* 不设置背景，不影响父级页面背景 */
  /* background: none; */
  box-sizing: border-box;
  padding: 32px 0;
  overflow: hidden;
}

.banner {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 18px;
}
.banner img {
  width: 72px;
  height: 72px;
  border-radius: 50%;
  object-fit: cover;
  box-shadow: 0 4px 16px rgba(0,0,0,0.08);
  margin-bottom: 10px;
  background: #fff;
  padding: 4px;
}
.banner h2 {
  margin: 0;
  font-size: 1.4rem;
  color: #1976d2;
  letter-spacing: 1.5px;
  font-weight: 600;
}
.banner p {
  color: #666;
  font-size: 0.98rem;
  margin-top: 4px;
}

.password-card {
  width: 100%;
  max-width: 400px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 4px 16px rgba(60, 60, 100, 0.10);
  padding: 26px 20px 10px 20px;
  margin: 0 auto;
}
.el-form-item {
  margin-bottom: 18px;
}
.el-input__wrapper {
  border-radius: 6px !important;
}
.submit-btn {
  width: 112px;
  border-radius: 6px;
  font-size: 1rem;
  letter-spacing: 2px;
}
</style>
