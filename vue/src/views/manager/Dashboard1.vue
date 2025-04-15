<template>
  <div>
    <div style="display: flex">
      <div style="flex: 1; margin-right: 5px; display: flex; height: 120px; align-items: center" class="card">
        <div style="flex: 1; text-align: center">
          <img src="@/assets/imgs/科研项目数.png" alt="" style="width: 70px; height: 60px">
        </div>
        <div style="flex: 1">
          <div style="font-size: 20px">科研项目数</div>
          <div style="font-size: 20px; margin-top: 10px; font-weight: bold">{{ data.baseData.project }}</div>
        </div>
      </div>
      <div style="flex: 1; margin: 0 5px; display: flex; height: 120px; align-items: center" class="card">
        <div style="flex: 1; text-align: center">
          <img src="@/assets/imgs/反馈.png" alt="" style="width: 70px; height: 60px">
        </div>
        <div style="flex: 1">
          <div style="font-size: 20px">科研成果数</div>
          <div style="font-size: 20px; margin-top: 10px; font-weight: bold">{{ data.baseData.achievement }}</div>
        </div>
      </div>
      <div style="flex: 1; margin: 0 5px; display: flex; height: 120px; align-items: center" class="card">
        <div style="flex: 1; text-align: center">
          <img src="@/assets/imgs/活动.png" alt="" style="width: 70px; height: 60px">
        </div>
        <div style="flex: 1">
          <div style="font-size: 20px">活动总数</div>
          <div style="font-size: 20px; margin-top: 10px; font-weight: bold">{{ data.baseData.activity }}</div>
        </div>
      </div>
      <div style="flex: 1; margin-left: 5px; display: flex; height: 120px; align-items: center" class="card">
        <div style="flex: 1; text-align: center">
          <img src="@/assets/imgs/教师.png" alt="" style="width: 70px; height: 60px">
        </div>
        <div style="flex: 1">
          <div style="font-size: 20px">教师总数</div>
          <div style="font-size: 20px; margin-top: 10px; font-weight: bold">{{ data.baseData.teacher }}</div>
        </div>
      </div>
    </div>
  </div>

  <div style="margin-top: 10px; display: flex">
    <div id="bar1" style="flex: 1; margin-right: 5px; height: 400px" class="card"></div>
    <div id="pie" style="flex: 1; margin-left: 5px; height: 400px" class="card"></div>
  </div>

  <div style="margin-top: 10px; display: flex">
    <div id="bar2" style="flex: 1; margin-right: 5px; height: 400px" class="card"></div>
    <div id="line" style="flex: 1; margin-left: 5px; height: 400px" class="card"></div>
  </div>
</template>

<script setup>

import {reactive} from "vue";
import request from "@/utils/request.js";
import {ElMessage} from "element-plus";
import * as echarts from "echarts";
import { onMounted } from "vue";


const data = reactive({
  user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
  baseData: {}
})

const loadBaseData = () => {
  request.get('/dashboard/base').then(res => {
    if (res.code === '200') {
      data.baseData = res.data
    } else {
      ElMessage.error(res.msg)
    }
  })
}

const loadPie = () => {
  echarts.dispose(document.getElementById('pie'))
  request.get('/dashboard/pie').then(res => {
    if (res.code === '200') {
      let chartDom = document.getElementById('pie')
      let myChart = echarts.init(chartDom)
      pieOptions.series[0].data = res.data
      myChart.setOption(pieOptions)
    }
  })
}

const loadBar1 = () => {
  echarts.dispose(document.getElementById('bar1'))
  request.get('dashboard/bar1').then(res => {
    if (res.code === '200'){
      let chartDom = document.getElementById('bar1')
      let myChart = echarts.init(chartDom)
      bar1Options.xAxis.data = res.data.xAxis
      bar1Options.series[0].data = res.data.yAxis
      myChart.setOption(bar1Options)
    }
  })
}

const loadBar2 = () => {
  echarts.dispose(document.getElementById('bar2'))
  request.get('/dashboard/bar2').then(res => {
    if (res.code === '200') {
      let chartDom = document.getElementById('bar2')
      let myChart = echarts.init(chartDom)
      bar2Options.xAxis.data = res.data.xAxis
      bar2Options.series[0].data = res.data.yAxis
      myChart.setOption(bar2Options)
    }
  })
}
const loadLine = () => {
  echarts.dispose(document.getElementById('line'))
  request.get('/dashboard/line').then(res => {
    if (res.code === '200') {
      let chartDom = document.getElementById('line')
      let myChart = echarts.init(chartDom)
      lineOptions.xAxis.data = res.data.xAxis
      lineOptions.series[0].data = res.data.yAxis
      myChart.setOption(lineOptions)
    }
  })
}
loadBaseData()

onMounted(() => {
  loadPie()
  loadBar1()
  loadBar2()
  loadLine()
});



  // 导入和响应式数据部分保持不变

  // 更新后的图表选项
  let pieOptions = {
  title: {
  text: '系统中不同等级的项目等级',
  subtext: '统计维度：科研等级',
  left: 'center'
},
  tooltip: {
  trigger: 'item'
},
  legend: {
  orient: 'vertical',
  left: 'left'
},
  color: ['#cc3f5f', '#f27852', '#ffb750'],
  series: [
{
  name: '占比数据',
  type: 'pie',
  radius: '50%',
  data: [
    { value: 1048, name: '一级', itemStyle: { color: '#262581' } },
    { value: 735, name: '二级', itemStyle: { color: '#00bac0' } },
    { value: 580, name: '三级', itemStyle: { color: '#0076be' } }
  ],
  emphasis: {
  itemStyle: {
  shadowBlur: 10,
  shadowOffsetX: 0,
  shadowColor: 'rgba(0, 0, 0, 0.5)'
}
}
}
  ]
};

  // 柱状图1 - 学术活动数量
  let bar1Options = {
  title: {
  text: '系统中教师申请的学术活动数量',
  subtext: '统计维度：活动名称',
  left: 'center'
},
  color: ['#6B8EFF', '#7D9EFF', '#8FAEFF', '#A1BEFF', '#B3CEFF', '#C5DEFF'],
  xAxis: {
  type: 'category',
  data: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'],
  axisLabel: {
  interval: 0,
  rotate: 15,
  textStyle: {
  color: '#666666',
  fontSize: 10,
}
},
},
  yAxis: {
  type: 'value'
},
  tooltip: {
  trigger: 'item'
},
  series: [
{
  data: [120, 200, 150, 80, 70, 110, 130],
  type: 'bar',
  itemStyle: {
    color: function(params) {
      const colors = ['#6B8EFF', '#7D9EFF', '#8FAEFF', '#A1BEFF', '#B3CEFF', '#C5DEFF'];
      return colors[params.dataIndex % colors.length];
    }
  }
}
  ]
}

  // 柱状图2 - 科研成果数
  let bar2Options = {
  title: {
  text: '系统中不同成果类型下的科研成果数（柱状图）',
  subtext: '统计维度：成果类型',
  left: 'center'
},
  // color: ['#FF9E4F', '#4682B4', '#FFB482', '#5E9ED6', '#FFCA9E', '#76B1E5'],
  xAxis: {
  type: 'category',
  data: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'],
  axisLabel: {
  interval: 0,
  rotate: 15,
  textStyle: {
  color: '#666666',
  fontSize: 10
}
},
},
  yAxis: {
  type: 'value'
},
  tooltip: {
  trigger: 'item'
},
  series: [
{
  data: [120, 200, 150, 80, 70, 110, 130],
  type: 'bar',
  itemStyle: {
  color: function(params) {
    const colors = ['#193fa6', '#0061c0', '#007dc7', '#0096bd', '#00acab', '#12bf98'];
    return colors[params.dataIndex % colors.length];
  }
}
}
  ]
}

  // 折线图 - 反馈数量
  let lineOptions = {
  title: {
  text: '系统最近7天的反馈数量',
  subtext: '',
  left: 'center'
},
  color: ['#EE6666'],
  xAxis: {
  type: 'category',
  data: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun']
},
  yAxis: {
  type: 'value'
},
  tooltip: {
  trigger: 'item'
},
  series: [
{
  data: [120, 200, 150, 80, 70, 110, 130],
  type: 'line',
  smooth: true,
  lineStyle: {
  width: 4,
  color: '#EE6666'
},
  itemStyle: {
  color: '#EE6666',
  borderWidth: 2,
  borderColor: '#fff'
},
  areaStyle: {
  color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
{ offset: 0, color: 'rgba(238, 102, 102, 0.5)' },
{ offset: 1, color: 'rgba(238, 102, 102, 0.1)' }
  ])
}
}
  ]
}
</script>