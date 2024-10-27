<template>
<!-- 商品信息管理 -->
  <div class="right" style="margin-bottom: 10px;" >
       <el-button  style="margin-right: 1200px;" type="success" :icon="Search" @click="clearLastCache" >录入商品信息</el-button>
       <el-input v-model="query.keyword" style="width: 360px" placeholder="请输入关键字" />
       <el-button  style="margin-left: 10px;" type="primary" :icon="Search" @click="fetchData(1)" >搜索</el-button>
    </div>
  <el-table :data="dataList" border  height="700"  :row-class-name="tableRowRule">
    <el-table-column align="center" fixed label="商品图片"  width="110px">
      <template #default="scope">
        <div class="image-container"  @click="showDialog(scope.row.goodsCover)"> 
             <img :src="scope.row.goodsCover"  style="width: 50px; height: 50px;" />
        </div>
      </template>
    </el-table-column>
    <el-table-column align="center" label="商品名称" prop="goodsName" width="140px"/>
     <el-table-column align="center" label="商品价格" prop="price" width="100px" />
     <el-table-column align="center" label="商品数量" prop="goodsAmount" width="100px" />
     <el-table-column align="center" label="商品类型" width="140px" >
        <template #default="scope">
          <span>{{ scope.row.goodsType === 0 ? '食品' : '洗漱洗涤用品'}}</span>
        </template>
    </el-table-column>
      <el-table-column align="center" label="商品状态" width="130px" >
        <template #default="scope">
          <span>{{ scope.row.goodsStatus === 0 ? '库存充足' :
                   scope.row.goodsStatus === 1 ? '缺货' : '下架'}}</span>
        </template>
    </el-table-column>
     <el-table-column align="center" label="商品信息" prop="goodsInfo"  width="250px"/>
    <el-table-column align="center" label="商品生产日期" prop="productTime"  width="250px" />
    <el-table-column align="center" label="商品过期日期" prop="expireTime"  width="180px"  />
    <el-table-column align="center" label="商品客户满意度" width="130">
        <template #default="scope">
          <span v-if="scope.row.positiveRating != null"> {{ (scope.row.positiveRating * 100) + "%" }} </span>
        </template>
    </el-table-column>
     <el-table-column align="center" label="商品好评数目" prop="goodsTrumpUp" width="120px" />
      <el-table-column align="center" label="商品差评数目" prop="goodsTrumpDown" width="120px"  />
    <el-table-column align="center" fixed="right" label="操作" width="130" >
      <template #default="scope" >
        <div style="display: flex; justify-content: space-between;  align-items: center">
            <el-button color="#626aef" 
            :dark="isDark" plain size="small" 
            @click="handleEdit(scope.$index, scope.row)"
            >
          编辑
        </el-button>
        <el-button
          size="small"
          color="#001000"
          :dark="isDark" plain
          v-bind:disabled="scope.row.goodsStatus === 0 || scope.row.goodsStatus === 1 "
          @click="deleteCurrentGoodsInfo(scope.$index, scope.row)"
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

<!-- 新增商品信息 -->
   <el-dialog v-model="dialogAddVisible" title="录入商品信息"  :width="650" :height="430">
      <el-form ref="ruleFormRef" :rules="rules" :model="goodsEditInfo">
        <el-form-item label="商品图片" :label-width="formLabelWidth">
          <el-tooltip class="item" effect="dark" content="点击录入商品图片" placement="top">
            <el-upload
            class="avatar-uploader"
            action="http://localhost:8081/api/supervisor/uploadGoodsCover"
            :show-file-list="false"
            :before-upload="beforeAvatarUpload"
            :on-success="handleAvatarSuccessUpdate" >
              <div class="image-container"> 
                <img :src="goodsEditInfo.goodsCover"  style="width: 40px; height: 40px;" />
              </div>
            </el-upload>
          </el-tooltip>
        </el-form-item>
     <el-form-item align="center" label="商品名称" prop="goodsName" >
          <el-input v-model="goodsEditInfo.goodsName" autocomplete="off" />
     </el-form-item>
     <el-form-item align="center" label="商品数量" prop="goodsAmount" >
          <el-input type="number" v-model="goodsEditInfo.goodsAmount" autocomplete="off" />
     </el-form-item>
     <el-form-item align="center" label="商品价格" prop="price"  >
        <el-input type="number" v-model="goodsEditInfo.price" autocomplete="off" />
     </el-form-item>
     <el-form-item align="center" label="商品类型" prop="goodsType" >
         <el-radio-group v-model="goodsEditInfo.goodsType">
             <el-radio :value="0" size="large">食品</el-radio>
             <el-radio :value="1" size="large">洗漱洗涤用品</el-radio>
         </el-radio-group>
    </el-form-item>
    <el-form-item align="center" label="商品状态"  prop="goodsStatus" >
        <el-radio-group v-model="goodsEditInfo.goodsStatus">
            <el-radio :value="0" size="large">库存充足</el-radio>
            <el-radio :value="2" size="large">下架</el-radio>
        </el-radio-group>
    </el-form-item>
     <el-form-item align="center" label="商品信息" prop="goodsInfo"  >
        <el-input v-model="goodsEditInfo.goodsInfo" autocomplete="off" type="textarea" />
     </el-form-item>
     <el-form-item label="商品生产时间" :label-width="formLabelWidth" prop="productTime">
      <el-date-picker
        v-model="goodsEditInfo.productTime"
        type="datetime"
        value-format="YYYY-MM-DD HH:mm:ss"
        placeholder="请设置商品生产时间"
      />
     </el-form-item>
    <el-form-item label="商品过期时间" :label-width="formLabelWidth" prop="expireTime">
      <el-date-picker
        v-model="goodsEditInfo.expireTime"
        type="datetime"
        value-format="YYYY-MM-DD HH:mm:ss"
        placeholder="请设置商品过期时间"
      />
    </el-form-item> 
      </el-form>
      <template #footer>
      <span class="dialog-footer">
      <el-button @click="dialogAddVisible = false">取消</el-button>
        <el-button type="primary" @click="addGoodsInfo(ruleFormRef)">提交</el-button>
         <el-button  @click="resetForm(ruleFormRef)">重置</el-button>
      </span>
      </template>
   </el-dialog>  

 <!-- 编辑商品信息 -->
   <el-dialog v-model="dialogUpdateVisible" title="更新商品信息"  :width="650" :height="430">
         <el-form ref="ruleFormRef" :rules="rules" :model="goodsEditInfo">
        <el-form-item label="商品图片" :label-width="formLabelWidth">
          <el-tooltip class="item" effect="dark" content="点击更换商品图片" placement="top">
          <el-upload
           class="avatar-uploader"
           action="http://localhost:8081/api/supervisor/uploadGoodsCover"
           :show-file-list="false"
           :before-upload="beforeAvatarUpload"
           :on-success="handleAvatarSuccessUpdate" >
            <div class="image-container"> 
              <img :src="goodsEditInfo.goodsCover"  style="width: 40px; height: 40px;" />
            </div>
          </el-upload>
          </el-tooltip>
        </el-form-item>
     <el-form-item align="center" label="商品名称" prop="goodsName" >
          <el-input placeholder="请输入......" v-model="goodsEditInfo.goodsName" autocomplete="off" />
     </el-form-item>
     <el-form-item align="center" label="商品价格" prop="price"  >
        <el-input placeholder="请输入金额......" type="number" v-model="goodsEditInfo.price" autocomplete="off" />
     </el-form-item>
     <el-form-item align="center" label="商品类型"  prop="goodType" >
         <el-radio-group v-model="goodsEditInfo.goodsType">
             <el-radio :value="0" size="large">食品</el-radio>
             <el-radio :value="1" size="large">洗漱洗涤用品</el-radio>
         </el-radio-group>
    </el-form-item>
     <el-form-item align="center" label="商品数量" prop="goodsAmount" >
          <el-input type="number" v-model="goodsEditInfo.goodsAmount" autocomplete="off" />
     </el-form-item>
    <el-form-item align="center" label="商品状态"  prop="goodsStatus" >
      <el-radio-group v-model="goodsEditInfo.goodsStatus">
            <el-radio :value="0" size="large">库存充足</el-radio>
            <el-radio :value="2" size="large">下架</el-radio>
      </el-radio-group>
    </el-form-item>
     <el-form-item align="center" label="商品信息" prop="goodsInfo"  >
        <el-input placeholder="请输入......" v-model="goodsEditInfo.goodsInfo" autocomplete="off" type="textarea" />
     </el-form-item>
     <el-form-item label="商品生产时间" :label-width="formLabelWidth" prop="productTime">
      <el-date-picker
        type="datetime"  
        value-format="YYYY-MM-DD HH:mm:ss"
        v-model="goodsEditInfo.productTime"
        placeholder="请设置商品生产时间"
      />
     </el-form-item>
    <el-form-item label="商品过期时间" :label-width="formLabelWidth" prop="expireTime">
      <el-date-picker
        type="datetime"
        value-format="YYYY-MM-DD HH:mm:ss"  
        v-model="goodsEditInfo.expireTime"
        placeholder="请设置商品过期时间"
      />
    </el-form-item> 
      </el-form>
      <template #footer>
      <span class="dialog-footer">
      <el-button @click="dialogUpdateVisible = false">取消</el-button>
        <el-button type="primary" @click="updateGoodsInfo(ruleFormRef)">提交</el-button>
      </span>
      </template>
   </el-dialog>

  <!-- 商品图片预览 -->
   <el-dialog v-model="dialogVisible" height="800px" width="1000px" title="商品图片预览">
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
const dataList = ref<Goods[]>([])  // 原始数据列表


const query = reactive ( {
    currentPage: 1,
    size: 15,
    keyword: ''
  }  
)
  
const rules = reactive<FormRules<Goods>>({

  goodsName: [
    { required: true, message: '商品名字不能为空', trigger: 'blur' },
    { min: 1, max: 20, message: '字符长度应该在1到20之间', trigger: 'change' },
    {
        validator: (rule, value, callback) => {
            if (value && value.includes(' ')) {
                callback(new Error('商品名字中不能包含空格'));
            } else {
                callback();
            }
        },
        trigger: 'change'
    }
  ],
  goodsInfo: [
     { required: true, message: '商品信息不能为空', trigger: 'blur' },
    { min: 1, max: 150, message: '字符长度应该在1到150之间', trigger: 'change' },
    {
        validator: (rule, value, callback) => {
            if (value && value.includes(' ')) {
                callback(new Error('商品信息中不能包含空格'));
            } else {
                callback();
            }
        },
        trigger: 'change'
    }
  ],
  goodsStatus: [
     { required: true, message: '请选择一个商品当前的状态', trigger: 'change' },
  ],
  goodsType: [
     { required: true, message: '请选择一个商品类型', trigger: 'change' },
  ],
  price: [
    { required: true, message: '请输入商品的价格', trigger: 'blur' },
        {
           validator: (rule, value, callback) => {
             if (value <= 0) {
                callback(new Error('价格必须为正数'));
              } else {
                // 验证成功
                callback();
              }
            },
            trigger: 'change',
        }, 
  ],
  goodsAmount: [
    { required: true, message: '请输入商品的数量', trigger: 'blur' },
        {
           validator: (rule, value, callback) => {
             if (value <= 0) {
                callback(new Error('数量必须为正数'));
              } else {
                // 验证成功
                callback();
              }
            },
            trigger: 'change',
        }, 
  ],
  productTime: [
    {  required: true,  message: '商品生产时间不能为空',  trigger: 'change' },
  ],
   expireTime: [
    {  required: true,  message: '商品过期时间不能为空',  trigger: 'change' },
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
// 检查日期规范
function checkDate() {
  const productDate = new Date(goodsEditInfo.productTime);
  const expireDate = new Date(goodsEditInfo.expireTime);
  return productDate < expireDate;
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

   
// 录入商品信息
const addGoodsInfo = async ( formEl: FormInstance | undefined ) => {
    
     if (!formEl) {
        return;
     }
      
     if (!checkDate()) {
        noticeError("当前生产时间大于或等于最佳使用时间，请检查后再填写!")
        return;
     }

     await formEl.validate((valid, fields) => {
          if (valid) {
              axiosInstance.post(`/api/supervisor/addGoodsInfo`, goodsEditInfo )
            .then(response => {
              notice("录入当前商品信息成功！");
              dialogAddVisible.value = false;
              // 在修改后刷新数据
              loadDataList();
              })
              // 不管是否录入成功，都要将获得的图片路径进行删除
              goodsEditInfo.goodsCover = '';
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

// 当商品属于下架时，进行标黄
const tableRowRule = ({
  row,
}: {
  row: Goods
}) => {
  if (row.goodsStatus === 2) {
    return 'warning-row'
  }
}

interface Goods {
  goodsId: string
  goodsCover: string
  goodsName: string
  goodsAmount: string
  price: number
  goodsType: number
  goodsStatus: number
  goodsInfo: string
  productTime: string
  expireTime: string
  positiveRating: number
  goodsTrumpUp: number
  goodsTrumpDown: number
}


  // 自定义搜索
function fetchData(newPage: number) {
  query.currentPage = newPage;
   // 如果有搜索关键词，则添加到查询参数中
  loadDataList();
}

// 加载商品数据
const loadDataList = async () => {
    try {  
    const response = await axiosInstance.post(`/api/supervisor/queryGoodsList`, query);
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
 // 商品信息修改集合
  const goodsEditInfo = reactive ( {
  goodsId: '',
  goodsCover: '',
  goodsName: '',
  goodsAmount: null,
  price: null,
  goodsType: null,
  goodsStatus: null,
  goodsInfo: '',
  productTime: '',
  expireTime: '',
  positiveRating: null,
  goodsTrumpUp: null,
  goodsTrumpDown: null
})

// 图片上传成功后，回调函数获取上传文件的文件名
function handleAvatarSuccessUpdate(response : any, file : any ) {
    goodsEditInfo.goodsCover = "https://hotel-sys-ayuan.oss-cn-beijing.aliyuncs.com/" + file.name;
    notice("图片成功上传, 但要完整提交后才能更新图片！")  
}


// 编辑逻辑处理
const handleEdit = (index: number, row: Goods) => {
  dialogUpdateVisible.value = true;
  goodsEditInfo.goodsId = row.goodsId;
  goodsEditInfo.goodsCover = row.goodsCover;
  goodsEditInfo.goodsName = row.goodsName;
  goodsEditInfo.price = row.price;
  goodsEditInfo.goodsType = row.goodsType;
  goodsEditInfo.goodsStatus = row.goodsStatus;
  goodsEditInfo.goodsInfo = row.goodsInfo;
  goodsEditInfo.goodsAmount = row.goodsAmount;
  goodsEditInfo.productTime = row.productTime;
  goodsEditInfo.expireTime = row.expireTime;
  goodsEditInfo.positiveRating = row.positiveRating;
  goodsEditInfo.goodsTrumpUp = row.goodsTrumpUp;
  goodsEditInfo.goodsTrumpDown = row.goodsTrumpDown;
}

// 清除缓存
const clearLastCache = () => {
  dialogAddVisible.value = true;
  goodsEditInfo.goodsId = '';
  goodsEditInfo.goodsCover = '';
  goodsEditInfo.goodsName = '';
  goodsEditInfo.price = null;
  goodsEditInfo.goodsAmount = null;
  goodsEditInfo.goodsType = null;
  goodsEditInfo.goodsStatus = null;
  goodsEditInfo.goodsInfo = '';
  goodsEditInfo.productTime = '';
  goodsEditInfo.expireTime = '';
  goodsEditInfo.positiveRating = null;
  goodsEditInfo.goodsTrumpUp = null;
  goodsEditInfo.goodsTrumpDown = null;
}


// 更新商品信息
const updateGoodsInfo = async ( formEl: FormInstance | undefined ) => {  
    if (!formEl) {
        return;
      } 

    if (!checkDate()) {
        noticeError("当前生产时间大于或等于最佳使用时间，请检查后再填写!")
        return;
    }
      await formEl.validate((valid, fields) => {
         if (valid) {
              axiosInstance.post(`/api/supervisor/updateGoodsInfo`, goodsEditInfo )
            .then(response => {
              notice("变更当前商品信息成功！");
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

// 删除当前商品信息
async function deleteCurrentGoodsInfo  (index: number, row: Goods) {
  const result = await ElMessageBox.confirm('确认删除当前商品的信息吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  });

  if (result) {
    await axiosInstance.delete(`/api/supervisor/deleteCurrentGoodsInfo?goodsId=${row.goodsId}`)
      .then(response => {
        notice("删除当前商品的信息成功！");
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