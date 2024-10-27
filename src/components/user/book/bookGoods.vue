<template>
<div v-if="showCard && dataList.length > 0"  class="card-container" >
    <el-card  
    v-for="(goods) in dataList"
    :key="goods.goodsId"
    class="hover-shadow" style="max-height: 250px; max-width: 250px; overflow: hidden; "
    >
        <template #header >
         <div class="card-header-content">
          {{ goods.goodsName }}
         </div>
        </template>
         <img
            :src="goods.goodsCover"
            style="width: 100%; height: auto; object-fit: contain;"
            @click="showSingleGoodsInfo(goods)"
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

  <!-- 商品具体信息 -->
   <el-dialog v-model="dialogVisible" title="商品具体信息"  :width="550" :height="300">
        <el-form ref="ruleFormRef"  >
        <el-form-item label="商品图片" :label-width="formLabelWidth">
            <img :src="goodsEditInfo.goodsCover"  style="width: 40px; height: 40px;" />
        </el-form-item>
         <el-form-item label="商品名称" :label-width="formLabelWidth"  >
         <el-input disabled autocomplete="off"  :value="goodsEditInfo.goodsName" />  
        </el-form-item>
        <el-form-item label="商品信息" :label-width="formLabelWidth"  >
         <el-input disabled autocomplete="off"  :value="goodsEditInfo.goodsInfo" type="textarea"/>  
        </el-form-item>
         <el-form-item label="商品价格" :label-width="formLabelWidth"   >
         <el-input disabled type="number" autocomplete="off" :value="goodsEditInfo.price"/> 
        </el-form-item>
        <el-form-item label="商品当前库存" :label-width="formLabelWidth"   >
         <el-input disabled type="number" autocomplete="off" :value="goodsEditInfo.goodsAmount"/> 
        </el-form-item>
        <el-form-item label="商品生产日期" :label-width="formLabelWidth"  >
           <el-input disabled  autocomplete="off" v-model="goodsEditInfo.productTime"/> 
        </el-form-item>
         <el-form-item label="商品过期日期" :label-width="formLabelWidth"  >
           <el-input disabled  autocomplete="off" v-model="goodsEditInfo.expireTime"/> 
        </el-form-item>
         <el-form-item label="好评人数" :label-width="formLabelWidth"  >
           <el-input disabled  autocomplete="off" :value="goodsEditInfo.goodsTrumpUp"/> 
        </el-form-item>
         <el-form-item label="差评人数" :label-width="formLabelWidth"  >
           <el-input disabled  autocomplete="off" :value="goodsEditInfo.goodsTrumpDown"/> 
        </el-form-item>
        <el-form-item label="商品好评率" :label-width="formLabelWidth" >
         <el-input disabled  autocomplete="off" :value="`${(goodsEditInfo.positiveRating * 100).toFixed(2)}%`"/> 
        </el-form-item>
        <el-form-item label="选择商品数量" :label-width="formLabelWidth" >
          <div style="display: grid; grid-template-columns: 2fr 1fr;">
              <el-input disabled type="number" autocomplete="off" :value="goodsEditInfo.currentSelectedGoodsAmount"/>
            <div class="button-group" v-if="goodsEditInfo.goodsStatus === 0">
                <el-button type="success" @click="increaseQuantity">添加一件</el-button>
                <el-button type="warning" @click="decreaseQuantity">减少一件</el-button>
            </div> 
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
      <span class="dialog-footer">
        <el-button @click="clearLastSelectedCache()">取消</el-button>
        <el-button v-if="goodsEditInfo.goodsStatus === 0" type="success" @click="bookGoods( goodsEditInfo.goodsId, goodsEditInfo.currentSelectedGoodsAmount, goodsEditInfo.price)"> 下单 </el-button>
        <el-button disabled v-if="goodsEditInfo.goodsStatus === 1" type="danger" > 缺货 </el-button>
        <el-button disabled  v-if="goodsEditInfo.goodsStatus === 2" type="warning" > 下架 </el-button>
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
import { useRouter } from 'vue-router';
import axiosInstance from '@/axios/index';
import { ElMessageBox } from 'element-plus';
import router from '@/router';
import type { FormRules } from 'element-plus';
import type { FormInstance } from 'element-plus';



const ruleFormRef = ref<FormInstance>();  
const dialogVisible = ref(false);
const total = ref(10);  // 总数据量
const dataList = ref<Goods[]>([]);
const userId = ref('');
const showCard = ref(false);
const roomAvatar = ref('');
const filledOwnerIdCardCode = ref(false);


// 商品数量控制器
const increaseQuantity  = () => {
  if (goodsEditInfo.currentSelectedGoodsAmount >= 10) {
    noticeError("当前用户所购买商品数量已超出可购买数量的最大值！")
    return;
  }
  goodsEditInfo.currentSelectedGoodsAmount++;
}
const decreaseQuantity  = () => {
  if (goodsEditInfo.currentSelectedGoodsAmount <= 0) {
    noticeError("商品数量已达数量可选的最小值！")
    return;
  }
  goodsEditInfo.currentSelectedGoodsAmount--;
}


// 重置表单
const clearLastSelectedCache = () => {
  dialogVisible.value = false;
   goodsEditInfo.currentSelectedGoodsAmount = 0;
}


// 业务提示框弹出
const notice = ($content : any) => {
  ElMessage({
          message: $content,
          type: 'success',
        });
}


// 下单商品
async function bookGoods (goodsId : number, currentSelectedGoodsAmount: number, price: number) {
  console.log(currentSelectedGoodsAmount);
    if (currentSelectedGoodsAmount === 0) {
      noticeError("当前用户选购商品数量为0,不能进行下单,请选择商品数量再进行下单!");
      return;
    }
    dialogVisible.value = false;
    const result = await ElMessageBox.confirm('确认下单当前商品吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  });

  if (result) {
    await axiosInstance.get(`/api/user/bookGoods?goodsId=${goodsId}&userId=${userId.value}&currentSelectedGoodsAmount=${currentSelectedGoodsAmount}&price=${price}`)
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
    goodsEditInfo.currentSelectedGoodsAmount = 0;
}


interface Goods {
  goodsId: string
  goodsCover: string
  goodsName: string
  price: number
  goodsStatus: number
  goodsAmount: number
  currentSelectedGoodsAmount: number
  goodsInfo: string
  productTime: string
  expireTime: string
  positiveRating: number
  goodsTrumpUp: number
  goodsTrumpDown: number
}

 // 商品信息修改集合
  const goodsEditInfo = reactive<Goods> ( {
  goodsId: '',
  goodsCover: '',
  goodsName: '',
  goodsAmount: null,
  price: null,
  currentSelectedGoodsAmount: 0,
  goodsStatus: null,
  goodsInfo: '',
  productTime: '',
  expireTime: '',
  positiveRating: null,
  goodsTrumpUp: null,
  goodsTrumpDown: null
})

function showSingleGoodsInfo(goods: any) {
  dialogVisible.value = true;
  goodsEditInfo.goodsId = goods.goodsId;
  goodsEditInfo.goodsCover = goods.goodsCover;
  goodsEditInfo.goodsName = goods.goodsName;
  goodsEditInfo.price = goods.price;                   
  goodsEditInfo.goodsStatus = goods.goodsStatus;
  goodsEditInfo.goodsInfo = goods.goodsInfo;
  goodsEditInfo.goodsAmount = goods.goodsAmount;
  goodsEditInfo.productTime = goods.productTime;
  goodsEditInfo.expireTime = goods.expireTime;
  goodsEditInfo.positiveRating = goods.positiveRating;
  goodsEditInfo.goodsTrumpUp = goods.goodsTrumpUp;
  goodsEditInfo.goodsTrumpDown = goods.goodsTrumpDown;

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
    const response = await axiosInstance.post(`/api/supervisor/queryGoodsList`, query);
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