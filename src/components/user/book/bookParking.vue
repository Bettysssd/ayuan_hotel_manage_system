<template>
<div>
    <el-button v-if="showCard" @click="dialogVisible=true" type="success">添加个人车辆信息</el-button>
</div>


  <!-- 车辆具体信息 -->
   <el-dialog v-model="dialogVisible" title="车辆具体信息"  :width="550" :height="300">
      <el-form ref="ruleFormRef" :rules="rules" :model="carAddInfo">
        <el-form-item label="车辆图片" :label-width="formLabelWidth">
        <el-tooltip class="item" effect="dark" content="点击录入车辆图片" placement="top">
          <el-upload
           class="avatar-uploader"
           action="http://localhost:8081/api/supervisor/uploadCarParkingCover"
           :show-file-list="false"
           :before-upload="beforeAvatarUpload"
           :on-success="handleAvatarSuccessUpdate" >
            <div class="image-container"> 
              <img v-if="carAddInfo.carCover" :src="carAddInfo.carCover" class="avatar" style="width: 40px; height: 40px;" />
               <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
            </div>
          </el-upload>
        </el-tooltip>
        </el-form-item>
        <el-form-item label="车辆号码" :label-width="formLabelWidth" prop="carNumber" >
         <el-input v-model="carAddInfo.carNumber" autocomplete="off"  />
        </el-form-item>
        <el-form-item label="汽车名称" :label-width="formLabelWidth"  prop="carName">
         <el-input  v-model="carAddInfo.carName" autocomplete="off" />
        </el-form-item>
      </el-form>
      <template #footer>
      <span class="dialog-footer">
      <el-button @click="dialogVisible = false">取消</el-button>
      <el-button type="primary" @click="addCarParkingInfo(ruleFormRef)">提交</el-button>
      <el-button @click="resetForm(ruleFormRef)">重置</el-button>
      </span>
      </template>
   </el-dialog>   



<div style="display: flex; justify-content: space-between;">
  <div style="width: 500px; height: 400px; margin: 10px;">
    <p style="text-align: center;">泊车员当前状态分布情况</p>
    <div ref="info" style="width: 500px; height: 350px;"></div>
  </div>
  <div style="width: 500px; height: 400px; margin-right: 170px;">
    <p style="text-align: center;">我的车辆情况</p>
    <el-form v-if="showLastParkingInfo" ref="ruleFormRef" :model="carEditInfo">
      <el-form-item label="车辆图片" :label-width="formLabelWidth">
            <img :src="carEditInfo.carCover"  style="width: 40px; height: 40px;" />
        </el-form-item>
      <el-form-item label="车辆号码" :label-width="formLabelWidth" >
        <el-input disabled v-model="carEditInfo.carNumber" autocomplete="off" />
      </el-form-item>
      <el-form-item label="汽车名称" :label-width="formLabelWidth" >
        <el-input disabled v-model="carEditInfo.carName" autocomplete="off" />
      </el-form-item>
       <el-form-item label="累计停车费用" :label-width="formLabelWidth" >
        <el-input disabled v-model="carEditInfo.cumulativeCost" autocomplete="off" />
      </el-form-item>
       <el-form-item label="泊车员" :label-width="formLabelWidth" >
        <el-input disabled v-model="carEditInfo.parkingBoy" autocomplete="off" />
      </el-form-item>
      <el-form-item label="当前车辆状态" :label-width="formLabelWidth" >
         <el-input disabled :value="carEditInfo.carStatus === 1 ? '在库中' : '已离库'" autocomplete="off" />
      </el-form-item>
       <el-form-item label="最近泊车时间" :label-width="formLabelWidth" >
           <el-input disabled  autocomplete="off" :value="carEditInfo.lastParkingTime"/> 
        </el-form-item>
      <el-form-item label="最近取车离开时间" :label-width="formLabelWidth" >
           <el-input disabled  autocomplete="off" :value="carEditInfo.lastLeaveTime"/> 
      </el-form-item>
    </el-form>
     <el-empty v-else :image-size="100" description="本次您还没有正在泊车的相关信息哦！"></el-empty>
  </div>
</div>

</template>

<script lang="ts" setup>
import { ref, onMounted, reactive, computed } from 'vue'
import { ElMessage } from 'element-plus';
import {
  Document,
  Menu as IconMenu,
  Location,
  Setting,
} from '@element-plus/icons-vue'
import { useRouter } from 'vue-router';
import axiosInstance from '@/axios/index';
import { ElMessageBox } from 'element-plus';
import router from '@/router';
import type { FormRules } from 'element-plus';
import type { FormInstance } from 'element-plus';
import * as echarts from 'echarts';

const info = ref(null);
const showLastParkingInfo = ref(false);
// 渲染饼状图--当前泊车员情况（根据状态进行统计）
async function pieCharts() {
  if (info.value) {
    var pieCharts = echarts.init(info.value);

    const freeCount = await fetchCurrentParkingCount(0);
    const busyCount = await fetchCurrentParkingCount(1);
    const dayOffCount = await fetchCurrentParkingCount(2);
    const fireCount = await fetchCurrentParkingCount(3);
 

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
            { value: freeCount, name: "空闲" },
            { value: busyCount, name: "忙碌中" },
            { value: dayOffCount, name: "休假" },
            { value: fireCount, name: "离职" },
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

// 异步获取泊车员员不同状态的数量
async function fetchCurrentParkingCount(status : number) {
    try {
    const response = await axiosInstance.get(`/api/supervisor/distinguishParkingByInStatus?status=${status}`);
    return response.data.data;
  } catch (error) {
    console.error('Error fetching user count:', error);
  }

}

const ruleFormRef = ref<FormInstance>();  
const dialogVisible = ref(false);
const total = ref(10);  // 总数据量
const dataList = ref<Car[]>([]);
const userId = ref('');
const showCard = ref(false);
const roomAvatar = ref('');
const filledOwnerIdCardCode = ref(false);

const rules = reactive<FormRules<Car>>({

  carName: [
    { required: true, message: '汽车名字不能为空', trigger: 'blur' },
    { min: 2, max: 10, message: '字符长度应该在2到10之间', trigger: 'change' },
    {
        validator: (rule, value, callback) => {
            if (value && value.includes(' ')) {
                callback(new Error('汽车名字中不能包含空格'));
            } else {
                callback();
            }
        },
        trigger: 'change'
    }
  ],
  carNumber: [
    { 
      required: true, 
      message: '请输入车牌号码', 
      trigger: 'change' 
    },
    {
    validator: (rule, value, callback) => {
      // 限定第一位为省级行政单位，第二位为英文字母，后面5位为任意数字和英文字母
      const regex = /^[京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领][A-Z][0-9A-Z]{5}$/;
      if (!value) {
          callback(new Error('请输入车牌号码'));
      } else if (!regex.test(value)) {
          callback(new Error('请输入有效的车牌号码'));
      } else {
          callback(); // 验证通过
      }
    },
    trigger: 'change'
}
  ],

})

// 业务提示框弹出
const notice = ($content : any) => {
  ElMessage({
          message: $content,
          type: 'success',
        });
}

// 图片上传校验
const beforeAvatarUpload = (file : any) => {
 const MAX_SIZE = 0.5 * 1024 * 1024; // 512KB
  const MAX_WIDTH = 900;
  const MAX_HEIGHT = 800; 

  // 检查文件大小的同步函数
  function checkFileSize(file : any) {
    if (file.size > MAX_SIZE) {
      noticeError('上传汽车图片大小不能超过 512KB!');
      return Promise.resolve(false);
    }
    return Promise.resolve(true);
  }

  // 检查图片尺寸的异步函数
  function checkImageSize(file : any) {
    return new Promise((resolve, reject) => {
      const reader = new FileReader();
      reader.onload = (e: ProgressEvent<FileReader>) => {
        const img = new Image();
        img.onload = () => {
          if (img.width > MAX_WIDTH || img.height > MAX_HEIGHT) {
            noticeError('上传汽车图片尺寸不能超过 900 x 800!');
            resolve(false);
          } else {
            resolve(true);
          }
        };
        img.onerror = (error) => {
          reject('无法读取图片文件！');
        };
        img.src = e.target.result as string;
      };
      reader.onerror = (error) => {
        reject('文件读取出错！');
      };
      reader.readAsDataURL(file);
    });
  }

  // 使用 Promise.all 来同时检查文件大小和图片尺寸
  return Promise.all([checkFileSize(file), checkImageSize(file)])
    .then(([fileSizeCheck, imageSizeCheck]) => {
      // 只有当文件大小和图片尺寸都通过检查时，才返回 true
      return fileSizeCheck && imageSizeCheck;
    })
    .catch((error) => {
      // 处理任何可能的错误
      noticeError(error.message);
      return false;
    });
}

// 新增停车信息
async function addCarParkingInfo(formEl: FormInstance | undefined) {
  if (!formEl) {
    return;
  }

  // 验证表单
  await formEl.validate(async (valid, fields) => {
    if (valid) {
      // 使用 await 等待用户确认
      try {
        await ElMessageBox.confirm('确认预定当前泊车服务吗，预定后不可取消', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        });

        // 如果用户点击“确定”，则继续执行以下代码
        carAddInfo.userId = userId.value;
        try {
          const response = await axiosInstance.post(`/api/user/bookParking`, carAddInfo);
          notice("登记个人车辆信息成功!");
          dialogVisible.value = false;
          checkMyParkingInfo();
        } catch (error) {
          // 处理请求错误
          noticeError("预定泊车服务失败，请稍后再试！");
        } finally {
          // 不管是否录入成功，都要将获得的图片路径进行删除
          carEditInfo.carCover = '';
        }
      } catch (confirmError) {
        // 用户点击“取消”或者其它错误，这里可以不做处理或者给出提示
      }
    } else {
      noticeError("表单不完整或数据格式不正确。请仔细检查，再进行提交！");
    }
  });
}

// 重置表单
const resetForm = (formEl: FormInstance | undefined) => {
  if (!formEl) return
  formEl.resetFields() 
}

interface Car {
  carCover: string
  carNumber: string
  carName: string
  carStatus: number
  cumulativeCost: number
  lastParkingTime: string
  lastLeaveTime: string
  parkingBoy: string
}

const carAddInfo = reactive({
  carCover: '',
  carName: '',
  carNumber: '',
  userId: ''
})


 // 车辆信息修改集合
let carEditInfo = reactive<Car> ( {
  carCover: '',
  carName: '',
  carNumber: '',
  carStatus: null,
  cumulativeCost: null,
  lastParkingTime: '',
  lastLeaveTime: '',
  parkingBoy: ''

})




// 业务异常提示框弹出
const noticeError = ($content : any) => {
  ElMessage({
          message: $content,
          type: 'warning',
        });
}

const query = reactive ( {
    currentPage: 1,
    size: 15,
    keyword: ''
  }  
)

// 查询当前用户的停车信息
 function checkMyParkingInfo () {
    axiosInstance.get(`/api/user/searchCurrentParkingInfo?userId=${userId.value}`)
    .then (response => {
            carEditInfo = response.data.data;
            // 用户不是第一次预定时
            if (carEditInfo != null) {
            // 当车被取走时，意味着当前订单已经结束
              if (carEditInfo.carStatus === 0) {
                showCard.value = true;
              } else {
                showCard.value = false;
              }
              showLastParkingInfo.value = true;
            } else {
              showLastParkingInfo.value = false;
            }    
    })
}





// 查看用户是否已经填完必填的个人信息
function checkUserIsFinishInfo() {
    userId.value = localStorage.getItem('userId');
    axiosInstance.get(`/api/user/checkUserInfoAlreadyFinished?userId=${userId.value}`)
    .then(response => {
        // 用户已经按照要求将必要信息填写完毕
        if (response.data.code == 1) {
            showCard.value = true;
        } else {
            noticeError(response.data.msg);
            showCard.value = false;
            return;
        }
    })
}

onMounted(() => {
  checkUserIsFinishInfo();
  pieCharts();
  checkMyParkingInfo();
});

</script>

<style scoped>

.card-header-content { /* 添加样式以使内容居中 */
  display: flex;
  justify-content: center; /* 水平居中 */
  align-items: center; /* 垂直居中 */
  height: 100%; /* 如果需要的话，确保占满整个 header 高度 */
  padding: 0 10px; /* 可选：添加一些内边距 */
}
.button-group {
  display: flex;
  align-items: center; /* 如果需要垂直居中 */
  gap: 10px; /* 设置按钮之间的间距 */
}

.hover-shadow {
  /* ... (hover 阴影样式保持不变) */
  width: 100%; /* 这里的宽度被 Flex 子项样式中的 calc 覆盖，但在 Grid 布局中不需要 */
  max-width: calc(100% / 5 - 20px); /* 在 Grid 布局中，您可以移除这个样式，因为宽度由 Grid 控制 */
}

.hover-shadow:hover {
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.19), 0 6px 6px rgba(0, 0, 0, 0.23);
}
</style>