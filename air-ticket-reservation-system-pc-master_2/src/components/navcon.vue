<!--头部菜单-->
<template>
  <div class="navcon">
    <div class="left">
      <i class="el-icon-s-fold" @click="toggleSidebar"></i>
    </div>
    <div class="right">
      <el-dropdown trigger="click" @command="handleCommand">
        <span class="el-dropdown-link">
          <i class="el-icon-s-custom"></i> 个人中心<i class="el-icon-arrow-down el-icon--right"></i>
        </span>
        <el-dropdown-menu slot="dropdown">
          <el-dropdown-item command="balance" v-if="!isAdmin">查看余额</el-dropdown-item>
          <el-dropdown-item command="logout">退出登录</el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
    </div>

    <!-- 余额对话框 -->
    <el-dialog title="账户余额" :visible.sync="balanceDialogVisible" width="400px">
      <div v-if="userBalance !== null" class="balance-info">
        <p>当前余额: <span class="balance">{{ userBalance || 0 }} 元</span></p>
      </div>
      <div v-else class="loading-balance">
        <el-skeleton :rows="1" animated />
      </div>
    </el-dialog>
  </div>
</template>
<script>
export default {
  name: 'navcon',
  data() {
    return {
      isCollapse: true,
      balanceDialogVisible: false,
      userBalance: null,
      loading: false,
      userInfo: {}
    };
  },
  computed: {
    isAdmin() {
      return sessionStorage.getItem('role') === '1';
    }
  },
  methods: {
    toggleSidebar() {
      this.isCollapse = !this.isCollapse;
      this.$root.Bus.$emit('toggle', this.isCollapse);
      this.$root.Bus.$emit('toggle-nav', this.isCollapse);
    },
    handleCommand(command) {
      if (command === 'logout') {
        this.logout();
      } else if (command === 'balance') {
        this.showBalance();
      }
    },
    logout() {
      this.$confirm('确定要退出登录吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        // 清除登录信息
        sessionStorage.removeItem('token');
        sessionStorage.removeItem('role');
        sessionStorage.removeItem('user');
        sessionStorage.removeItem('userInfo');
        sessionStorage.removeItem('username');
        
        // 跳转到登录页
        this.$router.push('/login');
        
                this.$message({
                  type: 'success',
          message: '已退出登录'
        });
      }).catch(() => {
        // 取消退出
      });
    },
    showBalance() {
      this.balanceDialogVisible = true;
      this.userBalance = null;
      
      this.$axios.get('/user/info')
        .then(response => {
          if (response.data.code === 1) {
            this.userInfo = response.data.data;
            this.userBalance = this.userInfo.balance;
          } else {
            this.$message.error(response.data.message || '获取余额失败');
          }
        })
        .catch(error => {
          this.$message.error('获取余额失败，请稍后重试');
          console.error(error);
        });
  }
}
};
</script>
<style scoped>
.navcon {
  width: 100%;
  height: 50px;
  line-height: 50px;
  display: flex;
  justify-content: space-between;
  border-bottom: 1px solid #ddd;
  padding: 0 20px;
  box-sizing: border-box;
}
.left {
  display: flex;
  align-items: center;
}
.left i {
  font-size: 20px;
  cursor: pointer;
}
.right {
  display: flex;
  align-items: center;
}
.el-dropdown-link {
  cursor: pointer;
  color: #409EFF;
}
.balance-info {
  text-align: center;
  font-size: 16px;
}
.balance {
  font-weight: bold;
  color: #67C23A;
  font-size: 20px;
}
</style>
