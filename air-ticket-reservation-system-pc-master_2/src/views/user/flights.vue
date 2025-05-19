<template>
  <div class="flights-container">
    <div class="search-container">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="出发城市">
          <el-input v-model="searchForm.departure" placeholder="请输入出发城市"></el-input>
        </el-form-item>
        <el-form-item label="到达城市">
          <el-input v-model="searchForm.arrival" placeholder="请输入到达城市"></el-input>
        </el-form-item>
        <el-form-item label="出发日期">
          <el-date-picker
            v-model="searchForm.date"
            type="date"
            placeholder="选择日期"
            format="yyyy-MM-dd"
            value-format="yyyy-MM-dd"
            :picker-options="dateOptions">
          </el-date-picker>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="searchFlights" :loading="loading">查询</el-button>
        </el-form-item>
      </el-form>
    </div>

    <el-table
      v-loading="loading"
      :data="flightList"
      border
      style="width: 100%">
      <el-table-column
        prop="flightNumber"
        label="航班号"
        width="120">
      </el-table-column>
      <el-table-column
        prop="departure"
        label="出发城市"
        width="120">
      </el-table-column>
      <el-table-column
        prop="arrival"
        label="到达城市"
        width="120">
      </el-table-column>
      <el-table-column
        prop="departureTime"
        label="出发时间"
        width="180">
      </el-table-column>
      <el-table-column
        prop="arrivalTime"
        label="到达时间"
        width="180">
      </el-table-column>
      <el-table-column
        prop="price"
        label="价格(元)"
        width="120">
      </el-table-column>
      <el-table-column
        prop="seatsLeft"
        label="剩余座位"
        width="120">
      </el-table-column>
      <el-table-column
        prop="airline"
        label="航空公司"
        width="150">
      </el-table-column>
      <el-table-column
        label="操作"
        width="150">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="primary"
            @click="bookFlight(scope.row)">预订</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 预订对话框 -->
    <el-dialog title="预订航班" :visible.sync="bookingDialogVisible" width="500px">
      <el-form :model="bookingForm" label-width="100px" :rules="bookingRules" ref="bookingForm">
        <el-form-item label="航班号">
          <span>{{ selectedFlight.flightNumber }}</span>
        </el-form-item>
        <el-form-item label="出发/到达">
          <span>{{ selectedFlight.departure }} - {{ selectedFlight.arrival }}</span>
        </el-form-item>
        <el-form-item label="出发时间">
          <span>{{ selectedFlight.departureTime }}</span>
        </el-form-item>
        <el-form-item label="价格">
          <span>{{ selectedFlight.price }} 元</span>
        </el-form-item>
        <el-form-item label="座位类型" prop="seatType">
          <el-select v-model="bookingForm.seatType" placeholder="请选择座位类型">
            <el-option label="经济舱" value="ECONOMY"></el-option>
            <el-option label="商务舱" value="BUSINESS"></el-option>
            <el-option label="头等舱" value="FIRST"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="乘客" prop="passengerIds">
          <el-select
            v-model="bookingForm.passengerIds"
            multiple
            placeholder="请选择乘客"
            :loading="passengersLoading">
            <el-option
              v-for="item in passengers"
              :key="item.id"
              :label="item.name"
              :value="item.id">
            </el-option>
          </el-select>
          <div class="add-passenger">
            <router-link to="/user/passengers">添加乘客</router-link>
          </div>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="bookingDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitBooking" :loading="bookingSubmitting">确认预订</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'UserFlights',
  data() {
    return {
      searchForm: {
        departure: '',
        arrival: '',
        date: new Date().toISOString().split('T')[0] // 默认今天
      },
      dateOptions: {
        disabledDate(time) {
          // 禁用今天之前的日期
          return time.getTime() < Date.now() - 8.64e7; // 86400000毫秒 = 1天
        }
      },
      loading: false,
      flightList: [],
      bookingDialogVisible: false,
      selectedFlight: {},
      bookingForm: {
        flightId: null,
        seatType: 'ECONOMY',
        passengerIds: []
      },
      bookingRules: {
        seatType: [
          { required: true, message: '请选择座位类型', trigger: 'change' }
        ],
        passengerIds: [
          { required: true, message: '请选择乘客', trigger: 'change' },
          { type: 'array', min: 1, message: '请至少选择一位乘客', trigger: 'change' }
        ]
      },
      passengers: [],
      passengersLoading: false,
      bookingSubmitting: false
    };
  },
  methods: {
    searchFlights() {
      if (!this.searchForm.departure || !this.searchForm.arrival || !this.searchForm.date) {
        this.$message.warning('请填写完整的查询条件');
        return;
      }
      
      // 验证选择的日期不早于今天
      const selectedDate = new Date(this.searchForm.date);
      const today = new Date();
      today.setHours(0, 0, 0, 0); // 设置为今天的开始时间
      
      if (selectedDate < today) {
        this.$message.warning('不能查询今天之前的航班');
        return;
      }
      
      this.loading = true;
      const params = {
        departure: this.searchForm.departure,
        arrival: this.searchForm.arrival,
        date: this.searchForm.date
      };
      
      this.$axios.get('/flights', { params })
        .then(response => {
          this.loading = false;
          if (response.data.code === 200) {
            // 过滤出当前时间之后的航班
            const now = new Date();
            this.flightList = response.data.data.filter(flight => {
              // 假设departureTime格式为"yyyy-MM-dd HH:mm:ss"
              const departureTime = new Date(flight.departureTime);
              return departureTime > now;
            });
            
            if (this.flightList.length === 0) {
              this.$message.info('没有找到符合条件的航班');
            }
          } else {
            this.$message.error(response.data.message || '查询失败');
          }
        })
        .catch(error => {
          this.loading = false;
          this.$message.error('查询失败，请稍后重试');
          console.error(error);
        });
    },
    bookFlight(flight) {
      this.selectedFlight = flight;
      this.bookingForm.flightId = flight.id;
      this.loadPassengers();
      this.bookingDialogVisible = true;
    },
    loadPassengers() {
      this.passengersLoading = true;
      this.$axios.get('/user/passengers')
        .then(response => {
          this.passengersLoading = false;
          if (response.data.code === 200) {
            this.passengers = response.data.data;
            if (this.passengers.length === 0) {
              this.$message.info('您还没有添加乘客信息，请先添加乘客');
            }
          } else {
            this.$message.error(response.data.message || '获取乘客信息失败');
          }
        })
        .catch(error => {
          this.passengersLoading = false;
          this.$message.error('获取乘客信息失败，请稍后重试');
          console.error(error);
        });
    },
    submitBooking() {
      this.$refs.bookingForm.validate(valid => {
        if (valid) {
          this.bookingSubmitting = true;
          this.$axios.post('/user/orders', this.bookingForm)
            .then(response => {
              this.bookingSubmitting = false;
              if (response.data.code === 200) {
                this.$message.success('预订成功');
                this.bookingDialogVisible = false;
                // 跳转到订单页面进行支付
                this.$router.push({
                  path: '/user/orders',
                  query: { orderId: response.data.data.orderId }
                });
              } else {
                this.$message.error(response.data.message || '预订失败');
              }
            })
            .catch(error => {
              this.bookingSubmitting = false;
              if (error.response && error.response.data) {
                this.$message.error(error.response.data.message || '预订失败');
              } else {
                this.$message.error('预订失败，请稍后重试');
              }
              console.error(error);
            });
        }
      });
    }
  },
  mounted() {
    // 页面加载时自动查询航班
    this.searchFlights();
  }
};
</script>

<style scoped>
.flights-container {
  padding: 20px;
}
.search-container {
  margin-bottom: 20px;
  background-color: #f5f7fa;
  padding: 20px;
  border-radius: 4px;
}
.add-passenger {
  margin-top: 5px;
  font-size: 13px;
}
</style>