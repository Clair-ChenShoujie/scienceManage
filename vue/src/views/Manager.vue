<template>
  <div class="manager-container">

    <div class="manager-header">
      
      <div class="manager-header-left">
        <img src="@/assets/imgs/alu.jpg" alt="">
        <div class="title">高校科研管理系统</div>
      </div>

      <div class="manager-header-center">
        <el-breadcrumb separator="/">
          <el-breadcrumb-item :to="{ path: '/manager/home' }">首页</el-breadcrumb-item>
          <el-breadcrumb-item>{{ router.currentRoute.value.meta.name }}</el-breadcrumb-item>
        </el-breadcrumb>
      </div>

      <div class="manager-header-right">
        <el-dropdown style="cursor: pointer">
          <div style="padding-right: 20px; display: flex; align-items: center;">
            <img style="width: 40px; height: 40px; border-radius: 50%;" :src="data.user.avatar" alt="">
            <span style="margin-left: 5px;">{{ data.user.name }}</span><el-icon style="color: white;"><arrow-down /></el-icon>
          </div>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="router.push('/manager/person')">个人资料</el-dropdown-item>
              <el-dropdown-item @click="router.push('/manager/password')">修改密码</el-dropdown-item>
              <el-dropdown-item @click="logout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>

    </div>

    <div class="manager-body" style="display: flex">
      <div class="manager-main-left">

        <!--        默认展开，当前菜单高亮(注意在main.js里面引入router）       -->
        <el-menu
            :default-active="router.currentRoute.value.path"
            :default-openeds="['1','2']"
            router>
          <el-menu-item index="/manager/home">
            <el-icon><HomeFilled/></el-icon>
            <span>系统首页</span>
          </el-menu-item>

          <el-menu-item index="/manager/dashboard" v-if="data.user.role === 'ADMIN'">
            <el-icon><Monitor /></el-icon>
            <span>数据统计</span>
          </el-menu-item>


          <el-sub-menu index="1">
            <template #title>
              <el-icon>
                <Menu/>
              </el-icon>
              <span>信息管理</span>
            </template>
            <el-menu-item index="/manager/project">科研项目管理</el-menu-item>
            <el-menu-item index="/manager/type" v-if="data.user.role === 'ADMIN'">科研成果类型</el-menu-item>
            <el-menu-item index="/manager/process">项目过程记录</el-menu-item>
            <el-menu-item index="/manager/achievement">科研成果内容</el-menu-item>
            <el-menu-item index="/manager/teacherFeedback" v-if="data.user.role === 'TEACHER'">教师反馈模块</el-menu-item>
            <el-menu-item index="/manager/feedback" v-if="data.user.role === 'ADMIN'">教师反馈模块</el-menu-item>
            <el-menu-item index="/manager/activity" v-if="data.user.role === 'ADMIN'">科研活动管理</el-menu-item>
            <el-menu-item index="/manager/teacherActivity" v-if="data.user.role === 'TEACHER'">科研活动报名</el-menu-item>
            <el-menu-item index="/manager/apply" v-if="data.user.role === 'TEACHER'">我的活动申请</el-menu-item>
            <el-menu-item index="/manager/apply" v-if="data.user.role === 'ADMIN'">活动申请管理</el-menu-item>
            <el-menu-item index="/manager/log" v-if="data.user.role === 'ADMIN'">操作日志管理</el-menu-item>
            <el-menu-item index="/manager/notice" v-if="data.user.role === 'ADMIN'">系统公告</el-menu-item>

          </el-sub-menu>

          <el-sub-menu index="2" v-if="data.user.role === 'ADMIN'">
            <template #title>
              <el-icon>
                <Menu/>
              </el-icon>
              <span>用户管理</span>
            </template>
            <el-menu-item index="/manager/admin">管理员信息</el-menu-item>
            <el-menu-item index="/manager/teacher">教师信息</el-menu-item>
          </el-sub-menu>
        </el-menu>

      </div>
      <div class="manager-main-right">
        <RouterView @updateUser = 'updateUser'/>
      </div>

    </div>
  </div>
</template>

<script setup>

import router from "@/router/index.js";
import { ElMessage } from "element-plus";
import { reactive } from "vue";

const data = reactive({
  user: JSON.parse(localStorage.getItem("xm-user")||'{}')
})

const logout = () =>{
  localStorage.removeItem("xm-user")
  router.push('/login')
}

const updateUser = () => {
  data.user = JSON.parse(localStorage.getItem('xm-user')|| '{}')
}

// if (!data.user.id){
//   logout()
//   ElMessage("请登录！")
// }

</script>

<style scoped>
@import "@/assets/css/manager.css";

</style>