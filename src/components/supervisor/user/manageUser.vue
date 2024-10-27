<template>
<!-- 用户管理 -->
    <div class="right" style="margin-bottom: 10px;">
       <el-input v-model="query.keyword" style="width: 240px" placeholder="请输入关键字" />
       <el-button  style="margin-left: 10px;" type="primary" :icon="Search" @click="fetchData(1)" >搜索</el-button>
    </div>
  <el-table :data="dataList" border style="width: 100%" height="700" :row-class-name="tableRowRule">
    <el-table-column align="center" fixed label="用户头像" width="100">
    <template #default="scope">
      <div class="image-container" @click="showDialog(scope.row.userCover)">
        <img :src="scope.row.userCover" style="width: 40px; height: 40px;" />
      </div>
    </template>
  </el-table-column>
    <el-table-column align="center" label="用户姓名" prop="userName"  width="100"/>
    <el-table-column align="center" label="qq号码" prop="email"  width="200"/>
    <el-table-column align="center" label="性别" width="70">
    <template #default="scope">
      <span>{{ scope.row.gender === 0 ? '男' : '女' }}</span>
    </template>
   </el-table-column>
    <el-table-column align="center"  label="注册时间" prop="signInTime" />
    <el-table-column align="center" label="最近登录时间" prop="lastLoginTime" />
    <el-table-column align="center" label="身份证号码" prop="idCardCode"  width="200"/>
    <el-table-column align="center" label="手机号码" prop="phoneNumber"  width="150"/>
    <el-table-column align="center" label="花费金额" prop="spendAmount" width="90" />
    <el-table-column align="center" label="微信账号" prop="wechatAccount" width="115"/>
    <el-table-column align="center" label="房间号" prop="roomMark"  width="110"/>
    <el-table-column align="center" label="用户付款状态" width="80">
    <template #default="scope">
      <span>{{ scope.row.payStatus === 0 ? '未付款' : '已付款' }}</span>
    </template>
  </el-table-column>
    <el-table-column align="center" label="用户状态" width="80">
    <template #default="scope">
      <span>{{ scope.row.userStatus === 1 ? '正常' : '停用' }}</span>
    </template>
  </el-table-column>


    <el-table-column  fixed="right" align="center" label="操作" width="130" >
      
      <template #default="scope">
        <div style="display: flex; justify-content: space-between;  align-items: center">
        <el-button color="#626aef" :dark="isDark" plain size="small" @click="handleEdit(scope.$index, scope.row)">
          编辑
        </el-button>
      
        <el-button
          style="margin-left: 10px;"
          size="small"
          color="#008000"
          :dark="isDark" plain
          v-bind:disabled="scope.row.payStatus === 0"
          @click="handleUserStatus(scope.$index, scope.row)"
        >
            <span>{{ scope.row.userStatus === 1 ? '停用' : '启用' }}</span>
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


 <!-- 恢复默认图片 -->
   <el-dialog v-model="dialogUpdateVisible" title="恢复用户默认图片"  :width="300" :height="200">
        <el-form>
        <el-form-item align="center" label="头像" :label-width="formLabelWidth">
        <div class="image-container"> 
             <img :src="userEditInfo.userCover"  style="width: 40px; height: 40px;" />
        </div>
        </el-form-item>
      </el-form>
      <template #footer>
      <span class="dialog-footer">
      <el-button @click="dialogUpdateVisible = false">取消</el-button>
        <el-button type="primary" @click="recoverOrigin"> 恢复默认头像</el-button>
      </span>
      </template>
   </el-dialog>

    <!-- 用户图片预览 -->
   <el-dialog v-model="dialogVisible" width="600px" title="用户图片预览">
    <el-form class="image-container">
        <img :src="previewImage" />
    </el-form>
        <template #footer>
        <span class="dialog-footer">
          <el-button  type="success" @click="dialogVisible = false">关闭</el-button>
        </span>
        </template>
    </el-dialog>
</template>

<script lang="ts" setup>

import { computed, reactive, ref } from 'vue'
import { onMounted } from 'vue'
import axiosInstance from '@/axios/index';
import { ElMessage } from 'element-plus'
import { ElMessageBox } from 'element-plus';
import { ElDialog } from 'element-plus';
import router from '@/router';

let dialogUpdateVisible = ref(false)
const total = ref(100)  // 总数据量 
const dataList = ref<User[]>([])  // 原始数据列表

const dialogVisible = ref(false)
const previewImage = ref('')

const query = reactive ( {
    currentPage: 1,
    size: 15,
    keyword: ''
  }  
)



// 图片预览
function showDialog(imageURL: any) {
  previewImage.value = imageURL
  dialogVisible.value = true
 
}

// 修改每页显示数据的条数
function handleSizeChange (newSize : number) {
  query.size = newSize;
  loadDataList();
}



let currentMaxAmount = 0;
// 判断用户是否停用（黄色）|| 消费金额最大(绿色)
const tableRowRule = ({
  row,
}: {
  row: User
}) => {
  if (row.userStatus === 0) {
    return 'warning-row'
  } else if (row.userStatus === 1 && row.spendAmount === currentMaxAmount && row.spendAmount !== 0) {
    return 'success-row'
  }
}

interface User {
  userId: string
  userCover: string
  userName: string
  email: string
  gender: number
  signInTime: string
  lastLoginTime: string
  idCardCode: string
  phoneNumber: string
  spendAmount: number
  wechatAccount: string
  roomMark: string
  userStatus: number
  payStatus: number
}

// 恢复默认头像
function recoverOrigin () {
    userEditInfo.userCover = "https://hotel-sys-ayuan.oss-cn-beijing.aliyuncs.com/user_default.jpg"
    axiosInstance.get(`/api/supervisor/recoverDefaultImage?imgUrl=${userEditInfo.userCover}&userId=${userEditInfo.userId}`)
    .then(response => {
      dialogUpdateVisible.value = false;
      loadDataList();
    })
}

  // 自定义搜索
function fetchData(newPage: number) {
   query.currentPage = newPage;
   loadDataList()
}

// 加载用户数据
const loadDataList = async () => {
     axiosInstance.post(`/api/supervisor/queryUserList`, query )  
    .then(response => {
      // 判断token是否无效或过期
      console.log(response);
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
      
      if (dataList){
          currentMaxAmount = 0; // 初始加载数据
          for (let i = 0; i < dataList.value.length; i++) {
            if (dataList.value[i].spendAmount > currentMaxAmount) {
              currentMaxAmount = dataList.value[i].spendAmount;
            }
          }
      }
    })
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

// 业务异常弹出框
const noticeError = ($content : any) => {
  ElMessage({
          message: $content,
          type: 'warning',
        });
}
 // 用户修改集合
  const userEditInfo = reactive ( {
  userId: '',  
  userCover: '',
  roomCode: '',
})

// 编辑逻辑处理
const handleEdit = (index: number, row: User) => {
  dialogUpdateVisible.value = true;
  userEditInfo.userCover = row.userCover;
  userEditInfo.userId = row.userId;
}

 // 确认修改用户状态的函数
async function handleUserStatus(index: number, row: any) {
  const result = await ElMessageBox.confirm('确认修改当前用户状态吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  });

  if (result) {
    const userStatusInfo = reactive({
      userId: row.userId,
      userStatus: row.userStatus === 1 ? 0 : 1
    });

    // 修改用户状态
    await axiosInstance.post(`/api/supervisor/changeUserStatus`, userStatusInfo)
      .then(response => {
        notice("修改当前用户状态成功！");
        // 在修改后刷新数据
        loadDataList();
      })
  }
}

</script>


<style>
.image-container img {
  border-radius: 10px;  /* 这会让图片显示为圆角矩形 */
}
.image-container {
  text-align: center;
  max-width: 100%;
  overflow: auto;  
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

.success-row {
  --el-table-tr-bg-color: var(--el-color-success-light-9);
}

.right {
  display: flex;
  justify-content: flex-end;
}


</style>