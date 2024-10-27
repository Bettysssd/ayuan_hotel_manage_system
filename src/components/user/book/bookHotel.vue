<template>
<div v-if="showCard && dataList.length > 0"  class="card-container" >
    <el-card  
    v-for="(room) in dataList"
    :key="room.roomId"
    class="hover-shadow" style="max-height: 240px; max-width: 240px; overflow: hidden; "
    >
        <template #header >
           <div class="card-header-content">
          {{
           room.roomType === 1 ? '单人间' : room.roomType === 3 ? '豪华单人间' :
           room.roomType === 2 ? '经济双人间' : room.roomType === 4 ? '豪华双人间' :
           '家庭间'
          }}
          </div>
        </template>
         <img
            :src="room.roomCover"
            style="width: 100%; height: auto; object-fit: contain;"
            @click="showSingleHotelInfo(room)"
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

  <!-- 客房具体信息 -->
   <el-dialog v-model="dialogVisible" title="客房具体信息"  :width="550" :height="300">
        <el-form ref="ruleFormRef"  >
        <el-form-item label="客房图片" :label-width="formLabelWidth">
            <img :src="roomShowInfo.roomCover"  style="width: 40px; height: 40px;" />
        </el-form-item>
        <el-form-item label="客房介绍" :label-width="formLabelWidth"  >
         <el-input disabled autocomplete="off"  :value="roomShowInfo.roomInfo" type="textarea"/>  
        </el-form-item>
        <el-form-item label="客房类型" :label-width="formLabelWidth" >
          <el-input disabled  autocomplete="off"  :value="showRoomType(roomShowInfo.roomType)" type="textarea" /> 
        </el-form-item>
         <el-form-item label="客房房间号" :label-width="formLabelWidth"  >
         <el-input disabled autocomplete="off" :value="roomShowInfo.roomMark" />
        </el-form-item>
         <el-form-item label="客房定价金额" :label-width="formLabelWidth"   >
         <el-input disabled type="number" autocomplete="off" :value="roomShowInfo.price"/> 
        </el-form-item>
         <el-form-item label="入住人数" :label-width="formLabelWidth"  >
           <el-input disabled  autocomplete="off" :value="showNumberOfOccupants(roomShowInfo.numberOfOccupants)"/> 
        </el-form-item>
        <el-form-item label="当前已登记人数" :label-width="formLabelWidth"  >
           <el-input disabled  autocomplete="off" v-model="roomShowInfo.currentMarkPeople"/> 
        </el-form-item>
         <el-form-item label="好评人数" :label-width="formLabelWidth"  >
           <el-input disabled  autocomplete="off" :value="roomShowInfo.roomTrumpUp"/> 
        </el-form-item>
         <el-form-item label="差评人数" :label-width="formLabelWidth"  >
           <el-input disabled  autocomplete="off" :value="roomShowInfo.roomTrumpDown"/> 
        </el-form-item>
        <el-form-item label="房间好评率" :label-width="formLabelWidth" >
        <el-input disabled  autocomplete="off" :value="`${(roomShowInfo.roomRating * 100).toFixed(2)}%`"/> 
        </el-form-item>
      </el-form>
      <template #footer>
      <span class="dialog-footer">
      <el-button @click="dialogVisible = false">取消</el-button>
      <el-button v-if="roomShowInfo.roomStatus === 1 && roomShowInfo.currentMarkPeople != roomShowInfo.numberOfOccupants && roomShowInfo.roomType != 1  && roomShowInfo.roomType != 3"
      type="success" @click="filledOwnerIdCardCode = true"> 合租 </el-button>
        <el-button v-if="roomShowInfo.roomStatus === 0" type="success" @click="bookHotel( roomShowInfo.roomId, 
        roomShowInfo.currentMarkPeople, roomShowInfo.price, roomShowInfo.roomType)"> 预定 </el-button>
        <el-button disabled v-if="roomShowInfo.roomStatus === 1" type="primary" > 已预定 </el-button>
        <el-button disabled v-if="roomShowInfo.roomStatus === 2" type="danger" > 停用维护中 </el-button>
        <el-button disabled  v-if="roomShowInfo.roomStatus === 3" type="warning" > 已入住 </el-button>
      </span>
      </template>
   </el-dialog>   

<!-- 登记合租人身份id信息 -->
<el-dialog v-model="filledOwnerIdCardCode" title="合租联系人身份ID号"  :width="550" :height="300">
       <el-form ref="ruleFormRef" :rules="rules" :model="roomShowInfo" >
       <el-form-item label="身份证号码" :label-width="formLabelWidth" prop="idCode" >
           <el-input type="text" placeholder="请输入18位身份证号码"  autocomplete="off" v-model="roomShowInfo.idCode"/> 
        </el-form-item>
      </el-form>
      <template #footer>
      <span class="dialog-footer">
         <el-button @click="filledOwnerIdCardCode = false">取消</el-button>
         <el-button  @click="resetForm(ruleFormRef)">重置</el-button>
         <el-button @click="rentalTogeter(roomShowInfo.idCode, roomShowInfo.currentMarkPeople, roomShowInfo.roomId, ruleFormRef)" type="success" > 确定 </el-button>
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
import { fa } from 'element-plus/es/locale';


const ruleFormRef = ref<FormInstance>();  
const dialogVisible = ref(false);
const total = ref(10);  // 总数据量
const dataList = ref<Room[]>([]);
const userId = ref(null);
const showCard = ref(false);
const roomAvatar = ref('');
const filledOwnerIdCardCode = ref(false);

// 重置表单
const resetForm = (formEl: FormInstance | undefined) => {
  if (!formEl) return
  formEl.resetFields()
}


// 业务提示框弹出
const notice = ($content : any) => {
  ElMessage({
          message: $content,
          type: 'success',
        });
}

const rules = reactive<FormRules<Room>>({

  idCode: [
     { required: true, message: '身份证号码不能为空', trigger: 'change' },
     {
      validator: (rule, value, callback) => {
          if (!value) {
            return callback(new Error('身份证号码不能为空'));
          }
          if (!/^[1-9]\d{5}(18|19|20)\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\d{3}[\dXx]$/.test(value)) {
            // 简单的格式校验，包括长度、年份范围、月份、日期
            // 注意：这个正则表达式只做了非常基础的校验，并没有校验校验码
            callback(new Error('身份证号码格式不正确'));
          } else {
            callback();
          }
      }
     }
  ],
 
})

// 酒店合租
const rentalTogeter = async (idCode: string, currentMarkPeople: number, roomId: string,
                 formE1: FormInstance | undefined ) => {
            filledOwnerIdCardCode.value =false;
        if (!formE1) {
                return;
            }
            await formE1.validate((valid, fields) => {
                if (valid) {
                    const result =   ElMessageBox.confirm('确认和此用户合租当前客房吗？', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                        }).then(result => {
                        if (result) {
                            axiosInstance.get(`/api/user/rentalTogether?idCode=${idCode}&userId=${userId.value}&currentMarkPeople=${currentMarkPeople}&roomId=${roomId}`)
                            .then(response => {
                                if (response.data.code == 1) {
                                    notice("合租当前酒店成功！");
                                    // 在删除后刷新数据
                                    dialogVisible.value = false;
                                    loadDataList();
                                } else {
                                    noticeError(response.data.msg);
                                    if (response.data.code === 2) {
                                        noticeError("如需要退订合租，请到我的订单中修改！");
                                    }
                                    
                            }    
                       })
                    } 
                 })   
            }else {
                    noticeError("表单不完整或数据格式不正确。请仔细检查，再进行提交！")
                    return;
                }
        })
  }


// 预定酒店客房
async function bookHotel  (roomId : number, currentMarkPeople: number, price: number, roomType: number) {
    dialogVisible.value = false;
    const result = await ElMessageBox.confirm('确认预定当前客房吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  });

  if (result) {
    await axiosInstance.get(`/api/user/bookHotel?roomId=${roomId}&userId=${userId.value}&currentMarkPeople=${currentMarkPeople}&price=${price}&roomName=${showRoomType(roomType)}`)
      .then(response => {
        if (response.data.code === 1) {
            notice("预定当前酒店成功，请在15分钟内支付！");
            // 在删除后刷新数据
            filledOwnerIdCardCode.value = false;
            loadDataList();
        } else {
            noticeError(response.data.msg);
            noticeError("如需要退订，请到我的订单中修改！");
        }
     
      })
  } else {
    return;
  }
}

// 显示房间类型
const showRoomType = (roomType:number) => {
    switch(roomType) {
        case 1: return '温馨单人间';
        case 2: return '经济双人间';
        case 3: return '豪华单人间';
        case 4: return '豪华双人间';
        case 5: return '家庭间';
    }
}


interface Room {
  roomId: string
  roomCover: string
  roomInfo: string
  roomType: number
  roomStatus: number
  roomMark: string
  idCode: string
  price: number
  roomRating: number
  roomTrumpUp: number
  roomTrumpDown: number
  currentMarkPeople: number
  numberOfOccupants: number
  bookTime: string
  checkInTime: string
  checkOutTime: string
  listOfMembers: string
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

// 显示当前客房可住人数
const showNumberOfOccupants = (numberOfOccupants : number) => {
     switch (numberOfOccupants) {
    case 1: return '单人';
    case 2: return '双人';
    case 3: return '三人';
    case 4: return '四人';
    case 5: return '五人';
  }
}

// 刷新列表数据
const loadDataList = async () => {
    try {  
    const response = await axiosInstance.post(`/api/supervisor/queryRoomList`, query);
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
  const roomShowInfo = reactive<Room> ( {
  roomId: '',
  roomCover: '',
  roomInfo: '',
  roomType: null,
  roomStatus: null,
  roomMark: '',
  price: null,
  idCode: '',
  roomRating: null,
  roomTrumpUp: null,
  roomTrumpDown: null,
  currentMarkPeople: null,
  numberOfOccupants: null,
  bookTime: '',
  checkInTime: '',
  checkOutTime: '',
  listOfMembers: ''
})

function showSingleHotelInfo(room: any) {
  dialogVisible.value = true;
  roomShowInfo.roomId = room.roomId;
  roomShowInfo.idCode = room.idCode;
  roomShowInfo.roomStatus = room.roomStatus;
  roomShowInfo.roomCover = room.roomCover;                   
  roomShowInfo.roomInfo = room.roomInfo;
  roomShowInfo.roomType = room.roomType;
  roomShowInfo.price = room.price;
  roomShowInfo.currentMarkPeople = room.currentMarkPeople;
  roomShowInfo.numberOfOccupants = room.numberOfOccupants;
  roomShowInfo.roomMark = room.roomMark;
  roomShowInfo.roomTrumpUp = room.roomTrumpUp;
  roomShowInfo.roomTrumpDown = room.roomTrumpDown;
  roomShowInfo.roomRating = room.roomRating;
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

.card-container {
  display: grid;
  grid-template-columns: repeat(5, 1fr); /* 创建5列，每列宽度相等 */
  gap: 20px; /* 设置网格项之间的间隙 */
  width: 100%; /* 或者设置为具体宽度，取决于您的布局需求 */
}

.card-header-content { /* 添加样式以使内容居中 */
  display: flex;
  justify-content: center; /* 水平居中 */
  align-items: center; /* 垂直居中 */
  height: 100%; /* 如果需要的话，确保占满整个 header 高度 */
  padding: 0 10px; /* 可选：添加一些内边距 */
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