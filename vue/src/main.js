import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import '@/assets/css/global.css'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import zhCn from 'element-plus/es/locale/lang/zh-cn'

import { ElMessage } from 'element-plus';

// Element plus 路由守卫放main.js防止自定义样式不生效
router.beforeEach((to, from, next) => {
    const user = JSON.parse(localStorage.getItem("xm-user") || "{}");
    // 检查用户是否已登录（根据是否存在 token 或用户信息）
    if (to.meta.requiresAuth && (!user || !user.token)) {
      ElMessage.warning("请先登录！");
      return next("/login"); // 跳转到登录页
    }
    next(); // 放行
  });

const app = createApp(App)

app.use(router)
app.use(ElementPlus, {locale: zhCn})
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
    app.component(key, component)
}

app.mount('#app')


