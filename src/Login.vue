<template>  
  <section>
  
  <div class="box">
    
    <div class="square" style="--i:0;"></div>
    <div class="square" style="--i:1;"></div>
    <div class="square" style="--i:2;"></div>
    <div class="square" style="--i:3;"></div>
    <div class="square" style="--i:4;"></div>
    <div class="square" style="--i:5;"></div>
    
   <div class="container"> 


    <!-- 管理员&用户登录 -->
    <div class="form" v-if="opType == 1"> 
      <div v-if="!loading"> 
        <h2 style="text-align: center;">悦居酒店管理系统</h2>
        <form @submit.prevent> 
          <div class="inputBx">
            <input id="account" type="text" @blur="validateEmail" v-model="userFormData.email" required="required">
            <span>账号：</span>
            <i class="fas fa-user-circle"></i>
          </div>
          <div class="inputBx password">
            <input id="password" type="password" name="password" v-model="userFormData.password" required="required">
            <span>密码：</span>
            <a href="#" class="password-control" @click="show_hide_password($event);"></a>
            <i class="fas fa-key"></i>
          </div>
          <div class="inputBx">
            <input type="submit" value="用户登录"  @click="login(1)"  style="margin-right: 60px;">
            <input type="submit" value="管理员登录" @click="login(0)"> 
          </div>
        </form>
          <div class="inline-links">
              <p> <a href="javascript:void(0)" @click="showPanel(3)">忘记密码?</a> </p>
              <p> <a href="javascript:void(0)" @click="showPanel(2)">还没有账号?</a> </p>
        </div>
      </div>
      <h3 style="text-align: center; color: #FFFFFF; font: 25px" v-else> 程序正在加載, 请稍等······ </h3>
     
    </div>


    <!-- 用户注册 -->
    <div class="form" v-if="opType == 2"> 
      <div v-if="!loading"> 
          <h2 style="text-align: center;" >您好，请注册！</h2>
      <form  @submit.prevent="signIn">
        <div class="inputBx">
          <input type="text" v-model="userFormData.email" @blur="validateEmail" required="required">
          <span>请输入邮箱号码：</span>
          <i class="fas fa-user-circle"></i>
        </div>
        <div class="inputBx password">
          <input id="password" type="password" v-model="userFormData.password" name="password" required="required">
          <span>请输入密码：</span>
          <a href="#" class="password-control" @click="show_hide_password($event);"></a>
           <i class="fas fa-key"></i>
        </div>
        <div class="inputBx">
          <input type="submit" style="margin-left: 74px;" value="注册">
        </div>
      </form>
      <p> <a href="javascript:void(0)" @click="showPanel(1)">已有账号，登录?</a> </p>
      </div>
        <h3 style="text-align: center; color: #FFFFFF; font: 25px" v-else> 程序正在加載, 请稍等······ </h3>
     
    </div>


       <!-- 重置密码中间代理 -->
    <div class="form" v-if="opType == 3"> 
      <div v-if="!loading"> 
          <h2 style="text-align: center;" >请验证是你本人操作！</h2>
        <form  @submit.prevent="getAndVerify">
          <div class="inputBx">
            <input type="text" id="identify" v-model="userFormData.email" @blur="validateEmail" required="required">
            <span>请输入邮箱号码：</span>
            <i class="fas fa-user-circle"></i>
          </div>
          <div class="inputBx">
            <input id="verifyCode" type="text" name="verifyCode" v-model="userFormData.verifyCode" required="required">
            <span>请输入验证码：</span>
          </div>
          <div class="inputBx">
            <input type="submit" style="margin-left: 74px;" value="验证"  >
          </div>
          <p> 
            <span v-if="isSending">验证码已发送，{{ secondsLeft }}秒后可重新发送</span>
            <a href="javascript:void(0)" v-else @click="getVerifyCode()">获取验证码?</a>
          </p>
          <p>
            <a href="javascript:void(0)"  @click="showPanel(1)">返回登录?</a>
          </p>
        </form>
      </div>
       <h3 style="text-align: center; color: #FFFFFF; font: 25px" v-else> 程序正在加載, 请稍等······ </h3>
    </div>



    <!-- 重置密码 -->
    <div class="form" v-if="opType == 0"> 
    <div v-if="!loading">
        <h2 style="text-align: center;" >您好，请重置密码！</h2>
      <form  @submit.prevent="resetPwd">
        <div class="inputBx">
          <input type="text" id="email" v-model="userFormData.email" @blur="validateEmail" required="required">
          <span>请输入邮箱号码：: </span>
          <i class="fas fa-user-circle"></i>
        </div>
        <div class="inputBx password">
          <input id="password" type="password" name="password" v-model="userFormData.password" required="required">
          <span>请输入新密码：</span>
           <a href="#" class="password-control" @click="show_hide_password($event);"></a>
           <i class="fas fa-key"></i>
        </div>
        <div class="inputBx">
          <input type="submit" style="margin-left: 74px;" value="重置密码"  >
        </div>
      </form>
      <p> <a href="javascript:void(0)" @click="showPanel(1)">返回登录?</a> </p>
    </div>
    <h3 style="text-align: center; color: #FFFFFF; font: 25px" v-else> 程序正在加載, 请稍等······ </h3>
    </div>
  </div>

</div>
   
</section>
</template>  
  
<script setup>  


import { reactive, ref, onUnmounted, onMounted } from "vue";
import axios from 'axios'
import { useRouter } from "vue-router";
import { ElMessage } from 'element-plus';



const emailError = ref(null)
const isSending = ref(false);
const secondsLeft = ref(0);
let intervalId = null; // 用于存储setInterval的ID
const loading = ref(false);
const router = useRouter();
const opType = ref(1);
const formDataRef = ref();
const status = ref(null)
const userFormData = reactive({
  email: '',
  password: '',
  verifyCode: ''
})
const supervisorFormData = reactive({
  account: '',
  password: ''
})


// 组件卸载时清除倒计时
const cleanup = () => {
    if (intervalId) clearInterval(intervalId);
 }

onUnmounted(cleanup);


// 校验邮箱账号格式
function validateEmail() {
   const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/; // 简单的邮箱格式校验正则表达式
      if (!emailPattern.test(userFormData.email)) {
        status.value = false;
      } else {
        status.value = true;
      }
}
// 业务提示框弹出
const notice = ($content) => {
  ElMessage({
          message: $content,
          type: 'success',
        });
}

// 错误提示框
const errorNotice = ($content) => {
  ElMessage.error($content)
}

// 验证码倒计时
const startCountdown = (duration) => {
    if (intervalId) clearInterval(intervalId); // 清除之前的倒计时（如果有的话）
    secondsLeft.value = duration;
    intervalId = setInterval(() => {
      if (secondsLeft.value <= 0) {
        clearInterval(intervalId);
        intervalId = null;
        isSending.value = false;
        secondsLeft.value = 0; // 重置为0
      } else {
        secondsLeft.value--;
      }
    }, 1000);
  };

// 获取验证码
const getVerifyCode = () => {
   // 判断邮箱是否校验无误
  if (!status.value) {
    errorNotice('请输入有效的邮箱地址')
      return;
  }
   if (userFormData.email) {
      loading.value = true;
      axios.post('/api/user/getVerifyCode', userFormData)
        .then(response => {
         loading.value = false; 
        // 处理成功的响应
        notice("验证码已发放，请登录邮箱查看");
        // 更新状态并开始倒计时
        isSending.value = true;
        startCountdown(60);
        })
        .catch(error => {
          loading.value = false;
          // 处理错误响应
          errorNotice("未知异常，请重试");    
        });
   } else {
      loading.value = false;
      errorNotice('请输入邮箱号码！');
      return; // 阻止进一步执行
   }
}




// 验证操作者是否为本人
const getAndVerify = () => {
   // 判断邮箱是否校验无误
  if (!status.value) {
    errorNotice('请输入有效的邮箱地址')
    return;
  }
  if (opType.value == 3 && userFormData.email && userFormData.verifyCode) {
    loading.value = true;
    axios.post('/api/user/verify', userFormData)
    .then(response => {
      loading.value = false;
        // 处理成功的响应
      notice('验证通过，请重置密码');
      showPanel(0); 
      })
      .catch(error => {
        loading.value = false;
        // 处理错误响应
        errorNotice("验证失败，请稍后重试！");  
        return;
      });
    }
}

// 重置用户密码
const resetPwd = () => {
   // 判断邮箱是否校验无误
  if (!status.value) {
    errorNotice('请输入有效的邮箱地址')
      return;
  }
  if (opType.value == 0 && userFormData.email && userFormData.password) {
    loading.value = true;
    axios.post('/api/user/resetPwd', userFormData)
    .then(response => {
      loading.value = false;
        // 处理成功的响应
      notice("重置密码成功，请重新登录");
      showPanel(1); 
      })
      .catch(error => {
        loading.value = false;
        // 处理错误响应
        errorNotice("验证失败，请稍后重试！");    
        return;
      });
    }
}

  // 注册
const signIn = () => {
   // 判断邮箱是否校验无误
  if (!status.value) {
    errorNotice('请输入有效的邮箱地址')
      return;
  }
  if (opType.value == 2 && userFormData.email && userFormData.password) {
    loading.value = true;
    axios.post('/api/user/signIn', userFormData)
    .then(response => {
      loading.value = false;
      if (response.data.msg != null) {
        errorNotice(response.data.msg);
        return;
      }
        // 处理成功响应
      notice("注册成功，请点击后进行登录");    
      showPanel(1); 
      })
      .catch(error => {
        loading.value = false;
        // 处理错误响应
        errorNotice("注册失败，请重试");    
        return;
      });
  }
}

// base64解码
function decodeBase64UserName(encodedUserName) {
    try {
        // 尝试解码Base64编码的字符串
        var decodedBytes = atob(encodedUserName);
        // 将解码后的字节（实际上是UTF-8编码的字符串）转换为JavaScript字符串
        var decodedUserName = decodeURIComponent(escape(decodedBytes)); // 这种方法通常用于处理特殊字符，但在这里可能不是必需的，除非你知道编码的字符串包含需要额外处理的字符
        // 注意：上面的decodeURIComponent(escape(...))组合在某些情况下可能不是必需的，并且可能因浏览器而异。
        // 对于简单的Base64 UTF-8字符串，通常只需要atob()就足够了。

        // 直接返回解码后的用户名
        return decodedUserName;
    } catch (e) {
        // 处理解码过程中可能出现的错误（例如，如果输入不是有效的Base64编码）
        console.error('Failed to decode Base64 string:', e);
        return null; // 或者返回一个错误消息、抛出一个错误等
    }
}



// 登录
const login = (isUserType) => {
  // 判断邮箱是否校验无误
  if (!status.value) {
    errorNotice('请输入有效的邮箱地址')
      return;
  }
  if (opType.value == 1 && userFormData.email && userFormData.password) {
    // 用户登录
    if (isUserType === 1) {
      loading.value = true;
      axios.post('/api/user/login', userFormData)
        .then(response => {
          loading.value = false;
          // 设置用户token
          localStorage.setItem('userToken',response.headers["usertoken"]);
          // 设置用户昵称
          localStorage.setItem('userName',decodeBase64UserName(response.headers["username"]));
          // 设置用户id
          localStorage.setItem('userId', response.headers["userid"])
          // 路由跳转
          router.push('/user');
          })
          .catch(error => {
            // 处理错误响应
            loading.value = false;
            errorNotice("账号或密码错误，请重试");
            return;
          });
    } else {
      // 管理员登录
      supervisorFormData.account = userFormData.email;
      supervisorFormData.password = userFormData.password;
      loading.value = true;
      axios.post('/api/supervisor/login', supervisorFormData)
        .then(response => {
          loading.value = false;
          console.log(response.headers)
          // 设置管理员token
          localStorage.setItem('supervisorToken',response.headers["supervisortoken"]);
          // 设置管理员昵称
          localStorage.setItem('supervisorName',decodeBase64UserName(response.headers["supervisorname"]));
          // 路由跳转
          router.push('/supervisor');
          })
          .catch(error => {
            // 处理错误响应
            loading.value = false;
            errorNotice("账号或密码错误，请重试");
            return;
          });
    }
  }
   
}
   
// 表单之间的跳转
const showPanel = (type) => {
  opType.value = type;
  // 清空相关内容信息
  restForm();
}

const restForm = () => {
  userFormData.email = '',
  userFormData.password = '',
  userFormData.verifyCode = '',
  supervisorFormData.account = '',
  supervisorFormData.password = ''
}

// 显示密码
const show_hide_password = (event) => {
  const target = event.target;
	var input = document.getElementById('password');
	if (input.getAttribute('type') == 'password') {
		target.classList.add('view');
		input.setAttribute('type', 'text');
	} else {
		target.classList.remove('view');
		input.setAttribute('type', 'password');
	}
};
</script>  
  
<style lang="scss" scoped>  
@import url('https://fonts.googleapis.com/css2?family=El+Messiri:wght@700&display=swap');

.inline-links p {
    display: inline-block; /* 使p标签表现为行内块级元素 */
    margin: 0 25px; /* 可选：添加一些外边距以分隔链接 */
}

* {
  margin: 0;
  padding: 0;
  font-family: 'El Messiri', sans-serif;
}

body {
  background: #031323;
  overflow: hidden;
}

.fas {
  width: 32px;
}

section {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(-45deg, #ee7752, #e73c7e, #23a6d5, #23d5ab);
  background-size: 400% 400%;
  animation: gradient 10s ease infinite;
}

@keyframes gradient {
    0% {
      background-position: 0% 50%;
      }
    50% {
      background-position: 100% 50%;
      }
    100% {
      background-position: 0% 50%;
      }
}

.box {
  position: relative;
  
  .square {
    position: absolute;
    background: rgba(255, 255, 255, 0.1);
    backdrop-filter: blur(5px);
    box-shadow: 0 25px 45px rgba(0, 0, 0, 0.1);
    border: 1px solid rgba(255, 255, 255, 0.15);
    border-radius: 15px;
    animation: square 10s linear infinite;
    animation-delay: calc(-1s * var(--i));
  }
  
  @keyframes square {
    0%,100% {
      transform: translateY(-20px);
    }
    
    50% {
      transform: translateY(20px);
    }
  }
  
  .square:nth-child(1) {
    width: 100px;
    height: 100px;
    top: -15px;
    right: -45px;
  }
  
  .square:nth-child(2) {
    width: 150px;
    height: 150px;
    top: 105px;
    left: -125px;
    z-index: 2;
  }
  
  .square:nth-child(3) {
    width: 60px;
    height: 60px;
    bottom: 85px;
    right: -45px;
    z-index: 2;
  }
  
  .square:nth-child(4) {
    width: 50px;
    height: 50px;
    bottom: 35px;
    left: -95px;
  }
  
  .square:nth-child(5) {
    width: 50px;
    height: 50px;
    top: -15px;
    left: -25px;
  }
  
  .square:nth-child(6) {
    width: 85px;
    height: 85px;
    top: 165px;
    right: -155px;
    z-index: 2;
  }
}

.container {
  position: relative;
  padding: 50px;
  width: 260px;
  min-height: 380px;
  display: flex;
  justify-content: center;
  align-items: center;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(5px);
  border-radius: 10px;
  box-shadow: 0 25px 45px rgba(0, 0, 0, 0.2);
}

.container::after {
  content: '';
  position: absolute;
  top: 5px;
  right: 5px;
  bottom: 5px;
  left: 5px;
  border-radius: 5px;
  pointer-events: none;
  background: linear-gradient( to bottom, rgba(255, 255, 255, 0.1) 0%, rgba(255, 255, 255, 0.1) 2%
  );
}

.form {
  position: relative;
  width: 100%;
  height: 100%;

   h2 {
    color: #fff;
    letter-spacing: 2px;
    margin-bottom: 30px;
  }


  .inputBx {
    position: relative;
    width: 100%;
    margin-bottom: 20px;
    
    input {
      width: 80%;
      outline: none;
      border: none;
      border: 1px solid rgba(255, 255, 255, 0.2);
      background: rgba(255, 255, 255, 0.2);
      padding: 8px 10px;
      padding-left: 40px;
      border-radius: 15px;
      color: #fff;
      font-size: 16px;
      box-shadow: 0 5px 15px rgba(0, 0, 0, 0.05);
    }
    
    .password-control {
      position: absolute;
      top: 11px;
      right: 10px;
      display: inline-block;
      width: 20px;
      height: 20px;
      background: url(https://snipp.ru/demo/495/view.svg) 0 0 no-repeat;
      transition: 0.5s;
}
        
      .view {
         background: url(https://snipp.ru/demo/495/no-view.svg) 0 0 no-repeat;
        transition: 0.5s;
  }

    
  
    .fas {
      position: absolute;
      top: 13px;
      left: 13px;
    }
    
    input[type="submit"] {
      background: #fff;
      color: #111;
      max-width: 100px;
      padding: 8px 10px;
      box-shadow: none;
      letter-spacing: 1px;
      cursor: pointer;
      transition: 1.5s;
    }
    
    input[type="submit"]:hover {
      background: linear-gradient(115deg, 
        rgba(0,0,0,0.10), 
        rgba(255,255,255,0.25));
      color: #fff;
      transition: .5s;
    }
    
    input::placeholder {
      color: #fff;
    }
    
    span {
        position: absolute;
        left: 30px;
        padding: 10px;
        display: inline-block;
        color: #fff;
        transition: .5s;
        pointer-events: none;
      }
    
    input:focus ~ span,
    input:valid ~ span {
      transform: translateX(-30px) translateY(-25px);
      font-size: 12px;
    }
  }
  
  p {
    color: #fff;
    font-size: 15px;
    margin-top: 5px;
  
    a {
      color: #fff;
    }
    
    a:hover {
      background-color: #000;
      background-image: linear-gradient(to right, #434343 0%, black 100%);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
    }
  }
}

.remember {
  position: relative;
  display: inline-block;
  color: #fff;
  margin-bottom: 10px;
  cursor: pointer;
}
</style>