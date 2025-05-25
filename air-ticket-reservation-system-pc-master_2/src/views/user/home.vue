<template>
  <div class="user-home">
    <el-row :gutter="20">
      <!-- 欢迎卡片 -->
      <el-col :span="24">
        <el-card class="welcome-card">
          <div class="welcome-content">
            <h2>欢迎使用航空订票系统</h2>
            <p>快捷预订机票，轻松管理行程</p>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <el-row :gutter="20" style="margin-top: 20px;">
      <!-- 航班快速查询 -->
      <el-col :span="16">
        <el-card>
          <div slot="header" class="card-header">
            <span>快速查询航班</span>
          </div>
          <el-form :model="searchForm" :inline="true" class="flight-search-form">
            <el-form-item label="出发城市">
              <el-select v-model="searchForm.departureCity" placeholder="请选择出发城市" filterable>
                <el-option 
                  v-for="city in cities" 
                  :key="city.value" 
                  :label="city.label" 
                  :value="city.value">
                </el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="目的城市">
              <el-select v-model="searchForm.destinationCity" placeholder="请选择目的城市" filterable>
                <el-option 
                  v-for="city in cities" 
                  :key="city.value" 
                  :label="city.label" 
                  :value="city.value">
                </el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="出发日期">
              <el-date-picker
                v-model="searchForm.departureDate"
                type="date"
                placeholder="选择日期"
                :picker-options="dateOptions">
              </el-date-picker>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="searchFlights">查询航班</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
      
      <!-- 用户信息卡片 -->
      <el-col :span="8">
        <el-card class="user-card">
          <div slot="header" class="card-header">
            <span>我的信息</span>
          </div>
          <div v-if="userInfo" class="user-info">
            <p><i class="el-icon-user"></i> 用户名: {{ userInfo.username }}</p>
            <p><i class="el-icon-money"></i> 余额: {{ userInfo.balance !== undefined && userInfo.balance !== null ? userInfo.balance : 0 }} 元</p>
          </div>
          <div v-else class="user-loading">
            <el-skeleton :rows="3" animated />
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <!-- 公告信息 -->
    <el-row style="margin-top: 20px;">
      <el-col :span="24">
        <el-card>
          <div slot="header" class="card-header">
            <span>最新公告</span>
          </div>
          <div v-if="announcements.length > 0">
            <el-timeline>
              <el-timeline-item
                v-for="(announcement, index) in announcements"
                :key="index"
                :timestamp="announcement.createTime"
                placement="top"
                :type="index === 0 ? 'primary' : ''"
              >
                <el-card>
                  <h4>{{ announcement.title }}</h4>
                  <p>{{ announcement.content }}</p>
                </el-card>
              </el-timeline-item>
            </el-timeline>
          </div>
          <div v-else class="no-data">
            <i class="el-icon-bell"></i>
            <p>暂无公告信息</p>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import AnnouncementService from '@/store/announcement.js';

export default {
  name: 'UserHome',
  data() {
    return {
      searchForm: {
        departureCity: '',
        destinationCity: '',
        departureDate: new Date()
      },
      dateOptions: {
        disabledDate(time) {
          return time.getTime() < Date.now() - 8.64e7; // 不能选择今天之前的日期
        }
      },
      cities: [], // 城市列表
      userInfo: null,
      announcements: []
    };
  },
  created() {
    this.getUserInfo();
    this.getAnnouncements();
    this.getCities();
    
    // 监听公告更新事件
    document.addEventListener('announcements-updated', this.handleAnnouncementsUpdated);
  },
  beforeDestroy() {
    // 移除事件监听器
    document.removeEventListener('announcements-updated', this.handleAnnouncementsUpdated);
  },
  methods: {
    // 获取用户信息
    getUserInfo() {
      // 始终从服务器获取最新的用户信息
      this.$axios.get('/user/info').then(res => {
        if (res.data.code === 1) {
          this.userInfo = res.data.data;
          // 更新缓存的用户信息
          sessionStorage.setItem("user", JSON.stringify(this.userInfo));
          sessionStorage.setItem("userInfo", JSON.stringify(this.userInfo));
        }
      }).catch(err => {
        // 如果API请求失败，尝试从本地存储获取
        const userDataJSON = sessionStorage.getItem("user");
        if (userDataJSON) {
          this.userInfo = JSON.parse(userDataJSON);
        }
        console.error('获取用户信息失败', err);
      });
    },
    // 获取公告信息
    getAnnouncements() {
      // 首先从AnnouncementService获取公告
      const serviceAnnouncements = AnnouncementService.getAnnouncements();
      if (serviceAnnouncements && serviceAnnouncements.length > 0) {
        this.announcements = serviceAnnouncements;
      } else {
        // 如果服务中没有公告，则从API获取
        this.$axios.get('/announcement/latest').then(res => {
          if (res.data.code === 1) { // 修改这里，将200改为1，与后端返回的code值匹配
            this.announcements = res.data.data;
            // 将API获取的公告添加到服务中
            if (this.announcements && this.announcements.length > 0) {
              this.announcements.forEach(announcement => {
                AnnouncementService.addAnnouncement(announcement);
              });
            }
          }
        }).catch(err => {
          console.error('获取公告信息失败', err);
        });
      }
    },
    
    // 处理公告更新事件
    handleAnnouncementsUpdated(event) {
      if (event.detail && event.detail.announcements) {
        this.announcements = event.detail.announcements;
      }
    },
    // 获取城市列表
    getCities() {
      // 使用静态数据替代API调用
      const staticCityData = [
        { id: 1, name: '北京' },
        { id: 2, name: '上海' },
        { id: 3, name: '广州' },
        { id: 4, name: '深圳' },
        { id: 5, name: '成都' },
        { id: 6, name: '厦门' },
        { id: 7, name: '重庆' }
      ];
      
      this.cities = staticCityData.map(city => ({
        value: city.id,
        label: city.name
      }));
      
      /* API方式调用
      this.$axios.get('/airport/cities').then(res => {
        if (res.data.code === 200) {
          this.cities = res.data.data.map(city => ({
            value: city.id,
            label: city.name
          }));
        }
      }).catch(err => {
        console.error('获取城市列表失败', err);
      });
      */
    },
    // 搜索航班
    searchFlights() {
      if (!this.searchForm.departureCity || !this.searchForm.destinationCity || !this.searchForm.departureDate) {
        this.$message.warning('请填写完整的搜索信息');
        return;
      }

      // 验证选择的日期不早于今天
      const selectedDate = new Date(this.searchForm.departureDate);
      const today = new Date();
      today.setHours(0, 0, 0, 0); // 设置为今天的开始时间
      
      if (selectedDate < today) {
        this.$message.warning('请选择今天或之后的日期');
        return;
      }

      // 跳转到航班搜索页面，带上查询参数
      this.$router.push({
        path: '/user/flights',
        query: {
          departureCity: this.getCityNameById(this.searchForm.departureCity),
          destinationCity: this.getCityNameById(this.searchForm.destinationCity),
          departureDate: this.formatDate(this.searchForm.departureDate)
        }
      });
    },
    // 格式化日期
    formatDate(date) {
      const year = date.getFullYear();
      const month = (date.getMonth() + 1).toString().padStart(2, '0');
      const day = date.getDate().toString().padStart(2, '0');
      return `${year}-${month}-${day}`;
    },
    
    // 根据城市ID获取城市名称
    getCityNameById(cityId) {
      if (!cityId) return '';
      const city = this.cities.find(city => city.value === cityId);
      return city ? city.label : '';
    }
  }
};
</script>

<style scoped>
.user-home {
  padding: 20px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.welcome-card {
  background: linear-gradient(to right, #1989fa, #5cadff);
  color: white;
}
.welcome-content {
  text-align: center;
  padding: 20px 0;
}
.welcome-content h2 {
  font-size: 24px;
  margin-bottom: 10px;
}
.welcome-content p {
  font-size: 16px;
  opacity: 0.8;
}
.flight-search-form {
  display: flex;
  flex-wrap: wrap;
  justify-content: space-between;
}
.user-info p {
  margin: 10px 0;
  font-size: 14px;
}
.vip-tag {
  background-color: #E6A23C;
  color: white;
  padding: 2px 6px;
  border-radius: 4px;
  font-size: 12px;
}
.no-data {
  text-align: center;
  padding: 30px 0;
  color: #909399;
}
.no-data i {
  font-size: 30px;
  margin-bottom: 10px;
}
</style>