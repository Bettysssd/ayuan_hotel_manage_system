<template>


  <div>
  <div class="common-layout">
    <el-container>
      <el-aside width="200px">

                  <el-radio-group v-model="isCollapse" style="margin-bottom: 20px">
                  <el-radio-button :value="false">展开</el-radio-button>
                  <el-radio-button :value="true">折叠</el-radio-button>
                  </el-radio-group>
                  <el-menu
                    default-active="1"
                    class="el-menu-vertical-demo"
                    :collapse="isCollapse"
                  >
                    <el-menu-item index="1" @click="goToBookHotel">
                      <el-icon><House /></el-icon>
                      <template #title>客房预定</template>
                    </el-menu-item>
                    <el-menu-item index="2" @click="goToBookGoods">
                      <el-icon><Goods /></el-icon>
                      <template #title>商品预定</template>
                    </el-menu-item>
                        <el-menu-item index="3" @click="goToBookCanteen">
                      <el-icon><Dish /></el-icon>
                      <template #title>餐馆预定</template>
                    </el-menu-item>    
                    <el-menu-item index="4" @click="goToBookParking">
                      <el-icon><LocationInformation /></el-icon>
                      <template #title>车位预定</template>
                    </el-menu-item>
                    <el-menu-item index="5"  @click="goToOrder">
                     <el-icon><ShoppingCartFull /></el-icon>
                      <template #title>我的订单</template>
                    </el-menu-item>
                  </el-menu>

      </el-aside>

      <el-container>

        <el-header>
            <el-page-header :icon="null">
          <template #content>
            <div class="flex items-center">
               <el-tooltip class="item" effect="dark" content="点击设置个人基本信息" placement="top">
              <el-avatar
                :size="32"
                class="mr-3"
                :src="userAvatar"
                @click="showOwnerInfo"/>
               </el-tooltip>
              <span class="text-large font-600 mr-3"> 欢迎您，亲爱的用户： </span>
          
              <el-tag>{{ userName }}</el-tag>
            </div>
          </template>
            <template #extra>
              <div class="flex items-center">
                <el-button type="danger" class="ml-2" @click="logout">用户登出</el-button>
              </div>
            </template>
          </el-page-header>
        </el-header>

        <el-main>
          <router-view/>
        </el-main>


      </el-container>
      
    </el-container>
  </div>
  <div class="starsec"></div>
  <div class="starthird"></div>
  <div class="starfourth"></div>
  <div class="starfifth"></div>
</div>

<!-- 用户信息窗口 -->
 <el-dialog v-model="dialogInfoVisible" title="用户信息设置"  :width="650" :height="430">
         <el-form ref="ruleFormRef" :rules="rules" :model="userEditInfo">
        <div class="avatar-form-container">
        <el-form-item label="您的头像" :label-width="formLabelWidth" class="form-item">
          <el-tooltip class="item" effect="dark" content="点击更换个人头像" placement="top">
              <el-upload
              class="avatar-uploader"
              action="http://localhost:8081/api/user/uploadUserCover"
              :show-file-list="false"
              :before-upload="beforeAvatarUpload"
              :on-success="handleAvatarSuccessUpdate" >
                  <img :src="userEditInfo.userCover"  style="width: 50px; height: 50px;" />
              </el-upload>
          </el-tooltip>
        </el-form-item>
          <el-button type="primary" size="mini" class="upload-button" @click="dialogVisible=true">查看头像</el-button>
        </div>
     <el-form-item align="center" label="您的昵称" prop="userName" >
          <el-input v-model="userEditInfo.userName" autocomplete="off" />
     </el-form-item>
     <el-form-item align="center" label="您的登录邮箱" prop="email"  >
        <el-input type="text" v-model="userEditInfo.email" disabled autocomplete="off" />
     </el-form-item>
     <el-form-item align="center" label="您的性别"  prop="gender" >
         <el-radio-group v-model="userEditInfo.gender">
             <el-radio :value="0" size="large">男</el-radio>
             <el-radio :value="1" size="large">女</el-radio>
         </el-radio-group>
    </el-form-item>
    <el-form-item align="center" label="您的身份证号码"  prop="idCardCode" >
          <el-input type="text" v-model="userEditInfo.idCardCode" autocomplete="off" />
    </el-form-item>
     <el-form-item align="center" label="您的电话号码" prop="phoneNumber"  >
        <el-input type="number" v-model="userEditInfo.phoneNumber" autocomplete="off" />
     </el-form-item>
     <el-form-item align="center" label="您的微信联系方式" prop="wechatAccount"  >
        <el-input v-model="userEditInfo.wechatAccount" autocomplete="off" />
     </el-form-item>
    </el-form>
      <template #footer>
      <span class="dialog-footer">
      <el-button @click="dialogInfoVisible = false">取消</el-button>
        <el-button type="primary" @click="modifyUserInfo(ruleFormRef)">提交</el-button>
      </span>
      </template>
   </el-dialog>

   <!-- 用户头像图片预览 -->
   <el-dialog v-model="dialogVisible" height="450px" width="450px" title="用户头像图片预览">
    <el-form class="image-container" >
        <img :src="userAvatar" />
    </el-form>
        <template #footer>
        <span class="dialog-footer">
          <el-button type="success" @click="dialogVisible = false">关闭</el-button>
        </span>
        </template>
    </el-dialog>
 
</template>

<script lang="ts" setup>
import { ref, onMounted, reactive } from 'vue'
import { ElMessage } from 'element-plus';
import {
  Document,
  Menu as IconMenu,
  Location,
  Setting,
} from '@element-plus/icons-vue'
import { useRouter } from 'vue-router';
import axiosInstance from '@/axios/index';
import type { FormRules } from 'element-plus';
import type { FormInstance } from 'element-plus';
import { ElMessageBox } from 'element-plus';
const ruleFormRef = ref<FormInstance>()


const dialogInfoVisible = ref(false);
const dialogVisible = ref(false)

const rules = reactive<FormRules<User>>({

  userName: [
    { required: true, message: '名字不能为空', trigger: 'blur' },
    { min: 1, max: 10, message: '字符长度应该在1到10之间', trigger: 'change' },
    {
        validator: (rule, value, callback) => {
            if (value && value.includes(' ')) {
                callback(new Error('名字中不能包含空格'));
            } else {
                callback();
            }
        },
        trigger: 'change'
    }
  ],
  gender: [
     { required: true, message: '该字段不允许为空', trigger: 'change' },
  ],
  idCardCode: [
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
  phoneNumber: [
    {  required: true,  message: '手机号码不能为空',  trigger: 'change' },
    { min: 11, max: 11, message: '字符长度应该为11位', trigger: 'change' },
  ],
   wechatAccount: [
    {  required: true,  message: '微信号码不能为空',  trigger: 'change' },
    { min: 6, max: 12, message: '字符长度应该在6到12之间', trigger: 'change' },
    {
        validator: (rule, value, callback) => {
            if (value && value.includes(' ')) {
                callback(new Error('微信号码中不能包含空格'));
            } else {
                callback();
            }
        },
        trigger: 'change'
    }
  ],
})  

// 业务提示框弹出
const notice = ($content : any) => {
  ElMessage({
          message: $content,
          type: 'success',
        });
}

// 业务错误提醒
const noticeError = ($content : any) => {
  ElMessage({
          message: $content,
          type: 'warning',
        });
}

// 图片上传校验
const beforeAvatarUpload = (file : any) => {
 const MAX_SIZE = 0.5 * 1024 * 1024; // 512KB
  const MAX_WIDTH = 400;
  const MAX_HEIGHT = 400; 

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
            noticeError('上传车辆图片尺寸不能超过 400 x 400!');
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

// 修改用户信息
const modifyUserInfo = async ( formEl: FormInstance | undefined) => {
  if (!formEl) {
      return;
  }
 await formEl.validate(async (valid, fields) => {
  if (valid) {
    try {
      const response = await axiosInstance.post(`/api/user/updateUserInfo`, userEditInfo);
      
      if (response.data.msg != null) {
        // 如果响应中有错误信息，调用错误通知
        noticeError(response.data.msg);
        return;
      }

      // 成功的情况
      notice("修改个人信息成功，请刷新浏览器后，继续操作！");
      localStorage.setItem('userName', userEditInfo.userName);
      userName.value = userEditInfo.userName;
      userAvatar.value = userEditInfo.userCover;
      dialogInfoVisible.value = false;

    } catch (error) {
      // 捕获请求中的异常并抛出
      noticeError("当前用户所填信息不唯一，请检查！");
      throw error;  // 自动抛出异常以供上层处理
    }
  } else {
    noticeError("表单不完整或数据格式不正确。请仔细检查，再进行提交！");
  }
});

}

// 完善用户个人资料
async function showOwnerInfo () {
    await axiosInstance.get(`/api/user/getUserInfo?userId=${userId.value}` )
    .then(response => {
       if (response.status === 200) {}
      userEditInfo.userId = response.data.data.userId || ''; 
      userEditInfo.userCover = response.data.data.userCover || '';
      userEditInfo.userName = response.data.data.userName || '';
      userEditInfo.email = response.data.data.email || '';
      userEditInfo.gender = response.data.data.gender || 0; 
      userEditInfo.idCardCode = response.data.data.idCardCode || '';
      userEditInfo.phoneNumber = response.data.data.phoneNumber || '';
      userEditInfo.wechatAccount = response.data.data.wechatAccount || '';
    })
    dialogInfoVisible.value = true;
};
// 图片上传成功后，回调函数获取上传文件的文件名
function handleAvatarSuccessUpdate(response : any, file : any ) {
    userEditInfo.userCover = "https://hotel-sys-ayuan.oss-cn-beijing.aliyuncs.com/" + file.name;
    notice("图片成功上传, 但要完整提交后才能更新图片！")  
}



// 用户名称
const userName = ref('');
const userAvatar = ref('');
const userId = ref('');
interface User {
  userId: string
  userCover: string
  userName: string
  email: string
  gender: number
  idCardCode: string
  phoneNumber: string
  wechatAccount: string
}

 // 用户信息数据存储集合
  const userEditInfo = reactive ( {
  userId: '',
  userCover: '',
  userName: '',
  email: '',
  gender: 0,
  idCardCode: '',
  phoneNumber: '',
  wechatAccount: ''
})


// 获取当前用户的头像
async function onLoadUserAvatar () {
    userId.value = localStorage.getItem("userId");
    if (userId) {
         await axiosInstance.get(`/api/user/getUserAvatar?userId=${userId.value}`)
            .then(response => {
                  userAvatar.value = response.data.data;
            })
    }
}
// 页面加载时，自动绑定用户的姓名
onMounted(() => {
   onLoadUserAvatar();
   goToBookHotel();
   userName.value = localStorage.getItem('userName');

});

// 登出
const logout = () => {
    axiosInstance.get('/api/user/logout')
    .then(response => {
         // 登出成功，更新登录状态
          localStorage.removeItem('userToken');
           ElMessage({
          message: "登出成功，期待下次与你相见",
          type: 'success',
        });
        // 回到首页
        router.push("/")
      })
}

const router = useRouter();

// 酒店客房预定
const goToBookHotel = () =>  {
    router.push('/user/bookHotel');
}
//酒店货物预定
const goToBookGoods = () => {
    router.push('/user/bookGoods')
}

// 用户订单查看
const goToOrder = () => {
    router.push('/user/order')
}

// 预定餐馆
const goToBookCanteen = () => {
   router.push('/user/bookCanteen')
}

// 预定停车位
const goToBookParking = () => {
   router.push('/user/bookParking')
}

const isCollapse = ref(true)


</script>

<style>
.avatar-form-container {
  display: flex;
  align-items: center; /* 垂直居中 */
  /* 根据需要调整以下值以确保按钮与表单项对齐 */
  margin-top: -10px; /* 可能需要调整以抵消 el-form-item 的内部边距 */
}

.form-item {
  /* 如果需要，可以在这里为 el-form-item 添加额外的样式 */
  margin-right: 50px; /* 在表单项和按钮之间添加空间 */
}

.el-menu-vertical-demo:not(.el-menu--collapse) {
  width: 200px;
  min-height: 400px;
}
body
{
      background-color: hwb(0 100% 0%);
}

.top-header:before {
    background-image: url(https://1.bp.blogspot.com/-gxsOcYWghHA/Xp_izTh4sFI/AAAAAAAAU8s/y637Fwg99qAuzW9na_NT_uApny8Vce95gCEwYBhgL/s1600/header-footer-gradient-bg.png);
}
.top-header:before {
    content: '';
    display: block;
    width: 100%;
    height: 4px;
    background-repeat: repeat-x;
    background-size: contain;
    position: absolute;
    top: 0;
    left: 0;
  opacity:0.5;
}

#mainCoantiner
{
    background-color: #181828;
  height:100%;
  width:100%;
  padding-bottom: 50px;
}

.mystyleSec
{
  background-image: url("https://www.toptal.com/designers/subtlepatterns/uploads/dark-honeycomb.png");
}

.mystylethird
{
background-image: url(https://www.toptal.com/designers/subtlepatterns/uploads/random_grey_variations.png);
}
/*************header*******/

.main-header {
    top: 0;
    left: 0;
    z-index: 20;
    -webkit-transform: translate3d(0,0,0);
    height: 70px;
    width: 100%;
    bottom: inherit;
    text-align: center;
    background: rgba(28, 31, 47, 0.16);
    overflow: hidden;
    border: 1px solid #2e344d;
    -webkit-transition: all 0.3s ease-in-out 0s;
    -moz-transition: all 0.3s ease-in-out 0s;
    -ms-transition: all 0.3s ease-in-out 0s;
    -o-transition: all 0.3s ease-in-out 0s;
    transition: all 0.3s ease-in-out 0s;
    box-shadow: 10px 10px 36px rgb(0,0,0,0.5), -13px -13px 23px rgba(255,255,255, 0.03);
    border-width: 1px 0px 0 1px;
}

.folio-btn {
    position: absolute;
    bottom: 0;
    background: rgba(255, 255, 255, 0.03);
    border-top: 1px solid rgba(255,255,255,0.1);
    right: 0;
    height: 70px;
    width: 70px;
}

.folio-btn-item {
position: absolute;
    width: 40px;
    height: 40px;
    left: 15px;
    top: 15px;
}

.folio-btn-dot {
    float: left;
    width: 33.3%;
    height: 33.3%;
    position: relative;
}

.folio-btn-dot:before {
    content: '';
    position: absolute;
    left: 50%;
    top: 50%;
    width: 4px;
    height: 4px;
    margin: -2px 0 0 -2px;
    border-radius: 50%;
    transition: all 300ms linear;
    transform: scale(1.0);
}

.folio-btn-dot:before
{
  background: #00BCD4;
}

.folio-btn:hover .folio-btn-dot:first-child:before, .folio-btn:hover .folio-btn-dot:nth-child(3):before, .folio-btn:hover .folio-btn-dot:nth-child(4):before, .folio-btn:hover .folio-btn-dot:nth-child(8):before {
    transform: scale(1.8);
    opacity: 0.5;
}


/***social***/
.header-social {
position: absolute;
    z-index: 20;
    width: auto;
    bottom: 17px;
    right: 90px;
    padding: 0;
    min-width: 140px;
    box-shadow: 3px 9px 16px rgb(0,0,0,0.4), -3px -3px 10px rgba(255,255,255, 0.06), inset 14px 14px 26px rgb(0,0,0,0.3), inset -3px -3px 15px rgba(255,255,255, 0.05);
    border-width: 1px 0px 0px 1px;
    border-style: solid;
    border-color: #2e344d;
}

.header-social:before
{
  background: #00BCD4;
}

.header-social:before {
    content: '';
    position: absolute;
    left: 50%;
    top: -3px;
    width: 36px;
    margin-left: -18px;
    height: 6px;
}

.header-social li {
    display: block;
    float: left;
    margin-top: 0;
}

.header-social li a {
    width: 36px;
    height: 36px;
    line-height: 36px;
    display: inline-block;
    font-size: 12px;
    border-style: solid;
    color: rgba(255, 255, 255, 0.41);
    border: 1px solid rgba(255, 255, 255, 0.08);
    transition: all 300ms linear;
}

.header-social li a:hover
{
  color:  #00BCD4;
  cursor:pointer;
}

ul, li
{
      border: none;
    outline: 0;
    font-weight: inherit;
    font-style: inherit;
    font-size: 100%;
    font-family: inherit;
    vertical-align: baseline;
    text-decoration: none;
    margin: 0;
    padding: 0;
}

ol, ul {
    list-style: none;
}

.main-header:before {
    content: '';
    position: absolute;
    left: 10px;
    width: 60px;
    height: 1px;
    bottom: 120px;
    background: rgba(255,255,255,0.1);
}

/***end*****/

.card{    border: none;
    border-radius: 30px;
background-color: #14edaa;}

.wow-bg
{
      background-color: #141421;
    border: 1px solid #2e2e4c;
    box-shadow: 3px 9px 16px rgb(0,0,0,0.4), -3px -3px 10px rgba(255,255,255, 0.06), inset 14px 14px 26px rgb(0,0,0,0.3), inset -3px -3px 15px rgba(255,255,255, 0.05);
  border-radius: 10px;
  margin-top: 4px;
}



.starsec{
  content: " ";
    position: absolute;
    width: 3px;
    height: 3px;
    background: transparent;
   box-shadow: 571px 173px #00BCD4, 1732px 143px #00BCD4, 1745px 454px #FF5722, 234px 784px #00BCD4, 1793px 1123px #FF9800, 1076px 504px #03A9F4, 633px 601px #FF5722, 350px 630px #FFEB3B, 1164px 782px #00BCD4, 76px 690px #3F51B5, 1825px 701px #CDDC39, 1646px 578px #FFEB3B, 544px 293px #2196F3, 445px 1061px #673AB7, 928px 47px #00BCD4, 168px 1410px #8BC34A, 777px 782px #9C27B0, 1235px 1941px #9C27B0, 104px 1690px #8BC34A, 1167px 1338px #E91E63, 345px 1652px #009688, 1682px 1196px #F44336, 1995px 494px #8BC34A, 428px 798px #FF5722, 340px 1623px #F44336, 605px 349px #9C27B0, 1339px 1344px #673AB7, 1102px 1745px #3F51B5, 1592px 1676px #2196F3, 419px 1024px #FF9800, 630px 1033px #4CAF50, 1995px 1644px #00BCD4, 1092px 712px #9C27B0, 1355px 606px #F44336, 622px 1881px #CDDC39, 1481px 621px #9E9E9E, 19px 1348px #8BC34A, 864px 1780px #E91E63, 442px 1136px #2196F3, 67px 712px #FF5722, 89px 1406px #F44336, 275px 321px #009688, 592px 630px #E91E63, 1012px 1690px #9C27B0, 1749px 23px #673AB7, 94px 1542px #FFEB3B, 1201px 1657px #3F51B5, 1505px 692px #2196F3, 1799px 601px #03A9F4, 656px 811px #00BCD4, 701px 597px #00BCD4, 1202px 46px #FF5722, 890px 569px #FF5722, 1613px 813px #2196F3, 223px 252px #FF9800, 983px 1093px #F44336, 726px 1029px #FFC107, 1764px 778px #CDDC39, 622px 1643px #F44336, 174px 1559px #673AB7, 212px 517px #00BCD4, 340px 505px #FFF, 1700px 39px #FFF, 1768px 516px #F44336, 849px 391px #FF9800, 228px 1824px #FFF, 1119px 1680px #FFC107, 812px 1480px #3F51B5, 1438px 1585px #CDDC39, 137px 1397px #FFF, 1080px 456px #673AB7, 1208px 1437px #03A9F4, 857px 281px #F44336, 1254px 1306px #CDDC39, 987px 990px #4CAF50, 1655px 911px #00BCD4, 1102px 1216px #FF5722, 1807px 1044px #FFF, 660px 435px #03A9F4, 299px 678px #4CAF50, 1193px 115px #FF9800, 918px 290px #CDDC39, 1447px 1422px #FFEB3B, 91px 1273px #9C27B0, 108px 223px #FFEB3B, 146px 754px #00BCD4, 461px 1446px #FF5722, 1004px 391px #673AB7, 1529px 516px #F44336, 1206px 845px #CDDC39, 347px 583px #009688, 1102px 1332px #F44336, 709px 1756px #00BCD4, 1972px 248px #FFF, 1669px 1344px #FF5722, 1132px 406px #F44336, 320px 1076px #CDDC39, 126px 943px #FFEB3B, 263px 604px #FF5722, 1546px 692px #F44336;
  animation: animStar 150s linear infinite;
}

.starthird
{
  content: " ";
    position: absolute;
    width: 3px;
    height: 3px;
    background: transparent;
   box-shadow: 571px 173px #00BCD4, 1732px 143px #00BCD4, 1745px 454px #FF5722, 234px 784px #00BCD4, 1793px 1123px #FF9800, 1076px 504px #03A9F4, 633px 601px #FF5722, 350px 630px #FFEB3B, 1164px 782px #00BCD4, 76px 690px #3F51B5, 1825px 701px #CDDC39, 1646px 578px #FFEB3B, 544px 293px #2196F3, 445px 1061px #673AB7, 928px 47px #00BCD4, 168px 1410px #8BC34A, 777px 782px #9C27B0, 1235px 1941px #9C27B0, 104px 1690px #8BC34A, 1167px 1338px #E91E63, 345px 1652px #009688, 1682px 1196px #F44336, 1995px 494px #8BC34A, 428px 798px #FF5722, 340px 1623px #F44336, 605px 349px #9C27B0, 1339px 1344px #673AB7, 1102px 1745px #3F51B5, 1592px 1676px #2196F3, 419px 1024px #FF9800, 630px 1033px #4CAF50, 1995px 1644px #00BCD4, 1092px 712px #9C27B0, 1355px 606px #F44336, 622px 1881px #CDDC39, 1481px 621px #9E9E9E, 19px 1348px #8BC34A, 864px 1780px #E91E63, 442px 1136px #2196F3, 67px 712px #FF5722, 89px 1406px #F44336, 275px 321px #009688, 592px 630px #E91E63, 1012px 1690px #9C27B0, 1749px 23px #673AB7, 94px 1542px #FFEB3B, 1201px 1657px #3F51B5, 1505px 692px #2196F3, 1799px 601px #03A9F4, 656px 811px #00BCD4, 701px 597px #00BCD4, 1202px 46px #FF5722, 890px 569px #FF5722, 1613px 813px #2196F3, 223px 252px #FF9800, 983px 1093px #F44336, 726px 1029px #FFC107, 1764px 778px #CDDC39, 622px 1643px #F44336, 174px 1559px #673AB7, 212px 517px #00BCD4, 340px 505px #FFF, 1700px 39px #FFF, 1768px 516px #F44336, 849px 391px #FF9800, 228px 1824px #FFF, 1119px 1680px #FFC107, 812px 1480px #3F51B5, 1438px 1585px #CDDC39, 137px 1397px #FFF, 1080px 456px #673AB7, 1208px 1437px #03A9F4, 857px 281px #F44336, 1254px 1306px #CDDC39, 987px 990px #4CAF50, 1655px 911px #00BCD4, 1102px 1216px #FF5722, 1807px 1044px #FFF, 660px 435px #03A9F4, 299px 678px #4CAF50, 1193px 115px #FF9800, 918px 290px #CDDC39, 1447px 1422px #FFEB3B, 91px 1273px #9C27B0, 108px 223px #FFEB3B, 146px 754px #00BCD4, 461px 1446px #FF5722, 1004px 391px #673AB7, 1529px 516px #F44336, 1206px 845px #CDDC39, 347px 583px #009688, 1102px 1332px #F44336, 709px 1756px #00BCD4, 1972px 248px #FFF, 1669px 1344px #FF5722, 1132px 406px #F44336, 320px 1076px #CDDC39, 126px 943px #FFEB3B, 263px 604px #FF5722, 1546px 692px #F44336;
  animation: animStar 10s linear infinite;
}

.starfourth
{
  content: " ";
    position: absolute;
    width: 2px;
    height: 2px;
    background: transparent;
   box-shadow: 571px 173px #00BCD4, 1732px 143px #00BCD4, 1745px 454px #FF5722, 234px 784px #00BCD4, 1793px 1123px #FF9800, 1076px 504px #03A9F4, 633px 601px #FF5722, 350px 630px #FFEB3B, 1164px 782px #00BCD4, 76px 690px #3F51B5, 1825px 701px #CDDC39, 1646px 578px #FFEB3B, 544px 293px #2196F3, 445px 1061px #673AB7, 928px 47px #00BCD4, 168px 1410px #8BC34A, 777px 782px #9C27B0, 1235px 1941px #9C27B0, 104px 1690px #8BC34A, 1167px 1338px #E91E63, 345px 1652px #009688, 1682px 1196px #F44336, 1995px 494px #8BC34A, 428px 798px #FF5722, 340px 1623px #F44336, 605px 349px #9C27B0, 1339px 1344px #673AB7, 1102px 1745px #3F51B5, 1592px 1676px #2196F3, 419px 1024px #FF9800, 630px 1033px #4CAF50, 1995px 1644px #00BCD4, 1092px 712px #9C27B0, 1355px 606px #F44336, 622px 1881px #CDDC39, 1481px 621px #9E9E9E, 19px 1348px #8BC34A, 864px 1780px #E91E63, 442px 1136px #2196F3, 67px 712px #FF5722, 89px 1406px #F44336, 275px 321px #009688, 592px 630px #E91E63, 1012px 1690px #9C27B0, 1749px 23px #673AB7, 94px 1542px #FFEB3B, 1201px 1657px #3F51B5, 1505px 692px #2196F3, 1799px 601px #03A9F4, 656px 811px #00BCD4, 701px 597px #00BCD4, 1202px 46px #FF5722, 890px 569px #FF5722, 1613px 813px #2196F3, 223px 252px #FF9800, 983px 1093px #F44336, 726px 1029px #FFC107, 1764px 778px #CDDC39, 622px 1643px #F44336, 174px 1559px #673AB7, 212px 517px #00BCD4, 340px 505px #FFF, 1700px 39px #FFF, 1768px 516px #F44336, 849px 391px #FF9800, 228px 1824px #FFF, 1119px 1680px #FFC107, 812px 1480px #3F51B5, 1438px 1585px #CDDC39, 137px 1397px #FFF, 1080px 456px #673AB7, 1208px 1437px #03A9F4, 857px 281px #F44336, 1254px 1306px #CDDC39, 987px 990px #4CAF50, 1655px 911px #00BCD4, 1102px 1216px #FF5722, 1807px 1044px #FFF, 660px 435px #03A9F4, 299px 678px #4CAF50, 1193px 115px #FF9800, 918px 290px #CDDC39, 1447px 1422px #FFEB3B, 91px 1273px #9C27B0, 108px 223px #FFEB3B, 146px 754px #00BCD4, 461px 1446px #FF5722, 1004px 391px #673AB7, 1529px 516px #F44336, 1206px 845px #CDDC39, 347px 583px #009688, 1102px 1332px #F44336, 709px 1756px #00BCD4, 1972px 248px #FFF, 1669px 1344px #FF5722, 1132px 406px #F44336, 320px 1076px #CDDC39, 126px 943px #FFEB3B, 263px 604px #FF5722, 1546px 692px #F44336;
  animation: animStar 50s linear infinite;
}

.starfifth
{
  content: " ";
    position: absolute;
    width: 1px;
    height: 1px;
    background: transparent;
   box-shadow: 571px 173px #00BCD4, 1732px 143px #00BCD4, 1745px 454px #FF5722, 234px 784px #00BCD4, 1793px 1123px #FF9800, 1076px 504px #03A9F4, 633px 601px #FF5722, 350px 630px #FFEB3B, 1164px 782px #00BCD4, 76px 690px #3F51B5, 1825px 701px #CDDC39, 1646px 578px #FFEB3B, 544px 293px #2196F3, 445px 1061px #673AB7, 928px 47px #00BCD4, 168px 1410px #8BC34A, 777px 782px #9C27B0, 1235px 1941px #9C27B0, 104px 1690px #8BC34A, 1167px 1338px #E91E63, 345px 1652px #009688, 1682px 1196px #F44336, 1995px 494px #8BC34A, 428px 798px #FF5722, 340px 1623px #F44336, 605px 349px #9C27B0, 1339px 1344px #673AB7, 1102px 1745px #3F51B5, 1592px 1676px #2196F3, 419px 1024px #FF9800, 630px 1033px #4CAF50, 1995px 1644px #00BCD4, 1092px 712px #9C27B0, 1355px 606px #F44336, 622px 1881px #CDDC39, 1481px 621px #9E9E9E, 19px 1348px #8BC34A, 864px 1780px #E91E63, 442px 1136px #2196F3, 67px 712px #FF5722, 89px 1406px #F44336, 275px 321px #009688, 592px 630px #E91E63, 1012px 1690px #9C27B0, 1749px 23px #673AB7, 94px 1542px #FFEB3B, 1201px 1657px #3F51B5, 1505px 692px #2196F3, 1799px 601px #03A9F4, 656px 811px #00BCD4, 701px 597px #00BCD4, 1202px 46px #FF5722, 890px 569px #FF5722, 1613px 813px #2196F3, 223px 252px #FF9800, 983px 1093px #F44336, 726px 1029px #FFC107, 1764px 778px #CDDC39, 622px 1643px #F44336, 174px 1559px #673AB7, 212px 517px #00BCD4, 340px 505px #FFF, 1700px 39px #FFF, 1768px 516px #F44336, 849px 391px #FF9800, 228px 1824px #FFF, 1119px 1680px #FFC107, 812px 1480px #3F51B5, 1438px 1585px #CDDC39, 137px 1397px #FFF, 1080px 456px #673AB7, 1208px 1437px #03A9F4, 857px 281px #F44336, 1254px 1306px #CDDC39, 987px 990px #4CAF50, 1655px 911px #00BCD4, 1102px 1216px #FF5722, 1807px 1044px #FFF, 660px 435px #03A9F4, 299px 678px #4CAF50, 1193px 115px #FF9800, 918px 290px #CDDC39, 1447px 1422px #FFEB3B, 91px 1273px #9C27B0, 108px 223px #FFEB3B, 146px 754px #00BCD4, 461px 1446px #FF5722, 1004px 391px #673AB7, 1529px 516px #F44336, 1206px 845px #CDDC39, 347px 583px #009688, 1102px 1332px #F44336, 709px 1756px #00BCD4, 1972px 248px #FFF, 1669px 1344px #FF5722, 1132px 406px #F44336, 320px 1076px #CDDC39, 126px 943px #FFEB3B, 263px 604px #FF5722, 1546px 692px #F44336;
  animation: animStar 80s linear infinite;
}

@keyframes animStar
{
  0% {
    transform: translateY(0px);
}
  100% {
    transform: translateY(-2000px);
}
}



.logn-btn
{
     background: #1c1f2f;
    border-radius: 30px;
    overflow: hidden;
    border: 1px solid #2e344d;
    -webkit-transition: all 0.3s ease-in-out 0s;
    -moz-transition: all 0.3s ease-in-out 0s;
    -ms-transition: all 0.3s ease-in-out 0s;
    -o-transition: all 0.3s ease-in-out 0s;
    transition: all 0.3s ease-in-out 0s;
    /* box-shadow: 10px 10px 36px rgb(0,0,0,0.5), -13px -13px 23px rgba(255,255,255, 0.03), inset 14px 14px 70px rgb(0,0,0,0.2), inset -15px -15px 30px rgba(255,255,255, 0.04); */
    box-shadow: 0px 2px 26px rgb(0,0,0,0.5), 0px 7px 13px rgba(255,255,255, 0.03);
    margin-top: 30px;
}

.logn-btn:hover
{
  background-color: #d8d8d8;
    border-radius: 50px;
    min-width: 140px;
    /* box-shadow: 10px 10px 36px rgb(0,0,0,0.5), -13px -13px 23px rgba(255,255,255, 0.03), inset 14px 14px 70px rgb(0,0,0,0.2), inset -15px -15px 30px rgba(255,255,255, 0.04); */
    box-shadow: 3px 9px 16px rgb(0,0,0,0.4), -3px -3px 10px rgba(255,255,255, 0.06), inset 14px 14px 26px rgb(0,0,0,0.3), inset -3px -3px 15px rgba(255,255,255, 0.05);
    border-width: 1px 0px 0px 1px;
    border-style: solid;
    border-color: #2e344d;
}

.textbox-dg
{
  background: rgba(28, 31, 47, 0.16);
    border-radius: 4px;
    overflow: hidden;
    border: 1px solid #2e344d;
    -webkit-transition: all 0.3s ease-in-out 0s;
    -moz-transition: all 0.3s ease-in-out 0s;
    -ms-transition: all 0.3s ease-in-out 0s;
    -o-transition: all 0.3s ease-in-out 0s;
    transition: all 0.3s ease-in-out 0s;
    /* box-shadow: 10px 10px 36px rgb(0,0,0,0.5), -13px -13px 23px rgba(255,255,255, 0.03), inset 14px 14px 70px rgb(0,0,0,0.2), inset -15px -15px 30px rgba(255,255,255, 0.04); */
    box-shadow: 10px 10px 36px rgb(0,0,0,0.5), -13px -13px 23px rgba(255,255,255, 0.03);
    border-width: 1px 0px 0 1px;
      margin-top: 15px;
}



.form-control:focus
{
  border: 1px solid #344d2e;
  color: #495057;
    outline: 0;
    background: rgb(17, 20, 31);
    border-radius: 4px;
  transition: all 0.3s ease-in-out 0s;
    /* box-shadow: 10px 10px 36px rgb(0,0,0,0.5), -13px -13px 23px rgba(255,255,255, 0.03), inset 14px 14px 70px rgb(0,0,0,0.2), inset -15px -15px 30px rgba(255,255,255, 0.04); */
    box-shadow: 10px 10px 36px rgb(0,0,0,0.5), -13px -13px 23px rgba(255,255,255, 0.03);
}



.btn-link {
    color: #344d2e;
}


.btn-link:hover {
    color: #2b7a19;
    text-decoration: underline;
}

.btn-primary:not(:disabled):not(.disabled).active, .btn-primary:not(:disabled):not(.disabled):active, .show>.btn-primary.dropdown-toggle {
    color: #807f7f;
    background-color: transparent;
    border-color: #2b7a19;
}


.btn-primary.focus, .btn-primary:focus {
    color: #fff;
    background-color: transparent;
    border-color: transparent;
    box-shadow: 0 0 0 0.2rem rgba(0, 255, 110, 0.25);
}

.mt-6, .my-6 {
    margin-top: 2rem !important;
}


.socila-btn
{
  height: 40px;
    border-radius: 10%;
    width: 40px;
    box-shadow: 3px 9px 16px rgb(0,0,0,0.4), -3px -3px 10px rgba(255,255,255, 0.06), inset 14px 14px 26px rgb(0,0,0,0.3), inset -3px -3px 15px rgba(255,255,255, 0.05);
    border-width: 1px 0px 0px 1px;
    border-style: solid;
    border-color: rgba(255,255,255, 0.2);
    margin-right: 10px;
   
}

.fb-color
{
  color:#3b5998 ;
}

.incolor
{
  color:#007bff;
}

.tweetcolor
{
  color:#41a4f7;
}
.driblecolor
{
  color:#e83e8c;
}

.colorboard
{
  color: #00ffaaed;
}


</style>


