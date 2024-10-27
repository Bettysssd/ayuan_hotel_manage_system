<template>
<!--  停车场管理部 -->
  <div class="right" style="margin-bottom: 10px;" >
      <el-button  style="margin-right: 1200px;" type="success" :icon="Search" @click="clearLastCache" >录入停车场管理部服务员</el-button>
   <el-input v-model="query.keyword" style="width: 360px" placeholder="请输入关键字" />
       <el-button  style="margin-left: 10px;" type="primary" :icon="Search" @click="fetchData(1)" >搜索</el-button>
    </div>
  <el-table :data="dataList" border style="width: 100% " height="700"  :row-class-name="tableRowRule">
    <el-table-column align="center" fixed label="头像"  width="90">
      <template #default="scope">
        <div class="image-container"  @click="showDialog(scope.row.waiterCover)"> 
             <img :src="scope.row.waiterCover"  style="width: 40px; height: 40px;" />
        </div>
      </template>
    </el-table-column>
    <el-table-column align="center" label="姓名" prop="waiterName" width="80"/>
    <el-table-column align="center" label="性别" prop="gender" width="80">
        <template #default="scope">
          <span>{{ scope.row.gender === 1 ? '女' : '男' }}</span>
        </template>
    </el-table-column>
     <el-table-column align="center" label="状态" prop="waiterStatus" width="80">
        <template #default="scope">
          <span>{{ scope.row.waiterStatus === 0 ? '空闲' :
            scope.row.waiterStatus === 1 ?  '忙碌中' : 
            scope.row.waiterStatus === 2 ?  '休假' : '离职' }}</span>
        </template>
    </el-table-column>
    <el-table-column align="center" label="服务类型" prop="jobType" width="100">
        <template #default="scope">
         <span>{{ scope.row.jobType === 2? '泊车员' : '' }}</span>
        </template>
    </el-table-column>
    <el-table-column align="center" label="服务好评率" prop="waiterPositiveRating" width="120">
          <template #default="scope">
           <span v-if="scope.row.waiterPositiveRating != null"> {{ (scope.row.waiterPositiveRating * 100) + "%" }} </span>
        </template>
    </el-table-column>
    <el-table-column align="center" label="好评数目" prop="waiterTrumpUp"  width="100"/>
    <el-table-column align="center" label="差评数目" prop="waiterTrumpDown"  width="100"/>
    <el-table-column align="center" label="入职时间" prop="timeOfEmployment"  width="200"/>
    <el-table-column align="center" label="最近上班时间" prop="lastOfficeTime" width="200" />
    <el-table-column align="center" label="最近下班时间" prop="lastDayOffTime" width="200" />
    <el-table-column align="center" label="最近离职时间" prop="lastFireTime" width="200" />
    <el-table-column align="center" fixed="right" label="操作" width="130" >
      <template #default="scope">
        <div style="display: flex; justify-content: space-between;  align-items: center">
            <el-button color="#626aef" 
            :dark="isDark" plain size="small" 
            @click="handleEdit(scope.$index, scope.row)"
            v-bind:disabled="scope.row.waiterStatus === 1">
          编辑
        </el-button>
        <el-button
          style="margin-left: 10px;"
          size="small"
          color="#001000"
          :dark="isDark" plain
          v-bind:disabled="scope.row.waiterStatus !== 3"
          @click="deleteWaiter(scope.$index, scope.row)"
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

   <!-- 录入服务员信息 -->
   <el-dialog v-model="dialogAddVisible" title="录入服务员信息"  :width="550" :height="300">
        <el-form ref="ruleFormRef" :rules="rules" :model="waiterEditInfo">
        <el-form-item label="服务员头像" :label-width="formLabelWidth">
           <el-tooltip class="item" effect="dark" content="点击录入服务员图片" placement="top">
          <el-upload
           class="avatar-uploader"
           action="http://localhost:8081/api/supervisor/uploadWaiterCover"
           :show-file-list="false"
           :before-upload="beforeAvatarUpload"
           :on-success="handleAvatarSuccessUpdate" 
           >
            <div class="image-container"> 
              <img v-if="waiterEditInfo.waiterCover" :src="waiterEditInfo.waiterCover" class="avatar" style="width: 40px; height: 40px;" />
               <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
            </div>
          </el-upload>
           </el-tooltip>
        </el-form-item>
        <el-form-item label="服务员姓名" :label-width="formLabelWidth" prop="waiterName" >
         <el-input placeholder="请输入......" v-model="waiterEditInfo.waiterName" autocomplete="off" />
        </el-form-item>
        <el-form-item label="性别" :label-width="formLabelWidth" prop="gender" >
          <el-radio-group v-model="waiterEditInfo.gender">
                <el-radio :value="0" size="large">男</el-radio>
                <el-radio :value="1" size="large">女</el-radio>
          </el-radio-group>
        </el-form-item>
         <el-form-item label="服务类型" :label-width="formLabelWidth" prop="jobType" >
          <el-select v-model="waiterEditInfo.jobType" placeholder="请选择">
              <el-option  :label="`泊车员`" :value="2"> 泊车员 </el-option>
         </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
      <span class="dialog-footer">
      <el-button @click="dialogAddVisible = false;">取消</el-button>
      <el-button type="primary" @click="addWaiter(ruleFormRef)">提交</el-button>
      <el-button  @click="resetForm(ruleFormRef)">重置</el-button>
      </span>
      </template>
   </el-dialog>


 <!-- 编辑服务员信息 -->
   <el-dialog v-model="dialogUpdateVisible" title="更新服务员信息"  :width="550" :height="300">
        <el-form ref="ruleFormRef" :rules="rules" :model="waiterEditInfo">
        <el-form-item label="服务员头像" :label-width="formLabelWidth">
           <el-tooltip class="item" effect="dark" content="点击更换服务员头像" placement="top">
          <el-upload
           class="avatar-uploader"
           action="http://localhost:8081/api/supervisor/uploadWaiterCover"
           :show-file-list="false"
           :before-upload="beforeAvatarUpload"
           :on-success="handleAvatarSuccessUpdate" >
            <div class="image-container"> 
              <img :src="waiterEditInfo.waiterCover"  style="width: 40px; height: 40px;" />
            </div>
          </el-upload>
           </el-tooltip>
        </el-form-item>
        <el-form-item label="服务员姓名" :label-width="formLabelWidth" prop="waiterName" >
         <el-input v-model="waiterEditInfo.waiterName" autocomplete="off" />
        </el-form-item>
        <el-form-item label="性别" :label-width="formLabelWidth" prop="gender">
          <el-radio-group v-model="waiterEditInfo.gender">
                <el-radio :value="0" size="large">男</el-radio>
                <el-radio :value="1" size="large">女</el-radio>
          </el-radio-group>
        </el-form-item>
         <el-form-item label="服务类型" :label-width="formLabelWidth" prop="jobType">
          <el-select v-model="waiterEditInfo.jobType" placeholder="请选择">
              <el-option  :label="`保洁人员`" :value="0"> 保洁人员 </el-option>
              <el-option  :label="`商品配送员`" :value="1"> 商品配送员 </el-option>
              <el-option  :label="`泊车员`" :value="2"> 泊车员 </el-option>
              <el-option  :label="`门僮`" :value="3"> 门僮 </el-option>
              <el-option  :label="`中餐厨师`" :value="4"> 中餐厨师 </el-option>
              <el-option  :label="`西餐厨师`" :value="5"> 西餐厨师 </el-option>
              <el-option  :label="`中餐食堂服务员`" :value="6"> 中餐食堂服务员 </el-option>
              <el-option  :label="`西餐食堂服务员`" :value="7"> 西餐食堂服务员 </el-option>
              <el-option  :label="`礼宾员`" :value="8"> 礼宾员 </el-option>
              <el-option  :label="`前台接待员`" :value="9"> 前台接待员 </el-option>
              <el-option  :label="`总机话务员`" :value="10"> 总机话务员</el-option>
         </el-select>
        </el-form-item>
        <el-form-item label="变更状态" :label-width="formLabelWidth" prop="waiterStatus" >
          <el-select v-model="waiterEditInfo.waiterStatus">
                <el-option  :label="`空闲`" :value="0"> 空闲 </el-option>
                <el-option  :label="`忙碌中`" :value="1"> 忙碌中 </el-option>
                <el-option  :label="`休假`" :value="2"> 休假 </el-option>
                <el-option  :label="`离职`" :value="3"> 离职 </el-option>
          </el-select>
        </el-form-item>
        <el-form-item v-if ="waiterEditInfo.jobType === 4 || waiterEditInfo.jobType === 5 
                            || waiterEditInfo.jobType === 6 || waiterEditInfo.jobType === 7"
        label="隶属餐厅" :label-width="formLabelWidth" prop="departmentId"  >
          <el-select v-model="waiterEditInfo.departmentId" placeholder="请选择" >
              <el-option v-for="(name, id) in canteenInfo" :key="id" :label="name" :value="id"> {{ name }} </el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
      <span class="dialog-footer">
      <el-button @click="dialogUpdateVisible = false">取消</el-button>
        <el-button type="primary" @click="updateWaiter(ruleFormRef)">提交</el-button>
      </span>
      </template>
   </el-dialog>


     <!-- 服务员头像预览 -->
   <el-dialog v-model="dialogVisible" height="800px" width="1000px" title="服务员头像预览">
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
import { ElMessageBox } from 'element-plus';
import type { FormRules } from 'element-plus';
import type { FormInstance } from 'element-plus';
import router from '@/router';
import { errorMessages } from 'vue/compiler-sfc';



const dialogAddVisible = ref(false)
const dialogUpdateVisible = ref(false)
const total = ref(10)  // 总数据量

const dataList = ref<Waiter[]>([])  // 原始数据列表


const query = reactive ( {
    jobTypes: [2],
    currentPage: 1,
    size: 15,
    keyword: ''
  }  
)
const ruleFormRef = ref<FormInstance>()   
const dialogVisible = ref(false)
const previewImage = ref('')
let currentMaxAmount = 0;

// 重置表单
const resetForm = (formEl: FormInstance | undefined) => {
  if (!formEl) return
  formEl.resetFields()
}


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
      noticeError('上传服务员头像大小不能超过 512KB!');
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
            noticeError('上传服务员头像尺寸不能超过 900 x 800!');
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

// 判断当前服务员是否离职（黄色）
const tableRowRule = ({
  row,
}: {
  row: Waiter
}) => {
  if (row.waiterStatus === 3) {
    return 'warning-row'
  }
}

interface Waiter {
  waiterId: string
  waiterCover: string
  waiterName: string
  canteenName: string
  departmentId: string
  gender: number
  waiterStatus: number
  jobType: number
  waiterPositiveRating: number
  waiterTrumpUp: number
  waiterTrumpDown: number
  timeOfEmployment: string
  lastOfficeTime: string,
  lastDayOffTime: string,
}


// 添加服务员
const addWaiter = async ( formEl: FormInstance | undefined ) => {

     if (!formEl) {
      return;
     }
      await formEl.validate((valid, fields) => {
           if (valid) {
               axiosInstance.post(`/api/supervisor/addWaiterInfo`, waiterEditInfo )
              .then(response => {
                notice("录入当前服务员信息成功！");
                dialogAddVisible.value = false;
                // 在修改后刷新数据
                loadDataList();
                })
                // 不管是否录入成功，都要将获得的图片路径进行删除
                waiterEditInfo.waiterCover = '';
           } else {
                noticeError("表单不完整或数据格式不正确。请仔细检查，再进行提交！")
                return
           }
      })
     
      
  }

const rules = reactive<FormRules<Waiter>>({

  waiterName: [
    { required: true, message: '服务员名字不能为空', trigger: 'blur' },
    { min: 2, max: 15, message: '字符长度应该在2到15之间', trigger: 'change' },
    {
        validator: (rule, value, callback) => {
            if (value && value.includes(' ')) {
                callback(new Error('服务员名字中不能包含空格'));
            } else {
                callback();
            }
        },
        trigger: 'change'
    }
  ],
  gender: [
     { required: true, message: '请选择一个性别', trigger: 'change' },
  ],
  jobType: [
     { required: true, message: '请选择一个服务类型', trigger: 'change' },
  ],
  departmentId: [
     { required: true, message: '请选择一个隶属餐厅', trigger: 'change' },
  ],
  waiterStatus: [
    { required: true, message: '请选择一个服务员状态', trigger: 'change' },
  ]
})

  // 自定义搜索
function fetchData(newPage: number) {
  query.currentPage = newPage;
   // 如果有搜索关键词，则添加到查询参数中
  loadDataList();
}

// 加载用户数据
const loadDataList = async () => {
    try {  
    const response = await axiosInstance.post(`/api/supervisor/queryWaiterList`, query);
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
onMounted(() => {
  loadDataList(),
  axiosInstance.post(`/api/supervisor/queryCanteenList`)
  .then(response => {
      const canteens = response.data.data.list
      canteens.forEach(canteen => {
      canteenInfo[canteen.canteenId] = canteen.canteenName;});
  })
})

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
 // 服务员修改集合
  const waiterEditInfo = reactive ( {
  waiterId: '',  
  departmentId: '',
  canteenName: '',
  waiterCover: '',
  waiterName: '',
  gender: null,
  waiterStatus: null,
  jobType: null,
  waiterPositiveRating: null,
  waiterTrumpUp: null,
  waiterTrumpDown: null,
  timeOfEmployment: '',
  lastOfficeTime: '',
  lastDayOffTime: '',
  lastFireTime: '',
})

// 图片上传成功后，回调函数获取上传文件的文件名
function handleAvatarSuccessUpdate(response : any, file : any ) {
    waiterEditInfo.waiterCover = "https://hotel-sys-ayuan.oss-cn-beijing.aliyuncs.com/" + file.name;
    notice("图片成功上传, 但要完整提交后才能更新图片！")
    
}

const canteenInfo = reactive ({})
// 清除缓存,异步展示当前所有的食堂信息
const clearLastCache =  () => {
  dialogAddVisible.value = true;
  waiterEditInfo.waiterName = '';
  waiterEditInfo.waiterId = '';
  waiterEditInfo.waiterCover = '';
  waiterEditInfo.departmentId = '';
  waiterEditInfo.jobType = null;
  waiterEditInfo.waiterTrumpUp = null;
  waiterEditInfo.waiterTrumpDown = null
  waiterEditInfo.lastDayOffTime = null;
  waiterEditInfo.lastFireTime = null;
  
}

// 编辑逻辑处理
const handleEdit = (index: number, row: Waiter) => {
  dialogUpdateVisible.value = true;
  waiterEditInfo.waiterId = row.waiterId;
  waiterEditInfo.waiterName = row.waiterName;
  waiterEditInfo.gender = row.gender;
  waiterEditInfo.waiterCover = row.waiterCover;
  waiterEditInfo.waiterStatus = row.waiterStatus;
  waiterEditInfo.jobType = row.jobType;
  waiterEditInfo.departmentId = row.departmentId;
}



// 更新房间信息
const updateWaiter = async ( formEl: FormInstance | undefined ) => {
  if (!formEl) {
      return;
     }
  await formEl.validate((valid, fields) => {
       if (valid) {
           axiosInstance.post(`/api/supervisor/updateWaiterInfo`, waiterEditInfo )
          .then(response => {
            notice("变更当前服务员信息成功！");
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

// 删除单一服务员
async function deleteWaiter  (index: number, row: Waiter) {
  const result = await ElMessageBox.confirm('确认删除当前服务员吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  });

  if (result) {
    await axiosInstance.delete(`/api/supervisor/deleteCurrentWaiter?waiterId=${row.waiterId}`)
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