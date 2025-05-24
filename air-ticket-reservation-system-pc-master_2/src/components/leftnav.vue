<template>
  <div class="leftnav">
    <el-menu
      :default-active="activeIndex"
      class="el-menu-vertical-demo"
      background-color="rgba(204, 229, 255, 0.7)"
      text-color="#333333"
      active-text-color="#1890ff"
      :collapse="isCollapse"
      :router="true">
      
      <!-- 管理员菜单 -->
      <template v-if="isAdmin">
        <el-menu-item index="/charts/statistics">
          <i class="el-icon-data-analysis"></i>
          <span slot="title">数据统计</span>
        </el-menu-item>
        
        <el-submenu index="1">
          <template slot="title">
            <i class="el-icon-plane"></i>
            <span>航班管理</span>
          </template>
          <el-menu-item index="/fly/flight">航班信息</el-menu-item>
          <!-- <el-menu-item index="/fly/airport">机场信息</el-menu-item> -->
        </el-submenu>
        
        <el-submenu index="2">
          <template slot="title">
            <i class="el-icon-user"></i>
            <span>用户管理</span>
          </template>
          <el-menu-item index="/person/user">普通用户</el-menu-item>
          <!-- <el-menu-item index="/person/employee">员工管理</el-menu-item> -->
        </el-submenu>
        
        <el-menu-item index="/order/order">
          <i class="el-icon-document"></i>
          <span slot="title">订单管理</span>
        </el-menu-item>
        
        <!-- 座位管理、机型管理和公告管理功能已移除 -->
      </template>
      
      <!-- 普通用户菜单 -->
      <template v-else>
        <el-menu-item index="/user/home">
          <i class="el-icon-s-home"></i>
          <span slot="title">用户首页</span>
        </el-menu-item>
        
        <el-menu-item index="/user/flights">
          <i class="el-icon-search"></i>
          <span slot="title">航班查询</span>
        </el-menu-item>
        
        <el-menu-item index="/user/orders">
          <i class="el-icon-tickets"></i>
          <span slot="title">我的订单</span>
        </el-menu-item>
        
        <el-menu-item index="/user/passengers">
          <i class="el-icon-user"></i>
          <span slot="title">乘客管理</span>
        </el-menu-item>
      </template>
  </el-menu>
  </div>
</template>

<script>
export default {
  name: 'leftnav',
  data() {
    return {
      isCollapse: false,
      activeIndex: this.$route.path
      
    };
  },
  computed: {
    isAdmin() {
      return sessionStorage.getItem('role') === '1';
    }
  },
  watch: {
    $route(to) {
      this.activeIndex = to.path;
    }
  },
  mounted() {
    this.$root.Bus.$on('toggle-nav', val => {
      this.isCollapse = !val;
    });
  }
};
</script>

<style scoped>
.leftnav {
  height: 100%;
}
.el-menu {
  border-right: none;
  height: 100%;
  background-color: rgba(163, 198, 236, 0.7);
  backdrop-filter: blur(8px);
  -webkit-backdrop-filter: blur(8px);
  box-shadow: 0 2px 12px 0 rgba(168, 205, 212, 0.1);
}
.el-menu:not(.el-menu--collapse) {
  width: 240px;
}
</style>
