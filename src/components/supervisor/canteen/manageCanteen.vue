<template>
<!-- 食堂信息管理 -->
  <div class="right" style="margin-bottom: 10px;" >
       <el-button  style="margin-right: 1200px;" type="success" :icon="Search" @click="clearLastCache" >录入餐厅信息</el-button>
       <el-input v-model="query.keyword" style="width: 360px" placeholder="请输入关键字" />
       <el-button  style="margin-left: 10px;" type="primary" :icon="Search" @click="fetchData(1)" >搜索</el-button>
    </div>
  <el-table :data="dataList" border  height="700"  :row-class-name="tableRowRule">
    <el-table-column align="center" fixed label="食堂图片"  >
      <template #default="scope">
        <div class="image-container"  @click="showDialog(scope.row.canteenCover)"> 
             <img :src="scope.row.canteenCover"  style="width: 50px; height: 50px;" />
        </div>
      </template>
    </el-table-column>
    <el-table-column align="center" label="食堂名称" prop="canteenName" width="140px"/>
     <el-table-column align="center" label="食堂总位置数量" prop="canteenTotalTables" width="140px" />
      <el-table-column align="center" label="食堂当前用餐人数" prop="canteenNumberOfDiners" width="140px" />
       <el-table-column align="center" label="食堂当前空余位置" prop="canteenCurrentAvailableTables" width="140px" />
     <el-table-column align="center" label="食堂风味类型" width="140px" >
        <template #default="scope">
          <span>{{ scope.row.canteenFlavorType === 0 ? '中餐' : '西餐'}}</span>
        </template>
    </el-table-column>
      <el-table-column align="center" label="食堂位置状态" width="130px" >
        <template #default="scope">
          <span>{{ scope.row.canteenTableStatus === 0 ? '有余位，可预定' : '已满'}}</span>
        </template>
    </el-table-column>
     <el-table-column align="center" label="食堂营业状态" width="120px" >
        <template #default="scope">
          <span>{{ scope.row.canteenOpenStatus === 1 ? '打烊' :
                   scope.row.canteenOpenStatus === 2 ? '营业' :'暂停营业' }}</span>
        </template>
    </el-table-column>
     <el-table-column align="center" label="食堂简介" prop="canteenIntroduce"  width="250px"/>
    <el-table-column align="center" label="食堂菜谱" prop="canteenOfferFood"  width="250px" />
    <el-table-column align="center" label="食堂营业时间" prop="canteenOpenTime"  width="180px"  />
    <el-table-column align="center" label="食堂打烊时间" prop="canteenClosedTime"  width="180px" />
       <el-table-column align="center" label="食堂客户满意度" width="130">
          <template #default="scope">
          <span v-if="scope.row.canteenRating != null"> {{ scope.row.canteenRating + "%" }} </span>
        </template>
    </el-table-column>
     <el-table-column align="center" label="食堂好评数目" prop="canteenTrumpUp" width="120px" />
      <el-table-column align="center" label="食堂差评数目" prop="canteenTrumpDown" width="120px"  />
    <el-table-column align="center" fixed="right" label="操作" width="130" >
      <template #default="scope" >
        <div style="display: flex; justify-content: space-between;  align-items: center">
            <el-button color="#626aef" 
            :dark="isDark" plain size="small" 
            @click="handleEdit(scope.$index, scope.row)"
             v-bind:disabled="scope.row.canteenOpenStatus === 2  "
            >
          编辑
        </el-button>
        <el-button
          size="small"
          color="#001000"
          :dark="isDark" plain
          v-bind:disabled="scope.row.canteenOpenStatus !== 0"
          @click="deleteCurrentCanteenInfo(scope.$index, scope.row)"
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

<!-- 新增食堂信息 -->
   <el-dialog v-model="dialogAddVisible" title="录入食堂信息"  :width="650" :height="430">
        <el-form ref="ruleFormRef" :rules="rules" :model="canteenEditInfo">
        <el-form-item label="食堂图片" :label-width="formLabelWidth">
          <el-upload
           class="avatar-uploader"
           action="http://localhost:8081/api/supervisor/uploadCanteenCover"
           :show-file-list="false"
           :before-upload="beforeAvatarUpload"
           :on-success="handleAvatarSuccessUpdate" >
            <div class="image-container"> 
              <img :src="canteenEditInfo.canteenCover"  style="width: 40px; height: 40px;" />
            </div>
          </el-upload>
        </el-form-item>
        <el-form-item label="食堂名称" :label-width="formLabelWidth" prop="canteenName">
         <el-input v-model="canteenEditInfo.canteenName" autocomplete="off" />
        </el-form-item>
      <el-form-item align="center" label="食堂风味类型" prop="canteenFlavorType" >
         <el-radio-group v-model="canteenEditInfo.canteenFlavorType">
             <el-radio :value="0" size="large">中餐</el-radio>
             <el-radio :value="1" size="large">西餐</el-radio>
         </el-radio-group>
      </el-form-item>
      <el-form-item align="center" label="食堂当前状态" prop="canteenOpenStatus" >
         <el-select v-model="canteenEditInfo.canteenOpenStatus">
              <el-option  :label="`暂停营业`" :value="0"> 暂停营业 </el-option>
              <el-option  :label="`打烊`" :value="1"> 打烊 </el-option>
              <el-option  :label="`营业`" :value="2"> 营业 </el-option>
         </el-select>
      </el-form-item>
     <el-form-item label="食堂可容纳人员数量" prop="canteenTotalTables">
      <el-input type="number" v-model="canteenEditInfo.canteenTotalTables" autocomplete="off" />
    </el-form-item> 
    <el-form-item label="食堂简介" prop="canteenIntroduce">
      <el-input v-model="canteenEditInfo.canteenIntroduce" type="textarea" />
    </el-form-item>
    <el-form-item label="食堂菜谱" prop="canteenOfferFood">
      <el-input v-model="canteenEditInfo.canteenOfferFood" type="textarea" />
    </el-form-item>
     <el-form-item label="食堂营业时间" :label-width="formLabelWidth" prop="canteenOpenTime">
      <el-time-picker
        v-model="canteenEditInfo.canteenOpenTime"
         value-format="HH:mm:ss"
        placeholder="请设置食堂营业时间"
      />
     </el-form-item>
    <el-form-item label="食堂打烊时间" :label-width="formLabelWidth" prop="canteenClosedTime">
      <el-time-picker
        v-model="canteenEditInfo.canteenClosedTime"
        value-format="HH:mm:ss"
        placeholder="请设置食堂打烊时间"
      />
    </el-form-item> 
      </el-form>
      <template #footer>
      <span class="dialog-footer">
      <el-button @click="dialogAddVisible = false">取消</el-button>
        <el-button type="primary" @click="addCanteenInfo(ruleFormRef)">提交</el-button>
         <el-button  @click="resetForm(ruleFormRef)">重置</el-button>
      </span>
      </template>
   </el-dialog>  

 <!-- 编辑食堂信息 -->
   <el-dialog v-model="dialogUpdateVisible" title="更新食堂信息"  :width="650" :height="430">
        <el-form ref="ruleFormRef" :rules="rules" :model="canteenEditInfo">
        <el-form-item label="食堂图片" :label-width="formLabelWidth">
          <el-upload
           class="avatar-uploader"
           action="http://localhost:8081/api/supervisor/uploadCanteenCover"
           :show-file-list="false"
           :before-upload="beforeAvatarUpload"
           :on-success="handleAvatarSuccessUpdate" >
            <div class="image-container"> 
              <img :src="canteenEditInfo.canteenCover"  style="width: 40px; height: 40px;" />
            </div>
          </el-upload>
        </el-form-item>
        <el-form-item label="食堂名称" :label-width="formLabelWidth" prop="canteenName" >
         <el-input v-model="canteenEditInfo.canteenName" autocomplete="off" />
        </el-form-item>
      <el-form-item align="center" label="食堂风味类型" prop="canteenFlavorType">
         <el-radio-group v-model="canteenEditInfo.canteenFlavorType">
             <el-radio :value="0" size="large">中餐</el-radio>
             <el-radio :value="1" size="large">西餐</el-radio>
         </el-radio-group>
      </el-form-item>
      <el-form-item align="center" label="食堂当前状态" prop="canteenOpenStatus">
         <el-select v-model="canteenEditInfo.canteenOpenStatus">
              <el-option  :label="`暂停营业`" :value="0"> 暂停营业 </el-option>
              <el-option  :label="`打烊`" :value="1"> 打烊 </el-option>
              <el-option  :label="`营业`" :value="2"> 营业 </el-option>
         </el-select>
      </el-form-item>
      <el-form-item label="食堂可容纳人员数量" prop="canteenTotalTables">
      <el-input type="number" v-model="canteenEditInfo.canteenTotalTables" autocomplete="off" />
    </el-form-item> 
    <el-form-item label="食堂简介" prop="canteenIntroduce">
      <el-input v-model="canteenEditInfo.canteenIntroduce" type="textarea" />
    </el-form-item>
    <el-form-item label="食堂菜谱" prop="canteenOfferFood">
      <el-input v-model="canteenEditInfo.canteenOfferFood" type="textarea" />
    </el-form-item>
     <el-form-item label="食堂营业时间" :label-width="formLabelWidth" prop="canteenOpenTime">
      <el-time-picker
        v-model="canteenEditInfo.canteenOpenTime"
         value-format="HH:mm:ss"
        placeholder="请设置食堂营业时间"
      />
     </el-form-item>
    <el-form-item label="食堂打烊时间" :label-width="formLabelWidth" prop="canteenClosedTime">
      <el-time-picker
        v-model="canteenEditInfo.canteenClosedTime"
         value-format="HH:mm:ss"
        placeholder="请设置食堂打烊时间"
      />
    </el-form-item> 
      </el-form>
      <template #footer>
      <span class="dialog-footer">
      <el-button @click="dialogUpdateVisible = false">取消</el-button>
        <el-button type="primary" @click="updateCanteenInfo(ruleFormRef)">提交</el-button>
      </span>
      </template>
   </el-dialog>

  <!-- 食堂图片预览 -->
   <el-dialog v-model="dialogVisible" height="800px" width="1000px" title="食堂图片预览">
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
const dataList = ref<Canteen[]>([])  // 原始数据列表


const query = reactive ( {
    currentPage: 1,
    size: 15,
    keyword: ''
  }  
)
  
const rules = reactive<FormRules<Canteen>>({

  canteenName: [
    { required: true, message: '食堂名称不能为空', trigger: 'blur' },
    { min: 1, max: 10, message: '字符长度应该在1到10之间', trigger: 'change' },
  ],
  canteenFlavorType: [
     { required: true, message: '请选择一个风味类型', trigger: 'change' },
  ],
  canteenOpenStatus: [
     { required: true, message: '请选择一个食堂当前的状态', trigger: 'change' },
  ],
  canteenTotalTables: [
    { required: true, message: '可容纳人数不能为空', trigger: 'blur' },
        {
           validator: (rule, value, callback) => {
             if (value <= 0) {
                callback(new Error('可容纳人数必须为正整数'));
              } else {
                // 验证成功
                callback();
              }
            },
            trigger: 'change',
        }, 
  ],
  canteenOpenTime: [
    {  required: true,  message: '营业时间不能为空',  trigger: 'change' },
  ],
   canteenClosedTime: [
    {  required: true,  message: '打烊时间不能为空',  trigger: 'change' },
  ],
  canteenIntroduce: [
    { required: true, message: '食堂简介不能为空', trigger: 'blur' },
    { min: 1, max: 500, required: true, message: '字符长度应该在1到500之间', trigger: 'change' },
  ],
   canteenOfferFood: [
    { required: true, message: '食堂菜谱不能为空', trigger: 'blur' },
    { min: 1, max: 200, message: '字符长度应该在1到200之间', trigger: 'change' },
  ],
})  
const dialogVisible = ref(false)
const previewImage = ref('')
let currentMaxAmount = 0;

// 重置表单
const resetForm = ( formEl: FormInstance | undefined ) => {
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

   
// 录入食堂信息
const addCanteenInfo = async ( formEl: FormInstance | undefined ) => {
       if (!formEl) {
        return;
     }
     await formEl.validate((valid, fields) => {
          if (valid) {
              axiosInstance.post(`/api/supervisor/addCanteenInfo`, canteenEditInfo )
            .then(response => {
              notice("录入当前食堂信息成功！");
              dialogAddVisible.value = false;
              // 在修改后刷新数据
              loadDataList();
              })
              // 不管是否录入成功，都要将获得的图片路径进行删除
              canteenEditInfo.canteenCover = '';
          }else{
              noticeError("表单不完整或数据格式不正确。请仔细检查，再进行提交！")
              return
          } 
     })
      
  
}    

// 图片预览
function showDialog(imageURL: any) {
  previewImage.value = imageURL
  dialogVisible.value = true
}

// 当食堂是暂未营业时，进行标黄
const tableRowRule = ({
  row,
}: {
  row: Canteen
}) => {
  if (row.canteenOpenStatus === 0) {
    return 'warning-row'
  }
}

interface Canteen {
  canteenId: string
  canteenCover: string
  canteenName: string
  canteenFlavorType: number
  canteenTableStatus: number
  canteenOpenStatus: number
  canteenTotalTables: number
  canteenNumberOfDiners: number
  canteenCurrentAvailableTables: number
  canteenIntroduce: string
  canteenOfferFood: string
  canteenOpenTime: string
  canteenClosedTime: string
  canteenRating: number
  canteenTrumpUp: number
  canteenTrumpDown: number
}


  // 自定义搜索
function fetchData(newPage: number) {
  query.currentPage = newPage;
   // 如果有搜索关键词，则添加到查询参数中
  loadDataList();
}

// 加载食堂数据
const loadDataList = async () => {
    try {  
    const response = await axiosInstance.post(`/api/supervisor/queryCanteenList`, query);
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
 // 食堂信息修改集合
  const canteenEditInfo = reactive ( {
  canteenId: '',
  canteenCover: '',
  canteenName: '',
  canteenFlavorType: null,
  canteenTableStatus: null,
  canteenOpenStatus: null,
  canteenTotalTables: null,
  canteenNumberOfDiners: null,
  canteenCurrentAvailableTables: null,
  canteenIntroduce: '',
  canteenOfferFood: '',
  canteenOpenTime: '',
  canteenClosedTime: '',
  canteenRating: null,
  canteenTrumpUp: null,
  canteenTrumpDown: null,
})

// 图片上传成功后，回调函数获取上传文件的文件名
function handleAvatarSuccessUpdate(response : any, file : any ) {
    canteenEditInfo.canteenCover = "https://hotel-sys-ayuan.oss-cn-beijing.aliyuncs.com/" + file.name;
    notice("图片成功上传, 但要完整提交后才能更新图片！")  
}


// 编辑逻辑处理
const handleEdit = (index: number, row: Canteen) => {
  dialogUpdateVisible.value = true;
  canteenEditInfo.canteenId = row.canteenId;
  canteenEditInfo.canteenCover = row.canteenCover;
  canteenEditInfo.canteenName = row.canteenName;
  canteenEditInfo.canteenFlavorType = row.canteenFlavorType;
  canteenEditInfo.canteenOpenStatus = row.canteenOpenStatus;
  canteenEditInfo.canteenTotalTables = row.canteenTotalTables;
  canteenEditInfo.canteenIntroduce = row.canteenIntroduce;
  canteenEditInfo.canteenOfferFood = row.canteenOfferFood;
  canteenEditInfo.canteenOpenTime = row.canteenOpenTime;
  canteenEditInfo.canteenClosedTime = row.canteenClosedTime;
}

// 清除缓存
const clearLastCache = () => {
  dialogAddVisible.value = true;
  canteenEditInfo.canteenId = '';
  canteenEditInfo.canteenCover = '';
  canteenEditInfo.canteenName = '';
  canteenEditInfo.canteenFlavorType = null;
  canteenEditInfo.canteenOpenStatus = null;
  canteenEditInfo.canteenTotalTables = null;
  canteenEditInfo.canteenIntroduce = '';
  canteenEditInfo.canteenOfferFood = '';
  canteenEditInfo.canteenOpenTime = '';
  canteenEditInfo.canteenClosedTime = '';
}


// 更新食堂信息
const updateCanteenInfo = async ( formEl: FormInstance | undefined ) => {  
    if (!formEl) {
        return;
      } 
      await formEl.validate((valid, fields) => {
         if (valid) {
              axiosInstance.post(`/api/supervisor/updateCanteenInfo`, canteenEditInfo )
            .then(response => {
              notice("变更当前食堂信息成功！");
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

// 删除当前食堂信息
async function deleteCurrentCanteenInfo  (index: number, row: Canteen) {
  const result = await ElMessageBox.confirm('确认删除当前食堂的信息吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  });

  if (result) {
    await axiosInstance.delete(`/api/supervisor/deleteCurrentCanteenInfo?canteenId=${row.canteenId}`)
      .then(response => {
        notice("删除当前食堂的信息成功！");
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