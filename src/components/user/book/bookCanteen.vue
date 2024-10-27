<template>
<div v-if="showCard && dataList.length > 0"  class="card-container" >
    <el-card  
    v-for="(canteen) in dataList"
    :key="canteen.canteenId"
    class="hover-shadow" style="max-height: 250px; max-width: 250px; overflow: hidden; "
    >
        <template #header >
         <div class="card-header-content">
          {{ canteen.canteenName }}
         </div>
        </template>
         <img
            :src="canteen.canteenCover"
            style="width: 100%; height: auto; object-fit: contain;"
            @click="showSingleCanteenInfo(canteen)"
        />
    </el-card>
</div>

 <!-- 分页 -->
   <div style="margin-top: 10px;">
        <el-pagination
      :page-size="query.size"
      :disabled="disabled"
      :background="background"
      layout="total, prev, pager, next, jumper"
      :total="total"
      @size-change="handleSizeChange"
      @current-change="fetchData"
    />
  </div>

  <!-- 餐厅具体信息 -->
   <el-dialog v-model="dialogVisible" title="餐厅具体信息"  :width="550" :height="300">
        <el-form ref="ruleFormRef"  >
        <el-form-item label="餐厅图片" :label-width="formLabelWidth">
            <img :src="canteenEditInfo.canteenCover"  style="width: 40px; height: 40px;" />
        </el-form-item>
         <el-form-item label="餐厅名称" :label-width="formLabelWidth"  >
         <el-input disabled autocomplete="off"  :value="canteenEditInfo.canteenName" />  
        </el-form-item>
         <el-form-item label="餐厅介绍" :label-width="formLabelWidth"  >
         <el-input disabled autocomplete="off"  :value="canteenEditInfo.canteenIntroduce" type="textarea"/>  
        </el-form-item>
         <el-form-item label="餐厅菜谱" :label-width="formLabelWidth"  >
         <el-input disabled autocomplete="off"  :value="canteenEditInfo.canteenOfferFood" type="textarea"/>  
        </el-form-item>
       <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="餐厅类型" :label-width="formLabelWidth">
              <el-input disabled autocomplete="off" :value="canteenEditInfo.canteenFlavorType"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="餐厅总位置数量" :label-width="formLabelWidth">
              <el-input disabled type="number" autocomplete="off" :value="canteenEditInfo.canteenTotalTables"/>
            </el-form-item>
          </el-col>
        </el-row>
       <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="餐厅当前用餐人数" :label-width="formLabelWidth">
              <el-input disabled autocomplete="off" v-model="canteenEditInfo.canteenNumberOfDiners"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="餐厅当前空余位置" :label-width="formLabelWidth">
              <el-input disabled autocomplete="off" v-model="canteenEditInfo.canteenCureentAvailableTales"/>
            </el-form-item>
          </el-col>
      </el-row>
      <el-row :gutter="20">
         <el-col :span="12">
            <el-form-item label="好评人数" :label-width="formLabelWidth"  >
              <el-input disabled  autocomplete="off" :value="canteenEditInfo.canteenTrumpUp"/> 
            </el-form-item>
         </el-col>
          <el-col :span="12">
            <el-form-item label="差评人数" :label-width="formLabelWidth"  >
              <el-input disabled  autocomplete="off" :value="canteenEditInfo.canteenTrumpDown"/> 
            </el-form-item>
          </el-col>
          <el-col :span="12">
              <el-form-item label="餐厅好评率" :label-width="formLabelWidth" >
                  <el-input disabled  autocomplete="off" :value="`${(canteenEditInfo.canteenRating * 100).toFixed(2)}%`"/> 
              </el-form-item>
          </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
         <el-form-item label="餐厅营业时间" :label-width="formLabelWidth" >
           <el-input disabled  autocomplete="off" :value="canteenEditInfo.canteenOpenTime"/> 
        </el-form-item>
        </el-col>
        <el-col :span="12">
         <el-form-item label="餐厅打烊时间" :label-width="formLabelWidth" >
           <el-input disabled  autocomplete="off" :value="canteenEditInfo.canteenClosedTime"/> 
        </el-form-item>
        </el-col>
      </el-row>
      <el-col :span="12">
        <el-form-item label="选择用餐人数" :label-width="formLabelWidth" >
          <div style="display: grid; grid-template-columns: 2fr 1fr;">
            <el-input disabled type="number" autocomplete="off" :value="canteenEditInfo.currentSelectedPersonAmount" />
             </div>
          </el-form-item>
      </el-col>
            <div class="button-group" v-if="canteenEditInfo.canteenTableStatus === 0 && canteenEditInfo.canteenOpenStatus == 2">
                <el-button type="success" @click="increaseQuantity">添加一人</el-button>
                <el-button type="warning" @click="decreaseQuantity">减少一人</el-button>
            </div> 
             <div class="button-group" v-if="canteenEditInfo.canteenTableStatus === 1 && canteenEditInfo.canteenOpenStatus == 2">
                <el-button disabled type="warning">人数已满</el-button>
            </div> 
         
       
      </el-form>
      <template #footer>
      <span class="dialog-footer">
        <el-button @click="clearLastSelectedCache()">取消</el-button>
        <el-button v-if="canteenEditInfo.canteenOpenStatus === 2" type="success" @click="bookCanteen( canteenEditInfo.canteenId, canteenEditInfo.currentSelectedPersonAmount)"> 预定 </el-button>
        <el-button disabled v-if="canteenEditInfo.canteenOpenStatus === 1" type="warning" > 打烊 </el-button>
        <el-button disabled  v-if="canteenEditInfo.canteenOpenStatus === 0" type="danger" > 暂停营业 </el-button>
      </span>
      </template>
   </el-dialog>   

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
import { useRouter, viewDepthKey } from 'vue-router';
import axiosInstance from '@/axios/index';
import { ElMessageBox } from 'element-plus';
import router from '@/router';
import type { FormRules } from 'element-plus';
import type { FormInstance } from 'element-plus';



const ruleFormRef = ref<FormInstance>();  
const dialogVisible = ref(false);
const total = ref(10);  // 总数据量
const dataList = ref<Canteen[]>([]);
const userId = ref('');
const showCard = ref(false);
const roomAvatar = ref('');
const filledOwnerIdCardCode = ref(false);


// 商品数量控制器
const increaseQuantity  = () => {
  console.log(canteenEditInfo.currentSelectedPersonAmount);
  if (canteenEditInfo.currentSelectedPersonAmount >= 5) {
    noticeError("用餐人数量已超出最大数量的最大值！")
    return;
  }
  if (canteenEditInfo.currentSelectedPersonAmount >= canteenEditInfo.canteenCureentAvailableTales ) {
    noticeError("用餐人数量已超出当前空余位置！")
    return;
  }
  canteenEditInfo.currentSelectedPersonAmount++;
}
const decreaseQuantity  = () => {
  if (canteenEditInfo.currentSelectedPersonAmount <= 0) {
    noticeError("用餐人数量已达数量可选的最小值！")
    return;
  }
  canteenEditInfo.currentSelectedPersonAmount--;
}


// 重置表单
const clearLastSelectedCache = () => {
  dialogVisible.value = false;
   canteenEditInfo.currentSelectedPersonAmount = 0;
}


// 业务提示框弹出
const notice = ($content : any) => {
  ElMessage({
          message: $content,
          type: 'success',
        });
}


// 下单餐馆
async function bookCanteen (canteenId : number, currentSelectedPersonAmount: number) {
    if (currentSelectedPersonAmount === 0) {
      noticeError("当前用餐人数数量为0,不能进行下单,请选择合理的用餐数再进行下单!");
      return;
    }
    dialogVisible.value = false;
    const result = await ElMessageBox.confirm('确认预定当前餐馆吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  });
  console.log(canteenId)
  if (result) {
    await axiosInstance.get(`/api/user/bookCanteen?canteenId=${canteenId}&userId=${userId.value}&currentSelectedPersonAmount=${currentSelectedPersonAmount}`)
      .then(response => {
        if (response.data.code === 1) {
            notice(response.data.data);
            // 在删除后刷新数据
            filledOwnerIdCardCode.value = false;
            loadDataList();
        } else {
            noticeError(response.data.msg);
            noticeError("如需要退订，请到我的订单中修改！");
        }
     
      })
  } 
    canteenEditInfo.currentSelectedPersonAmount = 0;
}


interface Canteen {
  canteenId: string
  canteenCover: string
  canteenName: string
  canteenFlavorType: number
  canteenTableStatus: number
  currentSelectedPersonAmount: number
  canteenOpenStatus: number
  canteenNumberOfDiners: number
  canteenTotalTables: number
  canteenCureentAvailableTales: number
  canteenIntroduce: string
  canteenOfferFood: string
  canteenOpenTime: string
  canteenClosedTime: string
  canteenRating: number
  canteenTrumpUp: number
  canteenTrumpDown: number
}

 // 商品信息修改集合
  const canteenEditInfo = reactive<Canteen> ( {
  canteenId: "",
  canteenCover: "",
  canteenName: "",
  canteenFlavorType: null,
  canteenTableStatus: null,
  canteenOpenStatus: null,
  canteenNumberOfDiners: null,
  currentSelectedPersonAmount: 0,
  canteenTotalTables: null,
  canteenCureentAvailableTales: null,
  canteenIntroduce: "",
  canteenOfferFood: "",
  canteenOpenTime: "",
  canteenClosedTime: "",
  canteenRating: null,
  canteenTrumpUp: null,
  canteenTrumpDown: null,
})

function showSingleCanteenInfo(canteen: any) {
  dialogVisible.value = true;
  canteenEditInfo.canteenId = canteen.canteenId,
  canteenEditInfo.canteenCover = canteen.canteenCover,
  canteenEditInfo.canteenName = canteen.canteenName,
  canteenEditInfo.canteenFlavorType = canteen.canteenFlavorType,
  canteenEditInfo.canteenTableStatus = canteen.canteenTableStatus,
  canteenEditInfo.canteenOpenStatus = canteen.canteenOpenStatus,
  canteenEditInfo.canteenNumberOfDiners = canteen.canteenNumberOfDiners,
  canteenEditInfo.canteenTotalTables = canteen.canteenTotalTables,
  canteenEditInfo.canteenIntroduce = canteen.canteenIntroduce,
  canteenEditInfo.canteenOfferFood = canteen.canteenOfferFood,
  canteenEditInfo.canteenOpenTime = canteen.canteenOpenTime,
  canteenEditInfo.canteenClosedTime = canteen.canteenClosedTime,
  canteenEditInfo.canteenOpenTime = canteen.canteenOpenTime,
  canteenEditInfo.canteenRating = canteen.canteenRating,
  canteenEditInfo.canteenTrumpUp = canteen.canteenTrumpUp,
  canteenEditInfo.canteenTrumpDown = canteen.canteenTrumpDown
  canteenEditInfo.canteenCureentAvailableTales = canteenEditInfo.canteenTotalTables - canteenEditInfo.canteenNumberOfDiners
}


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


function fetchData(newPage: number) {
  query.currentPage = newPage;
   // 如果有搜索关键词，则添加到查询参数中
  loadDataList();
}

// 修改每页显示数据的条数
function handleSizeChange (newSize : number) {
  query.size = newSize;
  loadDataList();
}



// 刷新列表数据
const loadDataList = async () => {
    try {  
    const response = await axiosInstance.post(`/api/supervisor/queryCanteenList`, query);
    // 判断token是否无效或过期
    if (response.headers['notice'] === 'unable') {
        noticeError("当前token过期或无效,请重新登录");
        // 移除原有的token
        localStorage.removeItem('userToken');
        // 跳转至登录页
        router.push("/");
        return;
    }
    dataList.value = response.data.data.list;
    total.value = response.data.data.total;
    }
    catch (error) {
    // 处理请求错误，例如打印错误信息或者更新UI等
    console.error("Error fetching data:", error);
  }
}




// 查看用户是否已经填完必填的个人信息
function checkUserIsFinishInfo() {
    userId.value = localStorage.getItem('userId');
    axiosInstance.get(`/api/user/checkUserInfoAlreadyFinished?userId=${userId.value}`)
    .then(response => {
        // 用户已经按照要求将必要信息填写完毕
        if (response.data.code == 1) {
            // 展示对应的列表
            loadDataList();
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

.card-container {
  display: grid;
  grid-template-columns: repeat(5, 1fr); /* 创建5列，每列宽度相等 */
  gap: 20px; /* 设置网格项之间的间隙 */
  width: 100%; /* 或者设置为具体宽度，取决于您的布局需求 */
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