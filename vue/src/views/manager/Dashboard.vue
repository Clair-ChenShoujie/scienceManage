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
    <div id="wordcloud" style="flex: 1; margin-right: 5px; height: 400px" class="card"></div>
    <div id="radar" style="flex: 1; margin-left: 5px; height: 400px" class="card"></div>
  </div>
  <div style="margin-top: 10px; display: flex">
    <div id="heatmap" style="flex: 1; margin-right: 5px; height: 400px" class="card"></div>
    <div id="line" style="flex: 1; margin-left: 5px; height: 400px" class="card"></div>
  </div>

</template>

<script setup>

import {reactive} from "vue";
import request from "@/utils/request.js";
import {ElMessage} from "element-plus";
import * as echarts from "echarts";
import 'echarts-wordcloud';
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

const loadRadar = () => {
  echarts.dispose(document.getElementById('radar'));
  request.get('/dashboard/radar').then(res => {
    if (res.code === '200') {
      let chartDom = document.getElementById('radar');
      let myChart = echarts.init(chartDom);
      radarOptions.radar.indicator = res.data.indicator.map(key => ({ name: key, max: Math.max(...res.data.values) + 2 }));
      radarOptions.series[0].data[0].value = res.data.values;
      myChart.setOption(radarOptions);
    }
  });
};

const loadHeatmap = () => {
  echarts.dispose(document.getElementById('heatmap'));
  request.get('/dashboard/heatmap').then(res => {
    if (res.code === '200') {
      let chartDom = document.getElementById('heatmap');
      let myChart = echarts.init(chartDom);
      heatmapOptions.xAxis.data = res.data.xAxis;
      heatmapOptions.yAxis.data = res.data.yAxis;
      heatmapOptions.series[0].data = res.data.data;
      myChart.setOption(heatmapOptions);
    }
  });
};

const loadWordCloud = () => {
  echarts.dispose(document.getElementById('wordcloud'));
  request.get('/dashboard/wordCloud').then(res => {
    if (res.code === '200') {
      let chartDom = document.getElementById('wordcloud');
      let myChart = echarts.init(chartDom);
      wordCloudOptions.series[0].data = res.data;
      myChart.setOption(wordCloudOptions);
    }
  });
};


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
  loadRadar();
  loadHeatmap();
  loadWordCloud();
  loadLine();
});




  // 导入和响应式数据部分保持不变

// 雷达图
let radarOptions = {
  title: {
    text: '项目等级分布（雷达图）',
    left: 'center'
  },
  tooltip: {},
  radar: {
    indicator: [],
    center: ['50%', '55%'],
    radius: 120
  },
  series: [{
    name: '项目数',
    type: 'radar',
    data: [
      {
        value: [],
        name: '等级项目数'
      }
    ],
    areaStyle: {
      color: 'rgba(0,112,192,0.2)'
    }
  }]
}


// 词云图
let wordCloudOptions = {
  title: {
    text: '科研成果类型词云',
    left: 'center'
  },
  tooltip: {},
  series: [{
    type: 'wordCloud',
    gridSize: 8,
    sizeRange: [18, 60],
    rotationRange: [-90, 90],
    shape: 'circle',
    width: '100%',
    height: '100%',
    textStyle: {
      normal: {
        color: function () {
          return 'rgb(' + [Math.round(Math.random() * 160), Math.round(Math.random() * 160), Math.round(Math.random() * 160)].join(',') + ')';
        }
      }
    },
    data: []
  }]
}


// 热力图
let heatmapOptions = {
  title: {
    text: '教师-活动申请热力图',
    left: 'center'
  },
  tooltip: {},
  xAxis: {
    type: 'category',
    data: [],
    splitArea: { show: true }
  },
  yAxis: {
    type: 'category',
    data: [],
    splitArea: { show: true }
  },
  visualMap: {
    min: 0,
    max: 10,
    calculable: true,
    orient: 'horizontal',
    left: 'center',
    bottom: '5%'
  },
  series: [{
    name: '申请次数',
    type: 'heatmap',
    data: [],
    label: { show: true },
    emphasis: {
      itemStyle: {
        shadowBlur: 10,
        shadowColor: 'rgba(0,0,0,0.6)'
      }
    }
  }]
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