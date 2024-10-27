导出订单<template>
<!--  我的订单 -->
   <div class="right" style="margin-bottom: 10px;" >
        <div class="button-container">
    <el-button
      v-bind:disabled="dataList.length === 0"
      type="success"
      :icon="Search"
      @click="exportOrder()"
    >
      导出订单
    </el-button>
    <el-button
      v-bind:disabled="dataList.length === 0"
      type="success"
      :icon="Search"
      @click="loadDataList(2)"
    >
      刷新订单列表
    </el-button>
  </div>
       <el-input v-model="query.keyword" style="width: 200px" placeholder="请输入关键字" />
       <el-button  style="margin-left: 10px;" type="primary" :icon="Search" @click="fetchData(1)" >搜索</el-button>
    </div>
  <el-empty v-if="dataList.length === 0" :image-size="100" description="当前您还没有任何订单哦！"></el-empty>
  <el-table v-else :data="dataList" border style="width: 100% " height="700" >
    <el-table-column align="center" label="订单ID" prop="orderId" width="150"/>
    <el-table-column align="center" label="消费对象ID" prop="objectId" width="120"/>
    <el-table-column align="center" label="消费对象名称" prop="objectName" width="120"/>
     <el-table-column align="center" label="订单消费金额" width="120">
      <template #default="scope">
        <span v-if="(scope.row.orderSpend === 0 || scope.row.orderSpend === null) && scope.row.objectStatus === 1">餐厅订单金额，以线下实际消费为准</span>
        <span v-else-if="(scope.row.orderSpend === 0 || scope.row.orderSpend === null) && scope.row.objectStatus === 2">停车订单金额，以实际计算为准</span>
        <span v-else>{{ scope.row.orderSpend }}</span>
      </template>
    </el-table-column>
     <el-table-column align="center" label="消费对象属性" prop="objectStatus" width="120">
        <template #default="scope">
          <span>{{ scope.row.objectStatus === 0 ? '酒店客房订单' :
            scope.row.objectStatus === 1 ?  '酒店食堂订单' : 
            scope.row.objectStatus === 2 ?  '酒店停车订单' :   '酒店商品订单'}}</span>
        </template>
    </el-table-column>
    <el-table-column align="center" label="购买/预定数量" prop="orderAmount" width="120" />
    <el-table-column align="center" label="订单状态" prop="orderStatus" width="120">
        <template #default="scope">
          <span>{{ scope.row.orderStatus === 0 ? '已下单' : scope.row.orderStatus === 1 ? '已支付' :
          scope.row.orderStatus === 2 ? '已取消支付' : '已评价' }}</span>
        </template>
    </el-table-column>
    <el-table-column align="center" label="下单时间" prop="bookTime" width="200" />
    <el-table-column align="center" label="支付/确认时间" prop="purchaseTime" width="200" />
    <el-table-column align="center" label="取消/失效时间" prop="expiredTime" width="200" />
    <el-table-column align="center" label="订单情况" prop="orderInfo" width="200" />
    <el-table-column align="center" fixed="right" label="操作" width="103" >
      <template #default="scope">
        <div style="display: flex; justify-content: space-between;  align-items: center">
            <el-icon class="hover-icon" @click="goToPay(scope.$index, scope.row)" title="去支付/确认"
            ><Pointer /></el-icon>
            <el-icon class="hover-icon" @click="CanclePay(scope.$index, scope.row)" title="取消订单"
            ><CloseBold /></el-icon>
            <el-icon class="hover-icon"
            @click="deleteSingleOrder(scope.$index, scope.row)" title="删除"
            ><Delete /></el-icon>
            <el-icon class="hover-icon" v-if=" scope.row.objectStatus=== 1 || ( scope.row.orderStatus !== 0 && scope.row.orderStatus !== 2)" @click="goToCheckCurrentWaiterAndGoods(scope.$index, scope.row)" title="去评价"
            ><EditPen /></el-icon>
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
<!-- 当前订单评价具体信息 -->
   <el-dialog v-model="turnOnReview" title="评价"  :width="550" :height="300">
      <el-form  :model="collectEditInfo">
        <el-form-item v-if="collectEditInfo.goodsCover !== null" label="商品图片" :label-width="formLabelWidth">
            <img :src="collectEditInfo.goodsCover"  style="width: 40px; height: 40px;" />
        </el-form-item>
          <el-form-item v-if="collectEditInfo.goodsName != null" label="商品名字" :label-width="formLabelWidth" prop="carNumber" >
         <el-input disabled v-model="collectEditInfo.goodsName" autocomplete="off"  />
        </el-form-item>
        <el-form-item v-if="collectEditInfo.goodsPositiveRating != null " label="好评率" :label-width="formLabelWidth"  prop="carName">
         <el-input disabled :value="`${(collectEditInfo.goodsPositiveRating * 100).toFixed(2)}%`" autocomplete="off" />
        </el-form-item>
        <el-form-item v-if="collectEditInfo.waiterCover !== null" label="服务员图片" :label-width="formLabelWidth">
            <img :src="collectEditInfo.waiterCover"  style="width: 40px; height: 40px;" />
        </el-form-item>
        <el-form-item v-if="collectEditInfo.waiterName !== null" label="服务员名字" :label-width="formLabelWidth" prop="carNumber" >
         <el-input disabled v-model="collectEditInfo.waiterName" autocomplete="off"  />
        </el-form-item>
          <el-form-item v-if="collectEditInfo.waiterJobType !== null" label="服务员类型" :label-width="formLabelWidth" prop="carNumber" >
         <el-input disabled v-model="collectEditInfo.waiterJobType" autocomplete="off"  />
        </el-form-item>
        <el-form-item v-if="collectEditInfo.waiterPositiveRating !== null" label="服务员当前好评率" :label-width="formLabelWidth"  prop="carName">
          <el-input disabled :value="`${(collectEditInfo.waiterPositiveRating * 100).toFixed(2)}%`" autocomplete="off" />
        </el-form-item>
      </el-form>
      <template #footer>
      <span class="dialog-footer" >
         <el-button v-if="collectEditInfo.orderStatus === 3" type="danger" disabled>已评论，无法再评</el-button>
        <div v-else>
             <el-button type="success" @click="giveComment(1)">给予好评</el-button>
             <el-button type="warning" @click="giveComment(0)">给予差评</el-button>
        </div>
     
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
import router from '@/router';
import { errorMessages } from 'vue/compiler-sfc';


const total = ref(10)  // 总数据量

const dataList = ref<Order[]>([])  // 原始数据列表

const query = reactive ( {
    userId: localStorage.getItem('userId'),
    currentPage: 1,
    size: 15,
    keyword: ''
  }  
)


// 修改每页显示数据的条数
function handleSizeChange (newSize : number) {
  query.size = newSize;
  loadDataList(1);
}



interface Order {

  orderId: string
  objectId: string
  waiterId: string
  objectName: string
  orderSpend: number
  objectStatus: number
  orderAmount: number
  orderStatus: number
  bookTime: string
  purchaseTime: string
  expiredTime: string
  orderInfo: string


}


// 导出个人订单
async function exportOrder() {
   try {
       const response = await axiosInstance.get(`/api/user/exportOrderList`,{
        params: { userId: query.userId},
        // 设置响应类型为blob，用于处理文件下载
        responseType: 'blob'
     });
    // 判断token是否无效或过期
    if (response.headers['notice'] === 'unable') {
        noticeError("当前token过期或无效,请重新登录");
        // 移除原有的token
        localStorage.removeItem('userToken');
        // 跳转至登录页
        router.push("/");
        return;
    }
      // 创建一个隐藏的可下载链接
  
      const link = document.createElement('a');
      document.body.appendChild(link);
      link.style.display = 'none';
     
      // 创建一个URL表示Blob对象
      const url = window.URL.createObjectURL(response.data);
      link.href = url;
      console.log(response.headers['filename'])
      link.setAttribute('download', '我的订单详情-' + response.headers['filename']); // 设置文件名

      // 触发点击事件
      link.click();

      // 清理
      window.URL.revokeObjectURL(url);
      link.remove();
   }
   catch (error) {
      console.error('导出订单失败:', error);
   }
}

  // 自定义搜索
function fetchData(newPage: number) {
  query.currentPage = newPage;
   // 如果有搜索关键词，则添加到查询参数中
  loadDataList(1);
}

const turnOnReview = ref(false);

// 查看当前服务员
const goToCheckCurrentWaiterAndGoods = async (index: number, row: Order) => {
    await axiosInstance.get(`/api/supervisor/showCurrentWaiterAndGoods?waiterId=${row.waiterId}&objectId=${row.objectId}&objectStatus=${row.objectStatus}`)
    .then(response => {
      collectEditInfo = response.data.data;
    });
    console.log(collectEditInfo);
    collectEditInfo.objectStatus = row.objectStatus;
    collectEditInfo.waiterId = row.waiterId;
    collectEditInfo.objectId = row.objectId;
    collectEditInfo.orderId = row.orderId;
    collectEditInfo.orderStatus = row.orderStatus;
    collectEditInfo.userId = localStorage.getItem('userId');
    turnOnReview.value = true;
}

// 评价当前服务员和商品
const giveComment = (status: string) => {
  // 为1 说明给好评 为0 说明给差评
  if (status) {
   axiosInstance.get(`/api/supervisor/review?waiterId=${collectEditInfo.waiterId}&objectId=${collectEditInfo.objectId}&userId=${collectEditInfo.userId}&objectStatus=${collectEditInfo.objectStatus}&orderId=${collectEditInfo.orderId}&status=1`)
    .then(response => {
      if (response.data.code === 1) {
        notice("评价成功！");
        turnOnReview.value = false;
        loadDataList(0);
        return;
      } else{
        noticeError("评价失败！");
        return;
      }
    });
  }
   else {
      axiosInstance.get(`/api/supervisor/review?waiterId=${collectEditInfo.waiterId}&objectId=${collectEditInfo.objectId}&userId=${collectEditInfo.userId}&objectStatus=${collectEditInfo.objectStatus}&orderId=${collectEditInfo.orderId}&status=0`)
    .then(response => {
      if (response.data.code === 1) {
        notice("评价成功！");
        turnOnReview.value = false;
        return;
      } else{
        noticeError("评价失败！");
        return;
      }
    });
  }
}

// 加载用户数据
const loadDataList = async (flag: number) => {
    try {  
    const response = await axiosInstance.post(`/api/user/queryUserOrderList`, query);
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
    if(total.value === null || total.value === 0) {
      if (flag == 1) {
        noticeError("太难了，换个关键字再看看吧");
        return;
      }
    } else if (flag == 2) {
        notice("手动刷新订单信息成功！");
        return;
    }
  }
    catch (error) {
    // 处理请求错误，例如打印错误信息或者更新UI等
    console.error("Error fetching data:", error);
  }
}
   

// 初始化加载页面
onMounted(() => { loadDataList(0) })

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

let collectEditInfo = reactive ({
    waiterCover: '',
    goodsCover: '',
    goodsName: '',
    objectStatus: null,
    goodsPositiveRating: '',
    waiterName: '',
    orderStatus: null,
    waiterId: '',
    orderId: '',
    userId: '',
    objectId: '',
    waiterPositiveRating: null,
    waiterJobType: '',
    
})

// 支付当前订单
async function goToPay  (index: number, row: Order) {
  const result = await ElMessageBox.confirm('确认支付/确认当前订单吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  });

  if (result) {
  if (row.objectStatus === 1) {
    noticeError('餐厅订单金额，以线下实际消费为准,不支持在线支付!')
    return;
  }
    if (row.orderStatus === 1) {
      noticeError('当前订单已支付，不可再支付!')
      return;
    } else if (row.orderStatus === 2) {
      noticeError('当前订单已取消支付，不可再支付!')
      return;
    }
    if (row.orderSpend === null) {
      row.orderSpend = 0;
    }
    await axiosInstance.get(`/api/user/goToPay?orderId=${row.orderId}&userId=${query.userId}&orderSpend=${row.orderSpend}`)
      .then(response => {
        notice("当前订单支付成功！");
        notice("好心人，给我们的服务员一个评价吧");
        // 在删除后刷新数据
        loadDataList(0);
      })
  }
}

// 取消订单
async function CanclePay  (index: number, row: Order) { 
  if (row.objectStatus === 2) {
    noticeError('当前订单为特殊订单，不允许取消，如有需要请联系服务员!')
    return;
  }
  const result = await ElMessageBox.confirm('确认取消当前订单吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  });

  if (result) {
    if (row.orderStatus === 1) {
      noticeError('当前订单已支付，不可再取消!')
      return;
    } else if (row.orderStatus === 2) {
      noticeError('当前订单已取消支付，不可再取消!')
      return;
    }
    await axiosInstance.get(`/api/user/cancelCurrentOrder?orderId=${row.orderId}&userId=${query.userId}&objectId=${row.objectId}&objectStatus=${row.objectStatus}&orderAmount=${row.orderAmount}`)
      .then(response => {
        if (response.data.code === 1) {
            notice(response.data.data);
        } else {
          noticeError(response.data.msg);
        }
        
        // 在删除后刷新数据
        loadDataList(0);
      })
  }
}
// 删除单一订单记录
async function deleteSingleOrder  (index: number, row: Order) {
  const result = await ElMessageBox.confirm('确认删除当前订单信息吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  });

  if (result) {
    if (row.orderStatus === 0 ) {
      noticeError('当前状态下，不允许删除订单信息!')
      return;
    }
    await axiosInstance.delete(`/api/user/deleteCurrentOrder?orderId=${row.orderId}&userId=${query.userId}`)
      .then(response => {
        notice("删除当前订单信息成功！");
        // 在删除后刷新数据
        loadDataList(0);
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

/* 可以将此样式添加到您的Vue组件的<style>标签中，或者添加到全局样式表中 */
.hover-icon {
  transition: color 0.3s ease; /* 添加颜色变化的过渡效果 */
}

.hover-icon:hover {
  color: rgb(255, 0, 0); /* 鼠标悬停时的颜色 */
}

.right {
  display: flex;
  justify-content: flex-end;
}
.button-container {
  margin-right: 1200px ;
  display: flex;
  gap: 10px; /* 设置按钮之间的间距 */
}
</style>