<template>
  <div class="card" style="margin-bottom: 5px;">您好，{{ data.user?.name }}，欢迎使用本系统！</div>
  <div style="display: flex; gap: 15px">
    <!-- 左侧系统公告（美化后） -->
    
    <div class="card notice-container" style="flex: 1; max-height: 580px; overflow-y: auto; padding: 0 15px;">
      <div class="notice-title">系统公告</div>
      <el-timeline class="notice-timeline" :reverse="true" style="max-width: 100%;">
        <el-timeline-item
            v-for="(item, index) in data.noticeData"
            :key="index"
            :timestamp="item.time"
            placement="top"
            class="notice-timeline-item"
        >
          <div class="notice-content">
            <p>{{ item.content }}</p>
          </div>
        </el-timeline-item>
      </el-timeline>
    </div>

    <!-- 右侧科研资讯（保持不变） -->
    <div class="card" style="flex: 1; max-height: 580px; overflow: auto; padding: 0">
      <div style="font-weight: bold; color: #143281; font-size: 18px; padding: 15px 20px; position: sticky; top: 0; background: white; z-index: 1; border-bottom: 1px solid #eee">科研资讯</div>
      <div style="padding: 10px">
        <el-scrollbar>
          <div v-for="(item, index) in researchNews" :key="index"
               class="news-card"
               :style="{ borderLeft: `4px solid ${colors[index % colors.length]}` }">
            <div style="display: flex; justify-content: space-between; margin-bottom: 8px">
              <div style="font-weight: bold; color: #333; font-size: 16px">{{ item.title }}</div>
              <el-tag size="small" :type="tagTypes[index % tagTypes.length]">{{ item.institution }}</el-tag>
            </div>
            <div style="color: #888; font-size: 13px; margin-bottom: 10px">
              <i class="el-icon-time" style="margin-right: 5px"></i>{{ item.date }}
              <span style="margin-left: 10px"><i class="el-icon-collection" style="margin-right: 5px"></i>{{ item.journal }}</span>
            </div>
            <div style="color: #444; line-height: 1.6; margin-bottom: 10px">{{ item.summary }}</div>
            <div style="display: flex; gap: 10px">
              <el-button text size="small" @click="showDetail(item)">
                <i class="el-icon-document" style="margin-right: 5px"></i>研究详情
              </el-button>
              <el-button text size="small" @click="openExternal(item.link)" v-if="item.link">
                <i class="el-icon-link" style="margin-right: 5px"></i>原文
              </el-button>
            </div>
          </div>
        </el-scrollbar>
      </div>
    </div>
  </div>

  <!-- 详情弹窗保持不变 -->
  <el-dialog v-model="detailVisible" :title="currentDetail.title" width="50%">
    <div style="margin-bottom: 15px">
      <el-tag type="info" style="margin-right: 10px">{{ currentDetail.institution }}</el-tag>
      <el-tag>{{ currentDetail.date }}</el-tag>
      <el-tag type="success" style="margin-left: 10px" v-if="currentDetail.journal">{{ currentDetail.journal }}</el-tag>
    </div>
    <div style="line-height: 1.8; margin-bottom: 15px">
      <h4>研究概述</h4>
      <p>{{ currentDetail.summary }}</p>
    </div>
    <div style="line-height: 1.8" v-if="currentDetail.details">
      <h4>研究突破</h4>
      <p>{{ currentDetail.details }}</p>
    </div>
    <div style="line-height: 1.8" v-if="currentDetail.impact">
      <h4>科学影响</h4>
      <p>{{ currentDetail.impact }}</p>
    </div>
    <template #footer>
      <el-button @click="detailVisible = false">关闭</el-button>
      <el-button type="primary" @click="openExternal(currentDetail.link)" v-if="currentDetail.link">查看原文</el-button>
    </template>
  </el-dialog>
</template>


<style scoped>
.notice-container {
  background: #fff;
  border-radius: 6px;
  box-shadow: 0 4px 15px rgb(0 0 0 / 0.07);
  padding-top: 10px;
}

.notice-title {
  font-weight: 700;
  font-size: 18px;
  padding: 15px 0 20px 10px;
  position: sticky;
  top: 0;
  background: #fff;
  z-index: 10;
  color: #143281;
  user-select: none;
}

.notice-timeline {
  padding-left: 10px;
}

.notice-timeline-item {
  padding-bottom: 20px;
}

.notice-content {
  background: #f0f5ff;
  padding: 12px 16px;
  border-radius: 8px;
  box-shadow: inset 0 0 8px rgb(64 158 255 / 0.15);
  font-size: 14.5px;
  color: #303133;
  line-height: 1.5;
  transition: background-color 0.3s ease;
}

.notice-content:hover {
  background-color: #dbeafe;
}

.news-card {
  background: white;
  border-radius: 4px;
  padding: 15px;
  margin: 10px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
  transition: all 0.3s;
}

.news-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 4px 16px 0 rgba(0, 0, 0, 0.1);
}

.card {
  background: white;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
}
</style>


<script setup>
import { reactive, ref } from 'vue';
import request from '@/utils/request.js'
import { ElMessage, ElTag, ElButton, ElDialog } from 'element-plus';

const data = reactive({
  user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
  noticeData: []
})

// 顶级科研动态数据（2024-2025年最新成果）
const researchNews = [
  {
    title: "北京大学2025年16篇顶刊论文",
    date: "2025-04-05",
    institution: "北京大学",
    journal: "Nature/Science/Cell",
    summary: "2025年1-3月，北京大学在Nature、Science、Cell三大顶刊发表16篇论文，领跑全国高校。",
    details: "包括刘雄军教授团队揭示超冷费米气体中的二维非厄米趋肤效应，周欢萍和张艳锋团队在钙钛矿太阳能电池领域的突破，庞全全团队全固态锂硫电池研究等。",
    impact: "北大在量子物理、新能源材料、生物医学等多个前沿领域保持领先优势，展现了强大的原始创新能力。",
    link: "https://www.sohu.com/a/862053687_121798711"
  },
  {
    title: "嫦娥六号揭示月背28亿年前火山活动",
    date: "2024-11-15",
    institution: "中科院国家天文台",
    journal: "Nature/Science",
    summary: "嫦娥六号任务实现人类首次从月球背面采样，样品分析揭示月球背面约28亿年前仍存在年轻的岩浆活动。",
    details: "该系列成果首次填补了月球玄武岩样品在28亿年前的记录空白，挑战了传统月球热演化模型。嫦娥六号任务副总设计师李春来研究员被Nature评为2024年度十大科学人物。",
    impact: "该研究不仅深化了对月球热演化的认识，也为未来月球资源开发利用提供了重要科学依据。成果相继入选2024年十大科技新闻、英国《Physics World》十大科学突破。",
    link: "https://new.qq.com/rain/a/20250327A05UKS00"
  },
  {
    title: "大规模光计算芯片实现智能推理与训练",
    date: "2024-09-20",
    institution: "清华大学",
    journal: "Nature/Science",
    summary: "研制国际首款大规模通用智能光计算芯片'太极'，实现每焦耳160万亿次运算的系统级能量效率。",
    details: "团队相继在Science和Nature报道了'太极'光芯片和'太极-II'光训练芯片。'太极-II'首次实现全前向模式学习方法，支撑智能系统的高效精准光训练，为AI大模型探索了光训练新路径。",
    impact: "该技术为突破传统电子计算芯片的能效瓶颈提供了全新方案，入选2024年度中国科学十大进展。戴琼海院士团队还推出了RUSH3D介观活体显微系统，实现细胞交互行为全景观测。",
    link: "https://new.qq.com/rain/a/20250327A0733400"
  },
  {
    title: "原子级纳米激光器与光频相控阵",
    date: "2024-07-10",
    institution: "北京大学",
    journal: "Nature",
    summary: "实现模式体积最小的激光器——奇点介电纳米激光器，首次将激光器特征尺度推进至原子级别。",
    details: "马仁敏团队提出了奇点色散方程，构建了介电体系突破衍射极限的理论框架。基于纳米激光器构建的可重构光频相控阵，使激光器阵列可同步启动，生成任意相干激射图案。",
    impact: "该纳米激光器具有小体积、低能耗等特点，在信息技术、传感探测等领域应用前景广阔，入选2024年度中国科学十大进展。",
    link: "https://new.qq.com/rain/a/20250327A0733400"
  },
  {
    title: "自旋超固态巨磁卡效应与极低温制冷",
    date: "2024-06-18",
    institution: "中科院",
    journal: "Nature",
    summary: "在钴基三角晶格量子磁性材料中发现兼具固体和超流体特征的新奇量子物态——自旋超固态。",
    details: "这是首次在实际固体材料中给出超固态存在的可靠实验证据。研究发现自旋超固态可导致巨磁卡效应，并基于该效应设计出新型低温制冷器件，实现亚开温区无液氦极低温固体制冷。",
    impact: "该技术为破解我国氦资源短缺问题提供了新解决方案，入选中国两院院士评选的2024年十大科技进展新闻。",
    link: "https://new.qq.com/rain/a/20250327A0733400"
  },
  {
    title: "凝聚态物质中引力子模的实验发现",
    date: "2024-05-22",
    institution: "南京大学",
    journal: "Nature",
    summary: "在分数量子霍尔液体中观察到具有引力子特征的集体激发——引力子模，并发现其具有手性。",
    details: "杜灵杰团队通过自主设计、组装的极低温强磁场共振非弹性偏振光散射系统，基于砷化镓量子阱实现了这一突破。这是世界上首次在真实系统中观察到引力子在凝聚态物质中的'投影'。",
    impact: "该发现为在凝聚态物质中探索量子引力相关物理问题开辟了全新视野，入选2024年度中国科学十大进展。",
    link: "https://www.sohu.com/a/854895956_121956424"
  },
  {
    title: "室温可充钙-氧电池开发",
    date: "2024-02-07",
    institution: "南京大学/复旦大学",
    journal: "Nature",
    summary: "创造新型钙-氧气电池，首次实现室温条件下电化学充放电，700次循环稳定运行。",
    details: "周豪慎教授与彭慧胜院士团队合作开发的这款电池展现出优异的安全性与低成本优势，能量密度达到锂离子电池的1.5倍。",
    impact: "该技术为下一代高能量密度、低成本储能系统提供了新思路，有望应用于大规模能源存储。",
    link: "https://www.sohu.com/a/854895956_121956424"
  },
  {
    title: "全钙钛矿叠层组件效率28.2%",
    date: "2024-10-14",
    institution: "南京大学",
    journal: "Nature",
    summary: "谭海仁团队在全钙钛矿叠层太阳电池领域取得突破，稳态光电转换效率达28.2%。",
    details: "该团队通过界面工程和组分优化，解决了窄带隙钙钛矿相分离问题，创造了全钙钛矿叠层电池效率新纪录。",
    impact: "这一成果推动了钙钛矿光伏技术的产业化进程，为可再生能源发展提供了新选择。",
    link: "https://www.sohu.com/a/854895956_121956424"
  }
]

// 当前显示的详情
const currentDetail = ref({})
const detailVisible = ref(false)

// 样式配置
const colors = ['#409EFF', '#67C23A', '#E6A23C', '#F56C6C', '#909399']
const tagTypes = ['', 'success', 'warning', 'danger', 'info']

const showDetail = (item) => {
  currentDetail.value = item
  detailVisible.value = true
}

const openExternal = (url) => {
  if(url) window.open(url, '_blank')
}

const noticeData = () => {
  request.get('/notice/selectAll').then( res => {
    if ( res.code === '200'){
      data.noticeData = res.data
    }
    else{
      ElMessage.error(res.msg)
    }
  })
}

noticeData()
</script>

<!--<style scoped>-->
<!--/* 容器布局 */-->
<!--.container {-->
<!--  display: flex;-->
<!--  gap: 15px;-->
<!--  flex-wrap: wrap;-->
<!--}-->

<!--/* 通用卡片样式 */-->
<!--.card {-->
<!--  background: #fff;-->
<!--  border-radius: 6px;-->
<!--  box-shadow: 0 4px 12px rgb(0 0 0 / 0.06);-->
<!--}-->

<!--/* 欢迎条 */-->
<!--.welcome-card {-->
<!--  padding: 12px 20px;-->
<!--  font-size: 16px;-->
<!--  font-weight: 600;-->
<!--  color: #606266;-->
<!--  margin-bottom: 10px;-->
<!--}-->

<!--/* 左侧系统公告 */-->
<!--.notice-container {-->
<!--  flex: 1 1 400px;-->
<!--  max-height: 580px;-->
<!--  overflow-y: auto;-->
<!--  padding: 0 20px 20px 20px;-->
<!--  position: relative;-->
<!--}-->

<!--/* 固定标题栏 */-->
<!--.notice-header {-->
<!--  position: sticky;-->
<!--  top: 0;-->
<!--  background: #fff;-->
<!--  font-weight: 700;-->
<!--  font-size: 20px;-->
<!--  padding: 20px 0 15px 0;-->
<!--  border-bottom: 1px solid #eaeaea;-->
<!--  z-index: 10;-->
<!--  box-shadow: 0 2px 6px rgb(0 0 0 / 0.05);-->
<!--}-->

<!--/* 时间轴样式调整 */-->
<!--.notice-timeline {-->
<!--  max-width: 100%;-->
<!--  margin-left: 10px;-->
<!--}-->

<!--.timeline-item {-->
<!--  padding-bottom: 20px;-->
<!--  margin-right: 10px;-->
<!--}-->

<!--/* 时间戳样式 */-->
<!--.timeline-item::v-deep(.el-timeline-item__timestamp) {-->
<!--  font-size: 13px;-->
<!--  color: #909399;-->
<!--  width: 100px;-->
<!--  left: -110px;-->
<!--  text-align: right;-->
<!--  user-select: none;-->
<!--}-->

<!--/* 时间轴内容块 */-->
<!--.timeline-content {-->
<!--  background: #f5f7fa;-->
<!--  padding: 12px 18px;-->
<!--  border-radius: 8px;-->
<!--  box-shadow: inset 0 0 6px rgb(0 0 0 / 0.03);-->
<!--  font-size: 14px;-->
<!--  color: #606266;-->
<!--  line-height: 1.5;-->
<!--  white-space: pre-line;-->
<!--  word-break: break-word;-->
<!--}-->

<!--/* 右侧科研资讯 */-->
<!--.research-container {-->
<!--  flex: 1 1 600px;-->
<!--  max-height: 580px;-->
<!--  overflow-y: auto;-->
<!--  padding: 0;-->
<!--}-->

<!--.research-header {-->
<!--  font-weight: 700;-->
<!--  font-size: 20px;-->
<!--  padding: 20px;-->
<!--  position: sticky;-->
<!--  top: 0;-->
<!--  background: #fff;-->
<!--  border-bottom: 1px solid #eee;-->
<!--  z-index: 10;-->
<!--}-->

<!--.research-content {-->
<!--  padding: 10px;-->
<!--}-->

<!--/* 新闻卡片 */-->
<!--.news-card {-->
<!--  background: white;-->
<!--  border-radius: 6px;-->
<!--  padding: 15px 20px;-->
<!--  margin: 12px 10px;-->
<!--  box-shadow: 0 2px 12px rgb(0 0 0 / 0.05);-->
<!--  transition: all 0.3s;-->
<!--}-->

<!--.news-card:hover {-->
<!--  transform: translateY(-3px);-->
<!--  box-shadow: 0 6px 20px rgb(0 0 0 / 0.12);-->
<!--}-->

<!--.news-header {-->
<!--  display: flex;-->
<!--  justify-content: space-between;-->
<!--  margin-bottom: 8px;-->
<!--  align-items: center;-->
<!--}-->

<!--.news-title {-->
<!--  font-weight: 700;-->
<!--  color: #303133;-->
<!--  font-size: 16px;-->
<!--}-->

<!--.news-meta {-->
<!--  color: #909399;-->
<!--  font-size: 13px;-->
<!--  margin-bottom: 10px;-->
<!--}-->

<!--.news-meta i {-->
<!--  margin-right: 5px;-->
<!--}-->

<!--.news-meta span {-->
<!--  margin-left: 15px;-->
<!--}-->

<!--.news-summary {-->
<!--  color: #606266;-->
<!--  line-height: 1.6;-->
<!--  margin-bottom: 10px;-->
<!--}-->

<!--.news-actions {-->
<!--  display: flex;-->
<!--  gap: 10px;-->
<!--}-->

<!--/* 详情弹窗 */-->
<!--.detail-tags {-->
<!--  margin-bottom: 15px;-->
<!--}-->

<!--.detail-section {-->
<!--  line-height: 1.8;-->
<!--  margin-bottom: 15px;-->
<!--}-->

<!--.detail-section h4 {-->
<!--  font-weight: 600;-->
<!--  margin-bottom: 8px;-->
<!--  color: #409EFF;-->
<!--}-->

<!--/* 滚动条美化 */-->
<!--.notice-container::-webkit-scrollbar,-->
<!--.research-container::-webkit-scrollbar {-->
<!--  width: 8px;-->
<!--}-->

<!--.notice-container::-webkit-scrollbar-thumb,-->
<!--.research-container::-webkit-scrollbar-thumb {-->
<!--  background-color: rgba(64, 158, 255, 0.3);-->
<!--  border-radius: 4px;-->
<!--}-->

<!--.notice-container::-webkit-scrollbar-track,-->
<!--.research-container::-webkit-scrollbar-track {-->
<!--  background: transparent;-->
<!--}-->

<!--@media (max-width: 900px) {-->
<!--  .container {-->
<!--    flex-direction: column;-->
<!--  }-->
<!--  .notice-container,-->
<!--  .research-container {-->
<!--    max-height: none;-->
<!--    flex: none;-->
<!--  }-->
<!--}-->
<!--</style>-->