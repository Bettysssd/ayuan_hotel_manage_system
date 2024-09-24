<template>
  <!-- 房间管理 -->
  <div class="right" style="margin-bottom: 10px;" >
      <el-button  style="margin-right: 1200px;" type="success" :icon="Search" @click="clearLastCache" >录入客房</el-button>
       <el-input v-model="query.keyword" style="width: 360px" placeholder="请输入关键字" />
       <el-button  style="margin-left: 10px;" type="primary" :icon="Search" @click="fetchData(1)" >搜索</el-button>
    </div>
  <el-table :data="dataList" border style="width: 100% " height="700"  :row-class-name="tableRowRule">
    <el-table-column align="center" fixed label="客房图片"  width="90">
      <template #default="scope">
        <div class="image-container"  @click="showDialog(scope.row.roomCover)"> 
             <img :src="scope.row.roomCover"  style="width: 40px; height: 40px;" />
        </div>
      </template>
    </el-table-column>
    <el-table-column align="center" label="客房介绍" prop="roomInfo"  width="200"/>
    <el-table-column align="center" label="客房类型" prop="roomType" width="80">
    <template #default="scope">
       <span>{{ scope.row.roomType === 5 ? '家庭间' :
              scope.row.roomType === 4 ? '豪华双人间' : 
              scope.row.roomType === 3 ? '豪华单人间' :
              scope.row.roomType === 2 ? '经济双人间' : '温馨单人间' }}</span>
    </template>  
    </el-table-column>
    <el-table-column align="center" label="客房房间号" prop="roomMark" width="120" />
    <el-table-column align="center" label="客房好评率"  width="120">
          <template #default="scope">
          <span v-if="scope.row.roomRating != null"> {{ scope.row.roomRating + "%" }} </span>
        </template>
    </el-table-column>
    <el-table-column align="center" label="客房好评数目" prop="roomTrumpUp"  width="100"/>
    <el-table-column align="center" label="客房差评数目" prop="roomTrumpDown"  width="100"/>
    <el-table-column align="center" label="入住人数" prop="numberOfOccupants"  width="100"/>
    <el-table-column align="center" label="成员名单" prop="listOfMembers"  width="100"/>
    <el-table-column align="center" label="客房定价金额" prop="price"  width="100">
      <template #default="scope">
        <span>{{ scope.row.price }} 元</span>
      </template>  
    </el-table-column>
    <el-table-column align="center" label="预定时间" prop="bookTime"  width="200"/>
    <el-table-column align="center" label="入住时间" prop="checkInTime" width="200" />
    <el-table-column align="center" label="退房时间" prop="checkOutTime" width="200"/>
    <el-table-column align="center" label="预定状态" width="100">
    <template #default="scope">
      <span>{{ scope.row.roomStatus === 1 ? '已预定' :
              scope.row.roomStatus === 0 ? '空闲' :
              scope.row.roomStatus === 3 ? '已入住' :
               '停用维护中' }}</span>
    </template>
   </el-table-column>
    
    <el-table-column align="center" fixed="right" label="操作" width="200" >
      <template #default="scope">
        <div style="display: flex; justify-content: space-between;  align-items: center">
            <el-button  v-bind:disabled="scope.row.roomStatus === 1 || scope.row.roomStatus === 3 "
            color="#626aef" :dark="isDark" plain size="small" @click="handleEdit(scope.$index, scope.row)">
          编辑
        </el-button>
      
        <el-button
          style="margin-left: 10px;"
          size="small"
          color="#008000"
          :dark="isDark" plain
          v-bind:disabled="scope.row.roomStatus === 1 || scope.row.roomStatus === 3 "
          @click="handleRoomStatus(scope.$index, scope.row)"
        >
            <span>{{ scope.row.roomStatus === 0 ? '维护' :
                     scope.row.roomStatus === 3 ? '勿动' :
               '启用' }}</span>
        </el-button>
        <el-button
          style="margin-left: 10px;"
          size="small"
          color="#001000"
          :dark="isDark" plain
          v-bind:disabled="scope.row.roomStatus === 1 || scope.row.roomStatus === 3"
          @click="deleteRoom(scope.$index, scope.row)"
        > 删除
        </el-button>
        </div>
      </template>
    </el-table-column>
  </el-table>
    <!-- 分页 -->
   <div style="margin-top: 10px;">
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

   <!-- 录入客房信息 -->
   <el-dialog v-model="dialogAddVisible" title="录入客房信息"  :width="550" :height="300">
        <el-form ref="ruleFormRef" :rules="rules" :model="roomEditInfo"  >
        <el-form-item label="客房图片" :label-width="formLabelWidth">
          <el-upload
           class="avatar-uploader"
           action="http://localhost:8081/api/supervisor/uploadRoomCover"
           :show-file-list="false"
           :before-upload="beforeAvatarUpload"
           :on-success="handleAvatarSuccessUpdate" 
           >
            <div class="image-container"> 
              <img v-if="roomEditInfo.roomCover" :src="roomEditInfo.roomCover" class="avatar" style="width: 40px; height: 40px;" />
               <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
            </div>
          </el-upload>
        </el-form-item>
        <el-form-item label="客房介绍" :label-width="formLabelWidth" prop="roomInfo" >
         <el-input v-model="roomEditInfo.roomInfo" autocomplete="off" type="textarea"/>
        </el-form-item>
        <el-form-item label="客房类型" :label-width="formLabelWidth" prop="roomType">
         <el-select v-model="roomEditInfo.roomType">
              <el-option :label="`家庭间`" :value="5"> 家庭间 </el-option>
              <el-option :label="`豪华双人间`" :value="4"> 豪华双人间 </el-option>
              <el-option :label="`豪华单人间`" :value="3"> 豪华单人间 </el-option>
              <el-option :label="`经济双人间`" :value="2"> 经济双人间 </el-option>
              <el-option :label="`温馨单人间`" :value="1"> 温馨单人间 </el-option>
         </el-select>
        </el-form-item>
         <el-form-item label="客房房间号" :label-width="formLabelWidth" prop="roomMark" >
         <el-input v-model="roomEditInfo.roomMark" autocomplete="off" />
        </el-form-item>
         <el-form-item label="入住人数" :label-width="formLabelWidth"  prop="numberOfOccupants">
         <el-select v-model="roomEditInfo.numberOfOccupants">
              <el-option v-if="roomEditInfo.roomType === 5" :label="`五人`" :value="5"> 五人 </el-option>
              <el-option v-if="roomEditInfo.roomType === 5" :label="`四人`" :value="4"> 四人 </el-option>
              <el-option v-if="roomEditInfo.roomType === 5" :label="`三人`" :value="3"> 三人 </el-option>
              <el-option v-if="roomEditInfo.roomType === 2 || roomEditInfo.roomType === 4" :label="`两人`" :value="2"> 两人 </el-option>
              <el-option v-if="roomEditInfo.roomType === 1 || roomEditInfo.roomType === 3" :label="`单人`" :value="1"> 单人 </el-option>
         </el-select>
        </el-form-item>
         <el-form-item label="客房定价金额" :label-width="formLabelWidth" prop="price" >
          <el-input type="number" v-model="roomEditInfo.price" autocomplete="off" placeholder="以人民币为计算单位，请输入数字"/>
        </el-form-item>

      </el-form>
      <template #footer>
      <span class="dialog-footer">
      <el-button @click="dialogAddVisible = false;">取消</el-button>
      <el-button type="primary" @click="addRoom(ruleFormRef)">提交</el-button>
      <el-button  @click="resetForm(ruleFormRef)">重置</el-button>
      </span>
      </template>
   </el-dialog>


 <!-- 编辑客房信息 -->
   <el-dialog v-model="dialogUpdateVisible" title="更新客房信息"  :width="550" :height="300">
        <el-form ref="ruleFormRef" :rules="rules" :model="roomEditInfo" >
        <el-form-item label="客房图片" :label-width="formLabelWidth">
          <el-upload
           class="avatar-uploader"
           action="http://localhost:8081/api/supervisor/uploadRoomCover"
           :show-file-list="false"
           :before-upload="beforeAvatarUpload"
           :on-success="handleAvatarSuccessUpdate" >
            <div class="image-container"> 
              <img :src="roomEditInfo.roomCover"  style="width: 40px; height: 40px;" />
            </div>
          </el-upload>
        </el-form-item>
        <el-form-item label="客房介绍" :label-width="formLabelWidth" prop="roomInfo"  >
         <el-input v-model="roomEditInfo.roomInfo" autocomplete="off"  type="textarea"/>
        </el-form-item>
        <el-form-item label="客房类型" :label-width="formLabelWidth" prop="roomType">
         <el-select v-model="roomEditInfo.roomType">
              <el-option :label="`家庭间`" :value="5"> 家庭间 </el-option>
              <el-option :label="`豪华双人间`" :value="4"> 豪华双人间 </el-option>
              <el-option :label="`豪华单人间`" :value="3"> 豪华单人间 </el-option>
              <el-option :label="`经济双人间`" :value="2"> 经济双人间 </el-option>
              <el-option :label="`温馨单人间`" :value="1"> 温馨单人间 </el-option>
         </el-select>
        </el-form-item>
         <el-form-item label="客房房间号" :label-width="formLabelWidth"  prop="roomMark" >
         <el-input v-model="roomEditInfo.roomMark" autocomplete="off" />
        </el-form-item>
         <el-form-item label="客房定价金额" :label-width="formLabelWidth"  prop="price" >
         <el-input type="number" v-model="roomEditInfo.price" autocomplete="off" />
        </el-form-item>
         <el-form-item label="入住人数" :label-width="formLabelWidth" prop="numberOfOccupants" >
         <el-select v-model="roomEditInfo.numberOfOccupants">
              <el-option v-if="roomEditInfo.roomType === 5" :label="`五人`" :value="5"> 五人 </el-option>
              <el-option v-if="roomEditInfo.roomType === 5" :label="`四人`" :value="4"> 四人 </el-option>
              <el-option v-if="roomEditInfo.roomType === 5" :label="`三人`" :value="3"> 三人 </el-option>
              <el-option v-if="roomEditInfo.roomType === 2 || roomEditInfo.roomType === 4" :label="`两人`" :value="2"> 两人 </el-option>
              <el-option v-if="roomEditInfo.roomType === 1 || roomEditInfo.roomType === 3" :label="`单人`" :value="1"> 单人 </el-option>
         </el-select>
        </el-form-item>

      </el-form>
      <template #footer>
      <span class="dialog-footer">
      <el-button @click="dialogUpdateVisible = false">取消</el-button>
        <el-button type="primary" @click="updateRoom(ruleFormRef)">提交</el-button>
      </span>
      </template>
   </el-dialog>

     <!-- 客房图片预览 -->
   <el-dialog v-model="dialogVisible" height="800px" width="1000px" title="客房图片预览">
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
const ruleFormRef = ref<FormInstance>()
const dataList = ref<Room[]>([])  // 原始数据列表

// 重置表单
const resetForm = ( formEl: FormInstance | undefined ) => {
  if (!formEl) return
  formEl.resetFields()
}

const query = reactive ( {
    currentPage: 1,
    size: 15,
    keyword: ''
  }  
)
    
const dialogVisible = ref(false)
const previewImage = ref('')
let currentMaxAmount = 0;


interface Room {
  roomId: string
  roomCover: string
  roomInfo: string
  roomType: number
  roomStatus: number
  roomMark: string
  price: number
  roomRating: number
  roomTrumpUp: number
  roomTrumpDown: number
  numberOfOccupants: number
  bookTime: string
  checkInTime: string
  checkOutTime: string
  listOfMembers: string
}

const rules = reactive<FormRules<Room>>({

  roomInfo: [
    { required: true, message: '客房介绍不能为空', trigger: 'blur' },
    { min: 1, max: 200, message: '字符长度应该在1到200之间', trigger: 'change' },
  ],
  
  roomType: [
     { required: true, message: '请选择一个客房类型', trigger: 'change' },
  ],
  numberOfOccupants: [
     { required: true, message: '请选择一个对应的客房人数', trigger: 'change' },
  ],
  roomMark: [
    { required: true, message: '客房房间号不能为空', trigger: 'blur' },
  ],
  price: [
    { required: true, message: '客房租金不能为空', trigger: 'blur' },
    {
           validator: (rule, value, callback) => {
             if (value <= 0) {
                callback(new Error('客房租金必须为正整数'));
              } else {
                // 验证成功
                callback();
              }
            },
            trigger: 'change',
           
     },
  ],
})

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
      noticeError('上传客房图片大小不能超过 512KB!');
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
            noticeError('上传客房图片尺寸不能超过 900 x 800!');
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

// 判断客房是否空闲（黄色）
const tableRowRule = ({
  row,
}: {
  row: Room
}) => {
  if (row.roomStatus === 2) {
    return 'warning-row'
  }
}




// 添加客房
const addRoom = async  ( formEl: FormInstance | undefined ) => {
    if (isLegitimated()) {
    noticeError("当前客房类型与可住人数不匹配，请更改入住人数再提交")
    }
   if (!formEl) {
      return;
     }
  await formEl.validate((valid, fields) => {
    if (valid) {
      axiosInstance.post(`/api/supervisor/addRoomInfo`, roomEditInfo )
      .then(response => {
        if (response.headers["room-add-error"] === "unable") {
          noticeError("当前客房房间号已录入数据库，请重试");
          return;
        }
        notice("录入当前客房信息成功！");
        dialogAddVisible.value = false;
        // 将获得的图片路径进行删除
        roomEditInfo.roomCover = '';
         // 在修改后刷新数据
        loadDataList();
        })
    } else {
      noticeError("表单不完整或数据格式不正确。请仔细检查，再进行提交！")
      return
    } 
  })
  
}

  // 自定义搜索
function fetchData(newPage: number) {
  query.currentPage = newPage;
   // 如果有搜索关键词，则添加到查询参数中
  loadDataList();
}

// 加载用户数据
const loadDataList = async () => {
    try {  
    const response = await axiosInstance.post(`/api/supervisor/queryRoomList`, query);
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
 // 客房修改集合
  const roomEditInfo = reactive<Room> ( {
  roomId: '',
  roomCover: '',
  roomInfo: '',
  roomType: null,
  roomStatus: null,
  roomMark: '',
  price: null,
  roomRating: null,
  roomTrumpUp: null,
  roomTrumpDown: null,
  numberOfOccupants: null,
  bookTime: '',
  checkInTime: '',
  checkOutTime: '',
  listOfMembers: ''
})

// 图片上传成功后，回调函数获取上传文件的文件名
function handleAvatarSuccessUpdate(response : any, file : any ) {
    roomEditInfo.roomCover = "https://hotel-sys-ayuan.oss-cn-beijing.aliyuncs.com/" + file.name;
    notice("图片成功上传, 但要完整提交后才能更新图片！")
    
}

const clearLastCache = () => {
  dialogAddVisible.value = true;
  roomEditInfo.roomId = '';
  roomEditInfo.price = null;
  roomEditInfo.roomCover = '';
  roomEditInfo.roomInfo = '';
  roomEditInfo.roomType = null;
  roomEditInfo.numberOfOccupants = null;
  roomEditInfo.roomMark = '';
}

// 编辑逻辑处理
const handleEdit = (index: number, row: Room) => {
  dialogUpdateVisible.value = true;
  roomEditInfo.roomId = row.roomId;
  roomEditInfo.roomCover = row.roomCover;
  roomEditInfo.roomInfo = row.roomInfo;
  roomEditInfo.roomType = row.roomType;
  roomEditInfo.price = row.price;
  roomEditInfo.numberOfOccupants = row.numberOfOccupants;
  roomEditInfo.roomMark = row.roomMark;
}

// 判断房间类型选择是否合法
const isLegitimated = () => {
   if ( roomEditInfo.roomType === 5 && roomEditInfo.numberOfOccupants < 3) {
      return true;
   } 
   if ((roomEditInfo.roomType === 4 ||  roomEditInfo.roomType === 2) && roomEditInfo.numberOfOccupants !== 2 ) {
      return true;
   }
   if ((roomEditInfo.roomType === 1 ||  roomEditInfo.roomType === 3) && roomEditInfo.numberOfOccupants !== 1) {
     return true;
   }   
   return false;
      
}

// 更新房间信息
const updateRoom = async (formEl: FormInstance | undefined) => {
   if (isLegitimated()) {
    noticeError("当前客房类型与可住人数不匹配，请更改入住人数再提交")
  } 
   if (!formEl) {
    return
    }

   await formEl.validate((valid, fields) => {
      if (valid) {
          axiosInstance.post(`/api/supervisor/updateRoomInfo`, roomEditInfo )
      .then(response => {
        if (response.headers["room-update-error"] === "unable") {
          noticeError("当前客房房间号已录入数据库，请重试");
          return;
        }
        notice("变更当前客房信息成功！");
        // 在修改后刷新数据
        dialogUpdateVisible.value = false;
        loadDataList();
      })
      } else {
         noticeError("表单不完整或数据格式不正确。请仔细检查，再进行提交！")
         return
      }
   })
   
      
  
}

 // 确认修改用户状态的函数
async function confirmDeleteRoom(index: number, row: any) {
  const result = await ElMessageBox.confirm('确认删除当前客房吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  });

  if (result) {
    await axiosInstance.delete(`/api/supervisor/deleteCurrentRoom?roomId=${row.roomId}`)
      .then(response => {
        notice("删除当前客房成功！");
        // 在修改后刷新数据
        dialogUpdateVisible.value = false;
        loadDataList();
      })
  }
}
// 修改用户状态逻辑处理
const handleRoomStatus = (index: number, row: Room) => {
   confirmChangeStatus(index, row);
}

async function confirmChangeStatus(index: number, row: any) {
  const result = await ElMessageBox.confirm('确认变更当前客房的状态吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  });

  if (result) {
    const roomStatusInfo = reactive({
      roomId: row.roomId,
      roomStatus: row.roomStatus === 2 ? 0 : 2
    });

    // 修改客房状态
    await axiosInstance.post(`/api/supervisor/changeRoomStatus`, roomStatusInfo)
      .then(response => {
        notice("变更当前客房状态成功！");
        // 在修改后刷新数据
        dialogUpdateVisible.value = false;
        loadDataList();
      })
  }
}

// 删除单一客房
const deleteRoom = (index: number, row: Room) => {
   confirmDeleteRoom(index, row);
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