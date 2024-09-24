<template>
<div style="display: flex; justify-content: center; width: 100%; flex-wrap: wrap;">
    <div style="width: 500px; height: 400px; margin: 10px;">
        <p style="text-align: center;">用户群体分布情况</p>
        <div ref="info" style="width: 500px; height: 350px;"></div>
    </div>
    <div style="width: 500px; height: 400px; margin: 10px;">
        <p style="text-align: center;">服务员群体人数分布情况</p>
        <div ref="info1" style="width: 500px; height: 350px;"></div>
    </div>
    <div style="width: 500px; height: 400px; margin: 10px;">
        <p style="text-align: center;">客房当前基本情况</p>
        <div ref="info2" style="width: 500px; height: 350px;"></div>
    </div>
    <div style="width: 500px; height: 400px; margin-right:50px;">
        <p style="text-align: center;">用户消费top5情况</p>
        <div ref="info3" style="width: 500px; height: 300px;"></div>
    </div>
   <div style="width: 500px; height: 400px; margin-left: 50px;">
        <p style="text-align: center;">用户欠费top5情况</p>
        <div ref="info4" style="width: 500px; height: 300px;"></div>
   </div>
</div>
</template>
<script setup>
import { onMounted, ref } from 'vue';
import * as echarts from 'echarts';
import axiosInstance from '@/axios/index';
const info = ref(null);  // 这是你用于存储 ECharts DOM 元素的 ref
const info1 = ref(null);
const info2 = ref(null);
const info3 = ref(null);
const info4 = ref(null);
// 异步获取用户数量
async function fetchCurrentUserCount(type, status) {
  try {
    const response = await axiosInstance.get(`/api/user/distinguishUserByCondition?type=${type}&status=${status}`);
    return response.data.data;
  } catch (error) {
    console.error('Error fetching user count:', error);
  }
}

// 异步获取不同类型的服务员数量
async function fetchCurrentWaiterCount(types) {
    try {
    const typeStr = types.join(',');
    const response = await axiosInstance.get(`/api/supervisor/distinguishWaiterByType?types=${typeStr}`);
    return response.data.data;
  } catch (error) {
    console.error('Error fetching user count:', error);
  }

}

// 异步获取不同类型的不同状态下的房间的数量
async function fetchCurrentRoomCount(types, status) {
    try {
    const typeStr = types.join(',');
    const response = await axiosInstance.get(`/api/supervisor/distinguishVariableRoom?types=${typeStr}&status=${status}`);
    return response.data.data;
  } catch (error) {
    console.error('Error fetching user count:', error);
  }

}

const userPayInfo = ref([]);
// 异步获取不同状态下的用户消费情况
async function fetchCurrentPayStatusUsersCount(status) {
    try {
    const response = await axiosInstance.get(`/api/user/getUserDifferentPaySituation?status=${status}`);
    return response.data.data;
  } catch (error) {
    console.error('Error fetching user count:', error);
  }
}

// 用户消费top5
async function barChartsThree() {
    if (info3.value) {
        var barChartsThree = echarts.init(info3.value);
        userPayInfo.value = await fetchCurrentPayStatusUsersCount(1);
        userPayInfo.value.sort((a, b) => a.fee - b.fee);
        var userNames = userPayInfo.value.map(userInfo  => userInfo.userName); 
        var userFee = userPayInfo.value.map(userInfo  => userInfo.fee); 
        console.log(userNames);
        console.log(userFee);
        var option = {
        tooltip: {
            trigger: 'axis',
            axisPointer: {
            type: 'shadow'
            }
        },
        legend: {},
        grid: {
            left: '3%',
            right: '4%',
            bottom: '3%',
            containLabel: true
        },
        xAxis: {
            type: 'value',
            boundaryGap: [0, 0.01]
        },
        yAxis: {
            type: 'category',
            data: userNames
        },
        series: [
            
            {
            name: '用户消费前5名',
            type: 'bar',
            data: userFee,
            itemStyle: {
                color: '#4CAF50' // 例如，使用浅绿色
            },
            }
        ]
   };
     barChartsThree.setOption(option);
  }
}
// 用户欠费top5
async function barChartsFour() {
    if (info4.value) {
        var barChartsFour = echarts.init(info4.value);
        userPayInfo.value = await fetchCurrentPayStatusUsersCount(0);
        userPayInfo.value.sort((a, b) => a.fee - b.fee);
        var userNames = userPayInfo.value.map(userInfo  => userInfo.userName); 
        var userFee = userPayInfo.value.map(userInfo  => userInfo.fee); 
        var option = {
        tooltip: {
            trigger: 'axis',
            axisPointer: {
            type: 'shadow'
            }
        },
        legend: {},
        grid: {
            left: '3%',
            right: '4%',
            bottom: '3%',
            containLabel: true
        },
        xAxis: {
            type: 'value',
            boundaryGap: [0, 0.01]
        },
        yAxis: {
            type: 'category',
            data: userNames
        },
        series: [
            
            {
            name: '用户欠费前5名',
            type: 'bar',
            data: userFee,
            itemStyle: {
                color: '#9C27B0' // 例如，使用浅绿色
            },
            }
        ]
 };
  barChartsFour.setOption(option);
    }
}
// 多层柱状图--显示客房当前状态
async function barChartsTwo() {
     if (info2.value) {
        var barChartsTwo = echarts.init(info2.value);
         // 等待异步数据加载
        const roomData = await Promise.all([
            fetchCurrentRoomCount([1, 3], 0),
            fetchCurrentRoomCount([1, 3], 1),
            fetchCurrentRoomCount([1, 3], 2),
            fetchCurrentRoomCount([1, 3], 3),
            fetchCurrentRoomCount([2, 4], 0),
            fetchCurrentRoomCount([2, 4], 1),
            fetchCurrentRoomCount([2, 4], 2),
            fetchCurrentRoomCount([2, 4], 3),
            fetchCurrentRoomCount([5], 0),
            fetchCurrentRoomCount([5], 1),
            fetchCurrentRoomCount([5], 2),
            fetchCurrentRoomCount([5], 3)
        ]);
        var option = {
                legend: {},
                tooltip: {},
                
                dataset: {
                    source: [
                    ['product', '空闲', '已预定', '停用维护中', '已入住'],
                     ['单人间', ...roomData.slice(0, 4)],
                     ['双人间', ...roomData.slice(4, 8)],
                     ['家庭间', ...roomData.slice(8, 12)]
                    ]
                },
                xAxis: { type: 'category',
                    data: ['单人间', '双人间', '家庭间']
                 },
                yAxis: {},
                // Declare several bar series, each will be mapped
                // to a column of dataset.source by default.
                series: [{ type: 'bar' }, { type: 'bar' }, { type: 'bar' }, { type: 'bar' }]
        };
            barChartsTwo.setOption(option);
     }
}

// 渲染柱状图--服务员群体（根据岗位进行统计）
async function barCharts() {
      if (info1.value) {
    var barChart = echarts.init(info1.value);

    const catering = await fetchCurrentWaiterCount([4,5,6,7]);
    const comunication = await fetchCurrentWaiterCount([10]);
    const concierge = await fetchCurrentWaiterCount([3,8]);
    const deliver = await fetchCurrentWaiterCount([1]);
    const parking = await fetchCurrentWaiterCount([2]);
    const reservation = await fetchCurrentWaiterCount([9]);
    const room = await fetchCurrentWaiterCount([0]);


    var total = catering + deliver + room + concierge + reservation + parking + comunication; //总人数
   var option = {
            xAxis: {
                type: 'category',
                data: ['餐饮部', '配送部', '套房部', '礼宾部', '前厅部', '停车场管理部', '总机与通讯部'],
                 axisLabel: {
                    interval: 0, // 确保所有标签都显示
                    rotate: 45 // 将标签旋转 45 度
                }
            },
            yAxis: {
                type: 'value'
            },
            tooltip: {
                trigger: 'axis', // 触发类型，默认数据项触发，可选为：'item' | 'axis'
                formatter: function (params) {
                    // params 是一个数组，包含当前悬停点的所有信息
                    var value = params[0].value;
                    var percent = (value / total * 100).toFixed(2) + '%'; // 计算百分比并保留两位小数
                    return `${params[0].name}<br/>人数：${value}<br/>比例：${percent}`;
                }
            },
            series: [
                {
                data: [
                    {
                        value: catering,
                        itemStyle: {
                            color: '#FF0000'
                        },
                        name: "餐饮部"
                    },
                   {
                        value: deliver,
                        itemStyle: {
                            color: '#FF7F00'
                        },
                        name: "配送部"
                    },
                    {
                        value: room,
                        itemStyle: {
                            color: '#FFFF00'
                        },
                        name: "套房部"
                    },
                    {
                        value: concierge,
                        itemStyle: {
                            color: '#00FF00'
                        },
                        name: "礼宾部"
                    },
                    {
                        value: reservation,
                        itemStyle: {
                            color: '#00FFFF'
                        },
                        name: "前厅部"
                    },
                    {
                        value: parking,
                        itemStyle: {
                            color: '#0000FF'
                        },
                        name: "停车场管理部"
                    },
                    {
                        value: comunication,
                        itemStyle: {
                            color: '#FF00FF'
                        },
                        name: "总机与通讯部"
                    }
                ],
                type: 'bar'
                }
            ]
        };

         barChart.setOption(option);
    }
}

// 渲染饼状图--用户群体（根据性别进行统计）
async function pieCharts() {
  if (info.value) {
    var pieCharts = echarts.init(info.value);

    const maleCount = await fetchCurrentUserCount(0,0);
    const femaleCount = await fetchCurrentUserCount(1,0);
    const normalCount = await fetchCurrentUserCount(1,1);
    const bannedCount = await fetchCurrentUserCount(0,1);
    const paid =  await fetchCurrentUserCount(1,2);
    const unpay =  await fetchCurrentUserCount(1,2);

    var option = {
      legend: {
        orient: "vertical",
        x: "left",
        data: ["男性用户", "女性用户","账号正常", "账号禁用","未付款","已付款"]
      },
      series: [
        {
          type: "pie",
          radius: "50%",
          data: [
            { value: maleCount, name: "男性用户" },
            { value: femaleCount, name: "女性用户" },
            { value: normalCount, name: "账号正常" },
            { value: bannedCount, name: "账号禁用" },
             { value: paid, name: "未付款" },
            { value: unpay, name: "已付款" },
          ],
          emphasis: {
            itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: "rgba(0, 0, 0, 0.5)",
            color: '#FF6347', // 你可以选择一个鲜艳的颜色来突出显示
            },
            label: {
              show: true, // 显示标签
              formatter: '{b}: {c} ({d}%)', // 自定义标签格式，其中 {b} 是数据名，{c} 是数据值，{d} 是百分比
              position: 'inside' // 标签位置，可以是 'inside'、'outside'、'left'、'right' 等
            }
          },
          color: ["#516b91", "#59c4e6","#FF3450","#A9A9A9","#FF5487","#00FF03"] // 只需两种颜色
        }
      ]
    };

    pieCharts.setOption(option);
  }
}
onMounted(() => {
  pieCharts();
  barCharts();
  barChartsTwo();
  barChartsThree();
  barChartsFour();
});
</script>


<style>
/* 可以在这里添加一些样式 */
</style>