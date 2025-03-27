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
          <div style="font-size: 20px">反馈总数</div>
          <div style="font-size: 20px; margin-top: 10px; font-weight: bold">{{ data.baseData.feedback }}</div>
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
    <div id="pie" style="flex: 1; margin-right: 5px; height: 400px" class="card"></div>
    <div id="bar1" style="flex: 1; margin-left: 5px; height: 400px" class="card"></div>
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

loadBaseData()

onMounted(() => {
  loadPie();
});



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
  series: [
    {
      name: '占比数据',
      type: 'pie',
      radius: '50%',
      data: [
        { value: 1048, name: '一级' },
        { value: 735, name: '二级' },
        { value: 580, name: '三级' }
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
</script>