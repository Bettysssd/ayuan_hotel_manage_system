<template>
<!--  用户车辆信息管理 -->
  <div class="right" style="margin-bottom: 10px;" >
       <el-input v-model="query.keyword" style="width: 360px" placeholder="请输入关键字" />
       <el-button  style="margin-left: 10px;" type="primary" :icon="Search" @click="fetchData(1)" >搜索</el-button>
    </div>
  <el-table :data="dataList" border style="width: 120% " height="700"  :row-class-name="tableRowRule">
    <el-table-column align="center" fixed label="车辆图片"  >
      <template #default="scope">
        <div class="image-container"  @click="showDialog(scope.row.carCover)"> 
             <img :src="scope.row.carCover"  style="width: 50px; height: 50px;" />
        </div>
      </template>
    </el-table-column>
    <el-table-column align="center" label="车主姓名" prop="carOwnerName" />
     <el-table-column align="center" label="车主联系方式" prop="carOwnerContact" />
     <el-table-column align="center" label="车辆当前状态" prop="carStatus" >
        <template #default="scope">
          <span>{{ scope.row.carStatus === 1 ? '在库中' : '已离开'}}</span>
        </template>
    </el-table-column>
     <el-table-column align="center" label="停车累计费用" prop="cumulativeCost"  />
    <el-table-column align="center" label="最近泊车时间" prop="lastParkingTime"  />
    <el-table-column align="center" label="最近离开时间" prop="lastLeaveTime"  />
    <el-table-column align="center" fixed="right" label="操作"  >
      <template #default="scope">
        <div style="display: flex; justify-content: space-between;  align-items: center">
            <el-button color="#626aef" 
            style="margin-left: 45px;"
            :dark="isDark" plain size="small" 
            @click="handleEdit(scope.$index, scope.row)"
            v-bind:disabled="scope.row.carStatus === 0">
          编辑
        </el-button>
        <el-button
          style="margin-right: 45px;"
          size="small"
          color="#001000"
          :dark="isDark" plain
          v-bind:disabled="scope.row.carStatus === 1"
          @click="deleteCurrentCarParkingInfo(scope.$index, scope.row)"
        > 删除
        </el-button>
        </div>
      </template>
    </el-table-column>
  </el-table>
    <!-- 分页 -->
   <div style="margin-top: 20px;">
        <el-pagination
      :page-size="query.size"
      :page-sizes="[10, 15, 20, 50]"
      :disabled="disabled"
      :background="background"
      layout="total, sizes, prev, pager, next, jumper"
      :total="total"
      @size-change="handleSizeChange"
      @current-change="fetchData"
    />
  </div>



 <!-- 编辑车辆累计收费 -->
   <el-dialog v-model="dialogUpdateVisible" title="更新车辆停放信息"  :width="550" :height="300">
        <el-form ref="ruleFormRef" :rules="rules" :model="carEditInfo">
        <el-form-item label="车辆图片" :label-width="formLabelWidth">
          <el-upload
           class="avatar-uploader"
           action="http://localhost:8081/api/supervisor/uploadCarParkingCover"
           :show-file-list="false"
           :before-upload="beforeAvatarUpload"
           :on-success="handleAvatarSuccessUpdate" >
            <div class="image-container"> 
              <img :src="carEditInfo.carCover"  style="width: 50px; height: 30px;" />
            </div>
          </el-upload>
        </el-form-item>
        <el-form-item label="车主姓名" :label-width="formLabelWidth" prop="carOwnerName" disa >
         <el-input v-model="carEditInfo.carOwnerName" autocomplete="off" disabled />
        </el-form-item>
        <el-form-item label="车主联系方式" :label-width="formLabelWidth" prop="carOwnerContact" >
         <el-input type="number" v-model="carEditInfo.carOwnerContact" autocomplete="off" disabled/>
        </el-form-item>
        <el-form-item label="停车累计费用" :label-width="formLabelWidth"  prop="cumulativeCost">
         <el-input type="number" v-model="carEditInfo.cumulativeCost" autocomplete="off" />
        </el-form-item>
      </el-form>
      <template #footer>
      <span class="dialog-footer">
      <el-button @click="dialogUpdateVisible = false">取消</el-button>
        <el-button type="primary" @click="updateCarParkingInfo(ruleFormRef)">提交</el-button>
      </span>
      </template>
   </el-dialog>

  <!-- 车辆图片预览 -->
   <el-dialog v-model="dialogVisible" height="800px" width="1000px" title="车辆图片预览">
    <el-form class="image-container" >
        <img :src="previewImage"  />
    </el-form>
        <template #footer>
        <span class="dialog-footer">
          <el-button  type="success" @click="dialogVisible = false">关闭</el-button>
        </span>
        </template>
    </el-dialog>

</template>

<script lang="ts" setup>

import { computed, reactive, ref, watch } from 'vue'
import { onMounted } from 'vue'
import axiosInstance from '@/axios/index';
import { ElMessage } from 'element-plus';
import type { FormRules } from 'element-plus';
import type { FormInstance } from 'element-plus';
import { ElMessageBox } from 'element-plus';
import router from '@/router';
import { errorMessages } from 'vue/compiler-sfc';



const dialogAddVisible = ref(false)
const dialogUpdateVisible = ref(false)
const total = ref(10)  // 总数据量
const ruleFormRef = ref<FormInstance>()
const dataList = ref<Car[]>([])  // 原始数据列表


const query = reactive ( {
    currentPage: 1,
    size: 15,
    keyword: ''
  }  
)

const rules = reactive<FormRules<Car>>({
  cumulativeCost: [
     { required: true, message: '停车累计费用不能为空', trigger: 'blur' },
     {
           validator: (rule, value, callback) => {
             if (value <= 0) {
                callback(new Error('停车累计费用必须为正整数'));
              } else {
                // 验证成功
                callback();
              }
            },
            trigger: 'change',
           
     },
  ]
})
const dialogVisible = ref(false)
const previewImage = ref('')
let currentMaxAmount = 0;


// 修改每页显示数据的条数
function handleSizeChange (newSize : number) {
  query.size = newSize;
  loadDataList();
}

// 图片上传校验
const beforeAvatarUpload = (file : any) => {
 const MAX_SIZE = 0.5 * 1024 * 1024; // 512KB
  const MAX_WIDTH = 900;
  const MAX_HEIGHT = 800; 

  // 检查文件大小的同步函数
  function checkFileSize(file : any) {
    if (file.size > MAX_SIZE) {
      noticeError('上传车辆图片大小不能超过 512KB!');
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
            noticeError('上传车辆图片尺寸不能超过 900 x 800!');
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

   
    

// 图片预览
function showDialog(imageURL: any) {
  previewImage.value = imageURL
  dialogVisible.value = true
}

// 判断当前车辆是否离开（黄色）
const tableRowRule = ({
  row,
}: {
  row: Car
}) => {
  if (row.carStatus === 0) {
    return 'warning-row'
  }
}

interface Car {
  parkingId: string
  carCover: string
  carOwnerName: string
  carOwnerContact: string
  carStatus: number
  cumulativeCost: number
  lastParkingTime: string
  lastLeaveTime: string
}


  // 自定义搜索
function fetchData(newPage: number) {
  query.currentPage = newPage;
   // 如果有搜索关键词，则添加到查询参数中
  loadDataList();
}

// 加载用户车辆数据
const loadDataList = async () => {
    try {  
    const response = await axiosInstance.post(`/api/supervisor/queryCarParkingList`, query);
    // 判断token是否无效或过期
    if (response.headers['notice'] === 'unable') {
        noticeError("当前token过期或无效,请重新登录");
        // 移除原有的token
        localStorage.removeItem('supervisorToken');
        // 跳转至登录页
        router.push("/");
        return;
    }
    dataList.value = response.data.data.list;
    total.value = response.data.data.total;
    if(total.value === null || total.value === 0) {
        noticeError("太难了，换个关键字再看看吧");
        return;
    }
}
    catch (error) {
    // 处理请求错误，例如打印错误信息或者更新UI等
    console.error("Error fetching data:", error);
  }
}
   

// 初始化加载页面
onMounted(loadDataList)

// 业务提示框弹出
const notice = ($content : any) => {
  ElMessage({
          message: $content,
          type: 'success',
        });
}

const noticeError = ($content : any) => {
  ElMessage({
          message: $content,
          type: 'warning',
        });
}
 // 车辆信息修改集合
  const carEditInfo = reactive ( {
  parkingId: '',
  carCover: '',
  carOwnerName: '',
  carOwnerContact: '',
  carStatus: null,
  cumulativeCost: null,
  lastParkingTime: '',
  lastLeaveTime: ''
})

// 图片上传成功后，回调函数获取上传文件的文件名
function handleAvatarSuccessUpdate(response : any, file : any ) {
    carEditInfo.carCover = "https://hotel-sys-ayuan.oss-cn-beijing.aliyuncs.com/" + file.name;
    notice("图片成功上传, 但要完整提交后才能更新图片！")  
}


// 编辑逻辑处理
const handleEdit = (index: number, row: Car) => {
  dialogUpdateVisible.value = true;
  carEditInfo.parkingId = row.parkingId;
  carEditInfo.carCover = row.carCover;
  carEditInfo.carOwnerName = row.carOwnerName;
  carEditInfo.carOwnerContact = row.carOwnerContact;
  carEditInfo.cumulativeCost = row.cumulativeCost;
  carEditInfo.carStatus = row.carStatus;
  carEditInfo.lastParkingTime = row.lastParkingTime;
  carEditInfo.lastLeaveTime = row.lastLeaveTime;
}



// 更新车辆信息
const updateCarParkingInfo = async ( formEl: FormInstance | undefined ) => {
   if (!formEl) {
      return;
     }
     await formEl.validate((valid, fields) => {
        if (valid) {
              axiosInstance.post(`/api/supervisor/updateCarParkingInfo`, carEditInfo )
            .then(response => {
              notice("变更当前车辆信息成功！");
              // 在变更后刷新数据
              dialogUpdateVisible.value = false;
              loadDataList();
          })
      }
        else {
            noticeError("表单不完整或数据格式不正确。请仔细检查，再进行提交！")
           return
        }
     })
    
  }

// 删除单一用户的车辆信息
async function deleteCurrentCarParkingInfo  (index: number, row: Car) {
  const result = await ElMessageBox.confirm('确认删除当前用户车辆信息吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  });

  if (result) {
    await axiosInstance.delete(`/api/supervisor/deleteCurrentCarParkingInfo?parkingId=${row.parkingId}`)
      .then(response => {
        notice("删除当前服务员成功！");
        // 在删除后刷新数据
        dialogUpdateVisible.value = false;
        loadDataList();
      })
  }
}
</script>


<style>
.image-container {
  text-align: center;
  max-width: 100%;
  overflow: auto;  
}
.image-container img {
  border-radius: 10px;  /* 这会让图片显示为圆角矩形 */
}

.example-pagination-block + .example-pagination-block {
  margin-top: 10px;
}
.example-pagination-block .example-demonstration {
  margin-bottom: 16px;
}
.warning-row {
  --el-table-tr-bg-color: var(--el-color-warning-light-9);
}

.right {
  display: flex;
  justify-content: flex-end;
}
</style>