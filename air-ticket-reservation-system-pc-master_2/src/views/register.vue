<template>
  <div class="register-container">
    <el-form :model="registerForm" :rules="rules" ref="registerForm" label-width="100px" class="register-form">
      <h3 class="title">航空订票系统 - 用户注册</h3>
      <el-form-item label="用户名" prop="username">
        <el-input v-model="registerForm.username" placeholder="请输入用户名"></el-input>
      </el-form-item>
      <el-form-item label="密码" prop="userPassword">
        <el-input type="password" v-model="registerForm.userPassword" placeholder="请输入密码"></el-input>
      </el-form-item>
      <el-form-item label="确认密码" prop="checkPassword">
        <el-input type="password" v-model="registerForm.checkPassword" placeholder="请再次输入密码"></el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="submitForm('registerForm')" :loading="loading">注册</el-button>
        <el-button @click="resetForm('registerForm')">重置</el-button>
      </el-form-item>
      <p class="login-link">已有账号? <router-link to="/login">立即登录</router-link></p>
    </el-form>
  </div>
</template>

<script>
export default {
  name: 'Register',
  data() {
    // 密码一致性验证
    const validatePass2 = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请再次输入密码'));
      } else if (value !== this.registerForm.userPassword) {
        callback(new Error('两次输入密码不一致!'));
      } else {
        callback();
      }
    };
    return {
      registerForm: {
        username: '',
        userPassword: '',
        checkPassword: ''
      },
      rules: {
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' },
          { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
        ],
        userPassword: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { min: 6, message: '密码长度至少为 6 个字符', trigger: 'blur' }
        ],
        checkPassword: [
          { required: true, message: '请再次输入密码', trigger: 'blur' },
          { validator: validatePass2, trigger: 'blur' }
        ]
      },
      loading: false
    };
  },
  methods: {
    submitForm(formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          this.loading = true;
          this.$axios.post('/api/user/register', {
            username: this.registerForm.username,
            userPassword: this.registerForm.userPassword,
            checkPassword: this.registerForm.checkPassword
          }).then(response => {
            this.loading = false;
            if (response.data.code === 200 || response.data.code === 201) {
              this.$message.success('注册成功，请登录');
              this.$router.push('/login');
            } else {
              this.$message.error(response.data.message || '注册失败');
            }
          }).catch(error => {
            this.loading = false;
            if (error.response && error.response.data) {
              this.$message.error(error.response.data.message || '注册失败');
            } else {
              this.$message.error('注册失败，请稍后重试');
            }
          });
        } else {
          return false;
        }
      });
    },
    resetForm(formName) {
      this.$refs[formName].resetFields();
    }
  }
};
</script>

<style scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #f3f3f3;
}
.register-form {
  width: 500px;
  padding: 35px;
  background: #fff;
  border-radius: 5px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}
.title {
  text-align: center;
  margin-bottom: 30px;
  color: #409EFF;
}
.login-link {
  text-align: right;
  margin-top: 20px;
  font-size: 14px;
}
</style> 