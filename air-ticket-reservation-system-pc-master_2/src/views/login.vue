<template>
  <div class="login-wrap">
    <el-card class="loginCord" v-show="!userLoginShow">
      <div class="loginTitle">登录</div>
      <el-form :model="loginForm" ref="loginForm" :rules="loginRules">
        <el-form-item prop="username">
          <el-input v-model="loginForm.username" placeholder="请输入用户名"></el-input>
        </el-form-item>
        <el-form-item prop="userPassword">
          <el-input type="password"  show-password v-model="loginForm.userPassword" placeholder="请输入密码"></el-input>
        </el-form-item>

        <el-form-item>
          <el-button class="loginButton" type="primary" @click="login('loginForm')">登录</el-button>
        </el-form-item>
      </el-form>

      <el-button style="color: #3a8ee6; margin-left: 10px" type="text" @click="userLogin()">用户登录</el-button>
    </el-card>


    <el-card class="loginCord" v-show="userLoginShow">
      <div class="loginTitle">用户登录</div>
      <el-form :model="userLoginForm" ref="userLoginForm" :rules="userLoginRules">
        <el-form-item prop="username">
          <el-input v-model="userLoginForm.username" placeholder="请输入用户名"></el-input>
        </el-form-item>
        <el-form-item prop="userPassword">
          <el-input type="password" show-password v-model="userLoginForm.userPassword" placeholder="请输入密码"></el-input>
        </el-form-item>
        <el-form-item>
          <el-form-item>
            <el-button class="loginButton" type="primary" @click="loginUser('userLoginForm')">登录</el-button>
          </el-form-item>
        </el-form-item>
      </el-form>
      <el-button style="color: #3a8ee6; margin-left: 200px" type="text" @click="adminLogin()">管理员登录</el-button>
      <el-button style="color: #3a8ee6; margin-left: 10px" type="text" @click="toReg()">注册账号</el-button>
    </el-card>
  </div>
</template>
<script>
export default {
  name: "",
  data(){
    /* 定义初始化变量 */
    return{
      userLoginShow: false,
      
      loginRules:{
        username: [ {required: true, message: '请填写用户名', trigger: 'blur'}],
        userPassword: [
          {required: true, message: '请输入密码', trigger: 'blur'},
          { min: 5, max: 12, message: '长度在 5 到 12 个字符', trigger: 'blur' }
        ]
      },
      loginForm:{
        username:'',
        userPassword:'',
      },
      userLoginRules:{
        username: [ {required: true, message: '请填写用户名', trigger: 'blur'}],
        userPassword: [
          {required: true, message: '请输入密码', trigger: 'blur'},
          { min: 5, max: 12, message: '长度在 5 到 12 个字符', trigger: 'blur' }
        ]
      },
      userLoginForm:{
        username:'',
        userPassword:''
      }
    }
  },
  /* 定义事件函数 */
  methods:{
    userLogin(){
      this.userLoginShow = true
    },
    adminLogin(){
      this.userLoginShow = false
    },
    login(formName){
      this.$refs[formName].validate(valid=>{
        if(valid){
          this.$axios.post('admin/login',this.loginForm).then(res =>{
            if(res.data.code === 1 || res.data.message === "ok"){
              // 根据后端返回的数据判断用户角色
              const userData = res.data.data
              // 检查用户权限，只允许管理员（permission=1）通过管理员登录入口登录
              if(userData.permission === 1) {
                this.$message.success("管理员登录成功")
                // 管理员
                sessionStorage.setItem('token', userData.id || res.data.data)
                sessionStorage.setItem('role', '1')
                sessionStorage.setItem('username', userData.username)
                this.$router.push('/charts/statistics')
              } else {
                // 普通用户尝试通过管理员入口登录
                this.$message.error("您没有管理员权限，请使用用户登录入口")
              }
            }else {
              this.$message.error(res.data.message || '登录失败')
            }
          }).catch(error => {
            if (error.response && error.response.data) {
              this.$message.error(error.response.data.message || '登录失败')
            } else {
              this.$message.error('登录失败，请稍后重试')
            }
          })
        } else {
          this.$message.error('请输入完整')
        }
      })
    },
    
    loginUser(formName){
      this.$refs[formName].validate(valid=>{
        if(valid){
          this.$axios.post('/user/login',this.userLoginForm).then(res =>{
            if(res.data.code === 1 || res.data.message === "ok"){
              // 根据后端返回的数据判断用户角色
              const userData = res.data.data
              // 检查用户权限，只允许普通用户（permission=0）通过用户登录入口登录
              if(userData.permission === 0) {
                this.$message.success("用户登录成功")
                // 保存用户信息
                sessionStorage.setItem('userInfo', JSON.stringify(userData))
                // 使用用户ID作为token
                sessionStorage.setItem('token', userData.id)
                sessionStorage.setItem('role', '0') // 设置为普通用户角色
                
                setTimeout(() => {
                  this.$router.push('/user/home')
                }, 100)
              } else {
                // 管理员尝试通过用户入口登录
                this.$message.error("管理员请使用管理员登录入口")
              }
            }else {
              this.$message.error(res.data.message || '登录失败')
            }
          }).catch(error => {
            if (error.response && error.response.data) {
              this.$message.error(error.response.data.message || '登录失败')
            } else {
              this.$message.error('登录失败，请稍后重试')
            }
          })
        } else
        {
          this.$message.error('请输入完整')
        }
      })
    },
    
    toReg(){
      this.$router.push('/register')
    },
  },
  /* vue的生命周期函数
     当页面加载完毕就会执行created里面的函数
  */
  
}
</script>

<style scoped>
.login-wrap {
  box-sizing: border-box;
  width: 100%;
  height: 100%;
  background-image: url(data:image/svg+xml;base64,PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiIHN0YW5kYWxvbmU9Im5vIj8+Cjxzdmcgd2lkdGg9IjEzNjFweCIgaGVpZ2h0PSI2MDlweCIgdmlld0JveD0iMCAwIDEzNjEgNjA5IiB2ZXJzaW9uPSIxLjEiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyIgeG1sbnM6eGxpbms9Imh0dHA6Ly93d3cudzMub3JnLzE5OTkveGxpbmsiPgogICAgPCEtLSBHZW5lcmF0b3I6IFNrZXRjaCA0Ni4yICg0NDQ5NikgLSBodHRwOi8vd3d3LmJvaGVtaWFuY29kaW5nLmNvbS9za2V0Y2ggLS0+CiAgICA8dGl0bGU+R3JvdXAgMjE8L3RpdGxlPgogICAgPGRlc2M+Q3JlYXRlZCB3aXRoIFNrZXRjaC48L2Rlc2M+CiAgICA8ZGVmcz48L2RlZnM+CiAgICA8ZyBpZD0iQW50LURlc2lnbi1Qcm8tMy4wIiBzdHJva2U9Im5vbmUiIHN0cm9rZS13aWR0aD0iMSIgZmlsbD0ibm9uZSIgZmlsbC1ydWxlPSJldmVub2RkIj4KICAgICAgICA8ZyBpZD0i6LSm5oi35a+G56CB55m75b2VLeagoemqjCIgdHJhbnNmb3JtPSJ0cmFuc2xhdGUoLTc5LjAwMDAwMCwgLTgyLjAwMDAwMCkiPgogICAgICAgICAgICA8ZyBpZD0iR3JvdXAtMjEiIHRyYW5zZm9ybT0idHJhbnNsYXRlKDc3LjAwMDAwMCwgNzMuMDAwMDAwKSI+CiAgICAgICAgICAgICAgICA8ZyBpZD0iR3JvdXAtMTgiIG9wYWNpdHk9IjAuOCIgdHJhbnNmb3JtPSJ0cmFuc2xhdGUoNzQuOTAxNDE2LCA1NjkuNjk5MTU4KSByb3RhdGUoLTcuMDAwMDAwKSB0cmFuc2xhdGUoLTc0LjkwMTQxNiwgLTU2OS42OTkxNTgpIHRyYW5zbGF0ZSg0LjkwMTQxNiwgNTI1LjE5OTE1OCkiPgogICAgICAgICAgICAgICAgICAgIDxlbGxpcHNlIGlkPSJPdmFsLTExIiBmaWxsPSIjQ0ZEQUU2IiBvcGFjaXR5PSIwLjI1IiBjeD0iNjMuNTc0ODc5MiIgY3k9IjMyLjQ2ODM2NyIgcng9IjIxLjc4MzA0NzkiIHJ5PSIyMS43NjYwMDgiPjwvZWxsaXBzZT4KICAgICAgICAgICAgICAgICAgICA8ZWxsaXBzZSBpZD0iT3ZhbC0zIiBmaWxsPSIjQ0ZEQUU2IiBvcGFjaXR5PSIwLjU5OTk5OTk2NCIgY3g9IjUuOTg3NDY0NzkiIGN5PSIxMy44NjY4NjAxIiByeD0iNS4yMTczOTEzIiByeT0iNS4yMTMzMDk5NyI+PC9lbGxpcHNlPgogICAgICAgICAgICAgICAgICAgIDxwYXRoIGQ9Ik0zOC4xMzU0NTE0LDg4LjM1MjAyMTUgQzQzLjg5ODQyMjcsODguMzUyMDIxNSA0OC41NzAyMzQsODMuNjgzODY0NyA0OC41NzAyMzQsNzcuOTI1NDAxNSBDNDguNTcwMjM0LDcyLjE2NjkzODMgNDMuODk4NDIyNyw2Ny40OTg3ODE2IDM4LjEzNTQ1MTQsNjcuNDk4NzgxNiBDMzIuMzcyNDgwMSw2Ny40OTg3ODE2IDI3LjcwMDY2ODgsNzIuMTY2OTM4MyAyNy43MDA2Njg4LDc3LjkyNTQwMTUgQzI3LjcwMDY2ODgsODMuNjgzODY0NyAzMi4zNzI0ODAxLDg4LjM1MjAyMTUgMzguMTM1NDUxNCw4OC4zNTIwMjE1IFoiIGlkPSJPdmFsLTMtQ29weSIgZmlsbD0iI0NGREFFNiIgb3BhY2l0eT0iMC40NSI+PC9wYXRoPgogICAgICAgICAgICAgICAgICAgIDxwYXRoIGQ9Ik02NC4yNzc1NTgyLDMzLjE3MDQ5NjMgTDExOS4xODU4MzYsMTYuNTY1NDkxNSIgaWQ9IlBhdGgtMTIiIHN0cm9rZT0iI0NGREFFNiIgc3Ryb2tlLXdpZHRoPSIxLjczOTEzMDQzIiBzdHJva2UtbGluZWNhcD0icm91bmQiIHN0cm9rZS1saW5lam9pbj0icm91bmQiPjwvcGF0aD4KICAgICAgICAgICAgICAgICAgICA8cGF0aCBkPSJNNDIuMTQzMTcwOCwyNi41MDAyNjgxIEw3LjcxMTkwMTYyLDE0LjU2NDA3MDIiIGlkPSJQYXRoLTE2IiBzdHJva2U9IiNFMEI0QjciIHN0cm9rZS13aWR0aD0iMC43MDI2Nzg5NjQiIG9wYWNpdHk9IjAuNyIgc3Ryb2tlLWxpbmVjYXA9InJvdW5kIiBzdHJva2UtbGluZWpvaW49InJvdW5kIiBzdHJva2UtZGFzaGFycmF5PSIxLjQwNTM1Nzg5OTg3MzE1MywyLjEwODAzNjk1MzQ2OTk4MSI+PC9wYXRoPgogICAgICAgICAgICAgICAgICAgIDxwYXRoIGQ9Ik02My45MjYyMTg3LDMzLjUyMTU2MSBMNDMuNjcyMTMyNiw2OS4zMjUwOTUxIiBpZD0iUGF0aC0xNSIgc3Ryb2tlPSIjQkFDQUQ5IiBzdHJva2Utd2lkdGg9IjAuNzAyNjc4OTY0IiBzdHJva2UtbGluZWNhcD0icm91bmQiIHN0cm9rZS1saW5lam9pbj0icm91bmQiIHN0cm9rZS1kYXNoYXJyYXk9IjEuNDA1MzU3ODk5ODczMTUzLDIuMTA4MDM2OTUzNDY5OTgxIj48L3BhdGg+CiAgICAgICAgICAgICAgICAgICAgPGcgaWQ9Ikdyb3VwLTE3IiB0cmFuc2Zvcm09InRyYW5zbGF0ZSgxMjYuODUwOTIyLCAxMy41NDM2NTQpIHJvdGF0ZSgzMC4wMDAwMDApIHRyYW5zbGF0ZSgtMTI2Ljg1MDkyMiwgLTEzLjU0MzY1NCkgdHJhbnNsYXRlKDExNy4yODU3MDUsIDQuMzgxODg5KSIgZmlsbD0iI0NGREFFNiI+CiAgICAgICAgICAgICAgICAgICAgICAgIDxlbGxpcHNlIGlkPSJPdmFsLTQiIG9wYWNpdHk9IjAuNDUiIGN4PSI5LjEzNDgyNjUzIiBjeT0iOS4xMjc2ODA3NiIgcng9IjkuMTM0ODI2NTMiIHJ5PSI5LjEyNzY4MDc2Ij48L2VsbGlwc2U+CiAgICAgICAgICAgICAgICAgICAgICAgIDxwYXRoIGQ9Ik0xOC4yNjk2NTMxLDE4LjI1NTM2MTUgQzE4LjI2OTY1MzEsMTMuMjE0MjgyNiAxNC4xNzk4NTE5LDkuMTI3NjgwNzYgOS4xMzQ4MjY1Myw5LjEyNzY4MDc2IEM0LjA4OTgwMTE0LDkuMTI3NjgwNzYgMCwxMy4yMTQyODI2IDAsMTguMjU1MzYxNSBMMTguMjY5NjUzMSwxOC4yNTUzNjE1IFoiIGlkPSJPdmFsLTQiIHRyYW5zZm9ybT0idHJhbnNsYXRlKDkuMTM0ODI3LCAxMy42OTE1MjEpIHNjYWxlKC0xLCAtMSkgdHJhbnNsYXRlKC05LjEzNDgyNywgLTEzLjY5MTUyMSkgIj48L3BhdGg+CiAgICAgICAgICAgICAgICAgICAgPC9nPgogICAgICAgICAgICAgICAgPC9nPgogICAgICAgICAgICAgICAgPGcgaWQ9Ikdyb3VwLTE0IiB0cmFuc2Zvcm09InRyYW5zbGF0ZSgyMTYuMjk0NzAwLCAxMjMuNzI1NjAwKSByb3RhdGUoLTUuMDAwMDAwKSB0cmFuc2xhdGUoLTIxNi4yOTQ3MDAsIC0xMjMuNzI1NjAwKSB0cmFuc2xhdGUoMTA2LjI5NDcwMCwgMzUuMjI1NjAwKSI+CiAgICAgICAgICAgICAgICAgICAgPGVsbGlwc2UgaWQ9Ik92YWwtMiIgZmlsbD0iI0NGREFFNiIgb3BhY2l0eT0iMC4yNSIgY3g9IjI5LjExNzY0NzEiIGN5PSIyOS4xNDAyNDM5IiByeD0iMjkuMTE3NjQ3MSIgcnk9IjI5LjE0MDI0MzkiPjwvZWxsaXBzZT4KICAgICAgICAgICAgICAgICAgICA8ZWxsaXBzZSBpZD0iT3ZhbC0yIiBmaWxsPSIjQ0ZEQUU2IiBvcGFjaXR5PSIwLjMiIGN4PSIyOS4xMTc2NDcxIiBjeT0iMjkuMTQwMjQzOSIgcng9IjIxLjU2ODYyNzUiIHJ5PSIyMS41ODUzNjU5Ij48L2VsbGlwc2U+CiAgICAgICAgICAgICAgICAgICAgPGVsbGlwc2UgaWQ9Ik92YWwtMi1Db3B5IiBzdHJva2U9IiNDRkRBRTYiIG9wYWNpdHk9IjAuNCIgY3g9IjE3OS4wMTk2MDgiIGN5PSIxMzguMTQ2MzQxIiByeD0iMjMuNzI1NDkwMiIgcnk9IjIzLjc0MzkwMjQiPjwvZWxsaXBzZT4KICAgICAgICAgICAgICAgICAgICA8ZWxsaXBzZSBpZD0iT3ZhbC0yIiBmaWxsPSIjQkFDQUQ5IiBvcGFjaXR5PSIwLjUiIGN4PSIyOS4xMTc2NDcxIiBjeT0iMjkuMTQwMjQzOSIgcng9IjEwLjc4NDMxMzciIHJ5PSIxMC43OTI2ODI5Ij48L2VsbGlwc2U+CiAgICAgICAgICAgICAgICAgICAgPHBhdGggZD0iTTI5LjExNzY0NzEsMzkuOTMyOTI2OCBMMjkuMTE3NjQ3MSwxOC4zNDc1NjEgQzIzLjE2MTYzNTEsMTguMzQ3NTYxIDE4LjMzMzMzMzMsMjMuMTc5NjA5NyAxOC4zMzMzMzMzLDI5LjE0MDI0MzkgQzE4LjMzMzMzMzMsMzUuMTAwODc4MSAyMy4xNjE2MzUxLDM5LjkzMjkyNjggMjkuMTE3NjQ3MSwzOS45MzI5MjY4IFoiIGlkPSJPdmFsLTIiIGZpbGw9IiNCQUNBRDkiPjwvcGF0aD4KICAgICAgICAgICAgICAgICAgICA8ZyBpZD0iR3JvdXAtOSIgb3BhY2l0eT0iMC40NSIgdHJhbnNmb3JtPSJ0cmFuc2xhdGUoMTcyLjAwMDAwMCwgMTMxLjAwMDAwMCkiIGZpbGw9IiNFNkExQTYiPgogICAgICAgICAgICAgICAgICAgICAgICA8ZWxsaXBzZSBpZD0iT3ZhbC0yLUNvcHktMiIgY3g9IjcuMDE5NjA3ODQiIGN5PSI3LjE0NjM0MTQ2IiByeD0iNi40NzA1ODgyNCIgcnk9IjYuNDc1NjA5NzYiPjwvZWxsaXBzZT4KICAgICAgICAgICAgICAgICAgICAgICAgPHBhdGggZD0iTTAuNTQ5MDE5NjA4LDEzLjYyMTk1MTIgQzQuMTIyNjI2ODEsMTMuNjIxOTUxMiA3LjAxOTYwNzg0LDEwLjcyMjcyMiA3LjAxOTYwNzg0LDcuMTQ2MzQxNDYgQzcuMDE5NjA3ODQsMy41Njk5NjA5NSA0LjEyMjYyNjgxLDAuNjcwNzMxNzA3IDAuNTQ5MDE5NjA4LDAuNjcwNzMxNzA3IEwwLjU0OTAxOTYwOCwxMy42MjE5NTEyIFoiIGlkPSJPdmFsLTItQ29weS0yIiB0cmFuc2Zvcm09InRyYW5zbGF0ZSgzLjc4NDMxNCwgNy4xNDYzNDEpIHNjYWxlKC0xLCAxKSB0cmFuc2xhdGUoLTMuNzg0MzE0LCAtNy4xNDYzNDEpICI+PC9wYXRoPgogICAgICAgICAgICAgICAgICAgIDwvZz4KICAgICAgICAgICAgICAgICAgICA8ZWxsaXBzZSBpZD0iT3ZhbC0xMCIgZmlsbD0iI0NGREFFNiIgY3g9IjIxOC4zODIzNTMiIGN5PSIxMzguNjg1OTc2IiByeD0iMS42MTc2NDcwNiIgcnk9IjEuNjE4OTAyNDQiPjwvZWxsaXBzZT4KICAgICAgICAgICAgICAgICAgICA8ZWxsaXBzZSBpZD0iT3ZhbC0xMC1Db3B5LTIiIGZpbGw9IiNFMEI0QjciIG9wYWNpdHk9IjAuMzUiIGN4PSIxNzkuNTU4ODI0IiBjeT0iMTc1LjM4MTA5OCIgcng9IjEuNjE3NjQ3MDYiIHJ5PSIxLjYxODkwMjQ0Ij48L2VsbGlwc2U+CiAgICAgICAgICAgICAgICAgICAgPGVsbGlwc2UgaWQ9Ik92YWwtMTAtQ29weSIgZmlsbC1vcGFjaXR5PSIwLjM1IiBmaWxsPSIjRDBCNEI3IiBjeD0iMTgwLjA5ODAzOSIgY3k9IjEwMi41MzA0ODgiIHJ4PSIyLjE1Njg2Mjc1IiByeT0iMi4xNTg1MzY1OSI+PC9lbGxpcHNlPgogICAgICAgICAgICAgICAgICAgIDxwYXRoIGQ9Ik0yOC45OTg1MzgxLDI5Ljk2NzE1OTggTDE3MS4xNTEwMTgsMTMyLjg3NjAyNCIgaWQ9IlBhdGgtMTEiIHN0cm9rZT0iI0NGREFFNiIgb3BhY2l0eT0iMC44Ij48L3BhdGg+CiAgICAgICAgICAgICAgICAgICAgPHBhdGggZD0iTTI4Ljk5ODUzODEsMzkuOTMyOTI2OCBMMjguOTk4NTM4MSwxOC4zNDc1NjEgQzIzLjE2MTYzNTEsMTguMzQ3NTYxIDE4LjMzMzMzMzMsMjMuMTc5NjA5NyAxOC4zMzMzMzMzLDI4Ljk5ODUzODEgQzE4LjMzMzMzMzMsMzUuMTAwODc4MSAyMy4xNjE2MzUxLDM5LjkzMjkyNjggMjguOTk4NTM4MSwzOS45MzI5MjY4IFoiIGlkPSJQYXRoLTEyIiBmaWxsPSIjQkFDQUQ5Ij48L3BhdGg+CiAgICAgICAgICAgICAgICAgICAgPHBhdGggZD0iTTI4Ljk5ODUzODEsNDkuOTMyOTI2OCBMMjguOTk4NTM4MSw1OS4zNDc1NjEgQzIzLjE2MTYzNTEsNTkuMzQ3NTYxIDE4LjMzMzMzMzMsNjQuMTc5NjA5NyAxOC4zMzMzMzMzLDI4Ljk5ODUzODEgQzE4LjMzMzMzMzMsMzUuMTAwODc4MSAyMy4xNjE2MzUxLDQ1LjkzMjkyNjggMjguOTk4NTM4MSw0NS45MzI5MjY4IFoiIGlkPSJQYXRoLTEzIiBmaWxsPSIjQkFDQUQ5Ij48L3BhdGg+CiAgICAgICAgICAgICAgICA8L2c+CiAgICAgICAgICAgICAgICA8ZyBpZD0iR3JvdXAtMTAiIG9wYWNpdHk9IjAuNzk5OTk5OTUyIiB0cmFuc2Zvcm09InRyYW5zbGF0ZSgxMDU0LjEwMDYzNSwgMzYuNjU5MzE3KSByb3RhdGUoLTEwLjAwMDAwMCkgdHJhbnNsYXRlKC0xMDU0LjEwMDYzNSwgLTM2LjY1OTMxNykgdHJhbnNsYXRlKDExMDAuMTAwNjM1LCAzNi42NTkzMTcpIj4KICAgICAgICAgICAgICAgICAgICA8ZWxsaXBzZSBpZD0iT3ZhbC03IiBzdHJva2U9IiNDRkRBRTYiIHN0cm9rZS13aWR0aD0iMC45NDExNzY0NzEiIGN4PSI0My44MTM1NTkzIiBjeT0iMzIiIHJ4PSIxMS4xODY0NDA3IiByeT0iMTEuMjk0MTE3NiI+PC9lbGxpcHNlPgogICAgICAgICAgICAgICAgICAgIDxnIGlkPSJHcm91cC0xMiIgdHJhbnNmb3JtPSJ0cmFuc2xhdGUoMzQuNTk2Nzc0LCAyMy4xMTExMTEpIiBmaWxsPSIjQkFDQUQ5Ij4KICAgICAgICAgICAgICAgICAgICAgICAgPGVsbGlwc2UgaWQ9Ik92YWwtNyIgb3BhY2l0eT0iMC40NSIgY3g9IjkuMTg1MzQ3MTgiIGN5PSI4Ljg4ODg4ODg5IiByeD0iOC40NzQ1NzYyNyIgcnk9IjguNTU2MTQ5NzMiPjwvZWxsaXBzZT4KICAgICAgICAgICAgICAgICAgICAgICAgPHBhdGggZD0iTTkuMTg1MzQ3MTgsMTcuNDQ1MDM4NiBDMTMuODY1NzI2NCwxNy40NDUwMzg2IDE3LjY1OTkyMzUsMTMuNjE0MzE5OSAxNy42NTk5MjM1LDguODg4ODg4ODkgQzE3LjY1OTkyMzUsNC4xNjM0NTc4NyAxMy44NjU3MjY0LDAuMzMyNzM5MTU2IDkuMTg1MzQ3MTgsMC4zMzI3MzkxNTYgTDkuMTg1MzQ3MTgsMTcuNDQ1MDM4NiBaIiBpZD0iT3ZhbC03Ij48L3BhdGg+CiAgICAgICAgICAgICAgICAgICAgPC9nPgogICAgICAgICAgICAgICAgICAgIDxwYXRoIGQ9Ik0zNC42NTk3Mzg1LDI0LjgwOTY5NCBMNS43MTY2NjA4NCw0Ljc2ODc4OTQ1IiBpZD0iUGF0aC0yIiBzdHJva2U9IiNDRkRBRTYiIHN0cm9rZS13aWR0aD0iMC45NDExNzY0NzEiPjwvcGF0aD4KICAgICAgICAgICAgICAgICAgICA8ZWxsaXBzZSBpZD0iT3ZhbCIgc3Ryb2tlPSIjQ0ZEQUU2IiBzdHJva2Utd2lkdGg9IjAuOTQxMTc2NDcxIiBjeD0iMy4yNjI3MTE4NiIgY3k9IjMuMjk0MTE3NjUiIHJ4PSIzLjI2MjcxMTg2IiByeT0iMy4yOTQxMTc2NSI+PC9lbGxpcHNlPgogICAgICAgICAgICAgICAgICAgIDxlbGxpcHNlIGlkPSJPdmFsLUNvcHkiIGZpbGw9IiNGN0UxQUQiIGN4PSIyLjc5NjYxMDE3IiBjeT0iNjEuMTc2NDcwNiIgcng9IjIuNzk2NjEwMTciIHJ5PSIyLjgyMzUyOTQxIj48L2VsbGlwc2U+CiAgICAgICAgICAgICAgICAgICAgPHBhdGggZD0iTTM0LjYzMTI0NDMsMzkuMjkyMjcxMiBMNS4wNjM2NjY2Myw1OS43ODUwODIiIGlkPSJQYXRoLTEwIiBzdHJva2U9IiNDRkRBRTYiIHN0cm9rZS13aWR0aD0iMC45NDExNzY0NzEiPjwvcGF0aD4KICAgICAgICAgICAgICAgIDwvZz4KICAgICAgICAgICAgICAgIDxnIGlkPSJHcm91cC0xOSIgb3BhY2l0eT0iMC4zMyIgdHJhbnNmb3JtPSJ0cmFuc2xhdGUoMTI4Mi41MzcyMTksIDQ0Ni41MDI4NjcpIHJvdGF0ZSgtMTAuMDAwMDAwKSB0cmFuc2xhdGUoLTEyODIuNTM3MjE5LCAtNDQ2LjUwMjg2NykgdHJhbnNsYXRlKDExNDIuNTM3MjE5LCAzMjcuNTAyODY3KSI+CiAgICAgICAgICAgICAgICAgICAgPGcgaWQ9Ikdyb3VwLTE3IiB0cmFuc2Zvcm09InRyYW5zbGF0ZSgxNDEuMzMzNTM5LCAxMDQuNTAyNzQyKSByb3RhdGUoMjc1LjAwMDAwMCkgdHJhbnNsYXRlKC0xNDEuMzMzNTM5LCAtMTA0LjUwMjc0MikgdHJhbnNsYXRlKDEyOS4zMzM1MzksIDkyLjUwMjc0MikiIGZpbGw9IiNCQUNBRDkiPgogICAgICAgICAgICAgICAgICAgICAgICA8Y2lyY2xlIGlkPSJPdmFsLTQiIG9wYWNpdHk9IjAuNDUiIGN4PSIxMS42NjY2NjY3IiBjeT0iMTEuNjY2NjY2NyIgcj0iMTEuNjY2NjY2NyI+PC9jaXJjbGU+CiAgICAgICAgICAgICAgICAgICAgICAgIDxwYXRoIGQ9Ik0yMy4zMzMzMzMzLDIzLjMzMzMzMzMgQzIzLjMzMzMzMzMsMTYuODkwMDExMyAxOC4xMDk5ODg3LDExLjY2NjY2NjcgMTEuNjY2NjY2NywxMS42NjY2NjY3IEM1LjIyMzM0NDU5LDExLjY2NjY2NjcgMCwxNi44OTAwMTEzIDAsMjMuMzMzMzMzMyBMMjMuMzMzMzMzMywyMy4zMzMzMzMzIFoiIGlkPSJPdmFsLTQiIHRyYW5zZm9ybT0idHJhbnNsYXRlKDExLjY2NjY2NywgMTcuNTAwMDAwKSBzY2FsZSgtMSwgLTEpIHRyYW5zbGF0ZSgtMTEuNjY2NjY3LCAtMTcuNTAwMDAwKSAiPjwvcGF0aD4KICAgICAgICAgICAgICAgICAgICA8L2c+CiAgICAgICAgICAgICAgICAgICAgPGNpcmNsZSBpZD0iT3ZhbC01LUNvcHktNiIgZmlsbD0iI0NGREFFNiIgY3g9IjIwMS44MzMzMzMiIGN5PSI4Ny41IiByPSI1LjgzMzMzMzMzIj48L2NpcmNsZT4KICAgICAgICAgICAgICAgICAgICA8cGF0aCBkPSJNMTQzLjUsODguODEyNjY4NSBMMTU1LjA3MDUwMSwxNy42MDM4NTQ0IiBpZD0iUGF0aC0xNyIgc3Ryb2tlPSIjQkFDQUQ5IiBzdHJva2Utd2lkdGg9IjEuMTY2NjY2NjciPjwvcGF0aD4KICAgICAgICAgICAgICAgICAgICA8cGF0aCBkPSJNMTcuNSwzNy4zMzMzMzMzIEwxMjcuNDY2MjUyLDk3LjY0NDk3MzUiIGlkPSJQYXRoLTE4IiBzdHJva2U9IiNCQUNBRDkiIHN0cm9rZS13aWR0aD0iMS4xNjY2NjY2NyI+PC9wYXRoPgogICAgICAgICAgICAgICAgICAgIDxwb2x5bGluZSBpZD0iUGF0aC0xOSIgc3Ryb2tlPSIjQ0ZEQUU2IiBzdHJva2Utd2lkdGg9IjEuMTY2NjY2NjciIHBvaW50cz0iMTQzLjkwMjU5NyAxMjAuMzAyMjgxIDE3NC45MzU0NTUgMjMxLjU3MTM0MiAzOC41IDE0Ny41MTA4NDcgMTI2LjM2Njk0MSAxMTAuODMzMzMzIj48L3BvbHlsaW5lPgogICAgICAgICAgICAgICAgICAgIDxwYXRoIGQ9Ik0xNTkuODMzMzMzLDk5Ljc0NTM4NDIgTDE5NS40MTY2NjcsODkuMjUiIGlkPSJQYXRoLTIwIiBzdHJva2U9IiNFMEI0QjciIHN0cm9rZS13aWR0aD0iMS4xNjY2NjY2NyIgb3BhY2l0eT0iMC42Ij48L3BhdGg+CiAgICAgICAgICAgICAgICAgICAgPHBhdGggZD0iTTIwNS4zMzMzMzMsODIuMTM3MjEwNSBMIDIzOC43MTk0MDYsMzYuMTY2NjY2NyIgaWQ9IlBhdGgtMjQiIHN0cm9rZT0iI0JBRUQ5IiBzdHJva2Utd2lkdGg9IjEuMTY2NjY2NjciPjwvcGF0aD4KICAgICAgICAgICAgICAgICAgICA8cGF0aCBkPSJNNjYuNzIzNDI0LDEzMi4yMzE5ODggTDIwNy4wODMzMzMsOTAuNDE2NjY2NyIgaWQ9IlBhdGgtMjUiIHN0cm9rZT0iI0NGREFFNiIgc3Ryb2tlLXdpZHRoPSIxLjE2NjY2NjciPjwvcGF0aD4KICAgICAgICAgICAgICAgICAgICA8Y2lyY2xlIGlkPSJPdmFsLTUiIGZpbGw9IiNDMURBRUQiIGN4PSIxNTYuOTE2NjY3IiBjeT0iOC43NSIgcj0iOC43NSI+PC9jaXJjbGU+CiAgICAgICAgICAgICAgICAgICAgPGNpcmNsZSBpZD0iT3ZhbC01LUNvcHktMyIgZmlsbD0iI0MxRDUxMCIgY3g9IjM5LjA4MzMzMzMiIGN5PSIxNDguNzUiIHI9IjUuMjUiPjwvY2lyY2xlPgogICAgICAgICAgICAgICAgICAgIDxjaXJjbGUgaWQ9Ik92YWwtNS1Db3B5LTIiIGZpbGwtb3BhY2l0eT0iMC42IiBmaWxsPSIjRDFERUVEIiBjeD0iOC43NSIgY3k9IjMuMjk0MTE3NjUiIHJ4PSIzLjI2MjcxMTg2IiByeT0iMy4yOTQxMTc2NSI+PC9jaXJjbGU+CiAgICAgICAgICAgICAgICAgICAgPHBhdGggZD0iTTM0LjYzMTI0NDMsMzkuMjkyMjcxMiBMNS4wNjM2NjY2Myw1OS43ODUwODIiIGlkPSJQYXRoLTEwIiBzdHJva2U9IiNDRkRBRTYiIHN0cm9rZS13aWR0aD0iMC45NDExNzY0NzEiPjwvcGF0aD4KICAgICAgICAgICAgICAgIDwvZz4KICAgICAgICAgICAgICAgIDxnIGlkPSJHcm91cC0xOSIgb3BhY2l0eT0iMC4zMyIgdHJhbnNmb3JtPSJ0cmFuc2xhdGUoMTI4Mi41MzcyMTksIDQ0Ni41MDI4NjcpIHJvdGF0ZSgtMTAuMDAwMDAwKSB0cmFuc2xhdGUoLTEyODIuNTM3MjE5LCAtNDQ2LjUwMjg2NykgdHJhbnNsYXRlKDExNDIuNTM3MjE5LCAzMjcuNTAyODY3KSI+CiAgICAgICAgICAgICAgICAgICAgPGcgaWQ9Ikdyb3VwLTE3IiB0cmFuc2Zvcm09InRyYW5zbGF0ZSgxNDEuMzMzNTM5LCAxMDQuNTAyNzQyKSByb3RhdGUoMjc1LjAwMDAwMCkgdHJhbnNsYXRlKC0xNDEuMzMzNTM5LCAtMTA0LjUwMjc0MikgdHJhbnNsYXRlKDEyOS4zMzM1MzksIDkyLjUwMjc0MikiIGZpbGw9IiNCQUNBRDkiPgogICAgICAgICAgICAgICAgICAgICAgICA8Y2lyY2xlIGlkPSJPdmFsLTQiIG9wYWNpdHk9IjAuNDUiIGN4PSIxMS42NjY2NjY3IiBjeT0iMTEuNjY2NjY2NyIgcj0iMTEuNjY2NjY2NyI+PC9jaXJjbGU+CiAgICAgICAgICAgICAgICAgICAgICAgIDxwYXRoIGQ9Ik0yMy4zMzMzMzMzLDIzLjMzMzMzMzMgQzIzLjMzMzMzMzMsMTYuODkwMDExMyAxOC4xMDk5ODg3LDExLjY2NjY2NjcgMTEuNjY2NjY2NywxMS42NjY2NjY3IEM1LjIyMzM0NDU5LDExLjY2NjY2NjcgMCwxNi44OTAwMTEzIDAsMjMuMzMzMzMzMyBMMjMuMzMzMzMzMywyMy4zMzMzMzMzIFoiIGlkPSJPdmFsLTQiIHRyYW5zZm9ybT0idHJhbnNsYXRlKDExLjY2NjY2NywgMTcuNTAwMDAwKSBzY2FsZSgtMSwgLTEpIHRyYW5zbGF0ZSgtMTEuNjY2NjY3LCAtMTcuNTAwMDAwKSAiPjwvcGF0aD4KICAgICAgICAgICAgICAgICAgICA8L2c+CiAgICAgICAgICAgICAgICAgICAgPGNpcmNsZSBpZD0iT3ZhbC01LUNvcHktNiIgZmlsbD0iI0NGREFFNiIgY3g9IjIwMS44MzMzMzMiIGN5PSI4Ny41IiByPSI1LjgzMzMzMzMzIj48L2NpcmNsZT4KICAgICAgICAgICAgICAgICAgICA8cGF0aCBkPSJNMTQzLjUsODguODEyNjY4NSBMMTU1LjA3MDUwMSwxNy42MDM4NTQ0IiBpZD0iUGF0aC0xNyIgc3Ryb2tlPSIjQkFDQUQ5IiBzdHJva2Utd2lkdGg9IjEuMTY2NjY2NjciPjwvcGF0aD4KICAgICAgICAgICAgICAgICAgICA8cGF0aCBkPSJNMTcuNSwzNy4zMzMzMzMzIEwxMjcuNDY2MjUyLDk3LjY0NDk3MzUiIGlkPSJQYXRoLTE4IiBzdHJva2U9IiNCQUNBRDkiIHN0cm9rZS13aWR0aD0iMS4xNjY2NjY2NyI+PC9wYXRoPgogICAgICAgICAgICAgICAgICAgIDxwb2x5bGluZSBpZD0iUGF0aC0xOSIgc3Ryb2tlPSIjQ0ZEQUU2IiBzdHJva2Utd2lkdGg9IjEuMTY2NjY2NjciIHBvaW50cz0iMTQzLjkwMjU5NyAxMjAuMzAyMjgxIDE3NC45MzU0NTUgMjMxLjU3MTM0MiAzOC41IDE0Ny41MTA4NDcgMTI2LjM2Njk0MSAxMTAuODMzMzMzIj48L3BvbHlsaW5lPgogICAgICAgICAgICAgICAgICAgIDxwYXRoIGQ9Ik0xNTkuODMzMzMzLDk5Ljc0NTM4NDIgTDE5NS40MTY2NjcsODkuMjUiIGlkPSJQYXRoLTIwIiBzdHJva2U9IiNFMEI0QjciIHN0cm9rZS13aWR0aD0iMS4xNjY2NjY2NyIgb3BhY2l0eT0iMC42Ij48L3BhdGg+CiAgICAgICAgICAgICAgICAgICAgPHBhdGggZD0iTTIwNS4zMzMzMzMsODIuMTM3MjEwNSBMIDIzOC43MTk0MDYsMzYuMTY2NjY2NyIgaWQ9IlBhdGgtMjQiIHN0cm9rZT0iI0JBRUQ5IiBzdHJva2Utd2lkdGg9IjEuMTY2NjY2NjciPjwvcGF0aD4KICAgICAgICAgICAgICAgICAgICA8cGF0aCBkPSJNNjYuNzIzNDI0LDEzMi4yMzE5ODggTDIwNy4wODMzMzMsOTAuNDE2NjY2NyIgaWQ9IlBhdGgtMjUiIHN0cm9rZT0iI0NGREFFNiIgc3Ryb2tlLXdpZHRoPSIxLjE2NjY2NjciPjwvcGF0aD4KICAgICAgICAgICAgICAgICAgICA8Y2lyY2xlIGlkPSJPdmFsLTUiIGZpbGw9IiNDMURBRUQiIGN4PSIxNTYuOTE2NjY3IiBjeT0iOC43NSIgcj0iOC43NSI+PC9jaXJjbGU+CiAgICAgICAgICAgICAgICAgICAgPGNpcmNsZSBpZD0iT3ZhbC01LUNvcHktMyIgZmlsbD0iI0MxRDUxMCIgY3g9IjM5LjA4MzMzMzMiIGN5PSIxNDguNzUiIHI9IjUuMjUiPjwvY2lyY2xlPgogICAgICAgICAgICAgICAgICAgIDxjaXJjbGUgaWQ9Ik92YWwtNS1Db3B5LTIiIGZpbGwtb3BhY2l0eT0iMC42IiBmaWxsPSIjRDFERUVEIiBjeD0iOC43NSIgY3k9IjMuMjk0MTE3NjUiIHJ4PSIzLjI2MjcxMTg2IiByeT0iMy4yOTQxMTc2NSI+PC9jaXJjbGU+CiAgICAgICAgICAgICAgICAgICAgPHBhdGggZD0iTTM0LjYzMTI0NDMsMzkuMjkyMjcxMiBMNS4wNjM2NjY2Myw1OS43ODUwODIiIGlkPSJQYXRoLTEwIiBzdHJva2U9IiNDRkRBRTYiIHN0cm9rZS13aWR0aD0iMC45NDExNzY0NzEiPjwvcGF0aD4KICAgICAgICAgICAgICAgIDwvZz4KICAgICAgICAgICAgPC9nPgogICAgICAgIDwvZz4KICAgIDwvZz4KPC9zdmc+);
  /* background-color: #112346; */
  background-repeat: no-repeat;
  background-position: center right;
  background-size: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
}
.loginCord {
  width: 25%;
  height: 400px;
}

.loginTitle {
  text-align: center;
  font-size: 20px;
  font-weight: 600;
  margin-bottom: 20px;
}

.loginButton {
  width: 100%;
}
.regButton{
  margin-left: -10px !important;
}
.codeClass{
  display: flex;
  justify-content: center;
  align-items: center;
}

</style>
