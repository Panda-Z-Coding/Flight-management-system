<template>
  <div class="flights-container">
    <div class="search-container">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="出发城市">
          <el-input v-model="searchForm.departureCity" placeholder="请输入出发城市"></el-input>
        </el-form-item>
        <el-form-item label="到达城市">
          <el-input v-model="searchForm.arrivalCity" placeholder="请输入到达城市"></el-input>
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
        prop="aircraftModel"
        label="飞机型号"
        width="120">
      </el-table-column>
      <el-table-column
        prop="departureCity"
        label="出发城市"
        width="120">
      </el-table-column>
      <el-table-column
        prop="arrivalCity"
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
        prop="totalSeats"
        label="总座位数"
        width="120">
      </el-table-column>
      <el-table-column
        prop="remainingSeats"
        label="剩余座位"
        width="120">
      </el-table-column>
      <el-table-column
        prop="airline"
        label="航空公司"
        width="150">
      </el-table-column>
      <el-table-column
        prop="createTime"
        label="创建时间"
        width="180">
      </el-table-column>
      <el-table-column
        prop="updateTime"
        label="修改时间"
        width="180">
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

    <!-- 分页区域 -->
    <div class="pagination-container">
      <el-pagination
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="searchForm.page"
        :page-sizes="[10, 20, 30, 50]"
        :page-size="searchForm.pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total">
      </el-pagination>
    </div>

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
        departureCity: '',
        arrivalCity: '',
        date: '',
        page: 1,
        pageSize: 10
      },
      dateOptions: {
        disabledDate(time) {
          // 禁用今天之前的日期
          return time.getTime() < Date.now() - 8.64e7; // 86400000毫秒 = 1天
        }
      },
      loading: false,
      flightList: [],
      //总条数
      total: 0,
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
    async searchFlights() {
      this.loading = true;
      try{
      //构建参数
      const params = {
        page: this.searchForm.page,
        pageSize: this.searchForm.pageSize
      };

      if (this.searchForm.departureCity && this.searchForm.departureCity.trim()) {
        params.departureCity = this.searchForm.departureCity.trim();

      }

      if (this.searchForm.arrivalCity && this.searchForm.arrivalCity.trim()) {
        params.arrivalCity = this.searchForm.arrivalCity.trim();
      }

      if (this.searchForm.date) {
        params.date = this.searchForm.date;
      }
    
      const response = await this.$axios.post("/user/flights/page", params, {
        headers: {
          'Authorization': sessionStorage.getItem('token') ? `Bearer ${sessionStorage.getItem('token')}` : '',
          'Content-Type': 'application/json',
          'Accept': 'application/json'
        }
      })

      if(response.data.code === 1){
        this.flightList = response.data.data.list
        this.total = response.data.data.total

        //根据查询条件给出相应的提示信息
        const hasSearchCriteria = params.departureCity || params.arrivalCity || params.date;
        if (hasSearchCriteria) {
          const message = this.flightList.length ? '查询成功' : '未找到符合条件的航班';
          this.$message[this.flightList.length ? 'success' : 'info'](message);
        }
      } else {
        this.$message.error(response.data.message || '获取航班列表失败');
      }
    } catch (error) {
      console.error('获取航班列表失败:', error);
      this.$message.error('获取航班列表失败，请稍后重试');
    } finally {
      this.loading = false;
    }
    },

    // 查询按钮点击事件
    handleSearch() {
      // 验证选择的日期是否早于今天
      const selectedDate = new Date(this.searchForm.date);
      const today = new Date();
      //清除时间
      today.setHours(0, 0, 0, 0);

      if(selectedDate < today){
        this.$message.error('不能查询今天之前的航班')
        return;
      }

      // 重置页码到第一页
      this.searchForm.page = 1

      // 去除出发城市和到达城市的首尾空格并执行搜索
      this.searchForm.departureCity = this.searchForm.departureCity ? this.searchForm.departureCity.trim():'';
      this.searchForm.arrivalCity = this.searchForm.arrivalCity ? this.searchForm.arrivalCity.trim():'';
      
      // 更新查询参数并执行查询
      this.searchFlights()
    },

    // 重置按钮点击事件
    resetQuery() {
      // 重置查询参数
      this.searchForm = {
        page: 1,
        pageSize: 10,
        departureCity: '',
        arrivalCity:'',
        departureTime:'',
        arrivalTime:''
      }
       // 提示用户
       this.$message.success('已重置查询条件')

      // 重新获取列表
      this.searchFlights()
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
  methods: {
    async searchFlights() {
      this.loading = true;
      try{
      //构建参数
      const params = {
        page: this.searchForm.page,
        pageSize: this.searchForm.pageSize
      };

      if (this.searchForm.departureCity && this.searchForm.departureCity.trim()) {
        params.departureCity = this.searchForm.departureCity.trim();

      }

      if (this.searchForm.arrivalCity && this.searchForm.arrivalCity.trim()) {
        params.arrivalCity = this.searchForm.arrivalCity.trim();
      }

      if (this.searchForm.date) {
        params.date = this.searchForm.date;
      }
    
      const response = await this.$axios.post("/user/flights/page", params, {
        headers: {
          'Authorization': sessionStorage.getItem('token') ? `Bearer ${sessionStorage.getItem('token')}` : '',
          'Content-Type': 'application/json',
          'Accept': 'application/json'
        }
      })

      if(response.data.code === 1){
        this.flightList = response.data.data.list
        this.total = response.data.data.total

        //根据查询条件给出相应的提示信息
        const hasSearchCriteria = params.departureCity || params.arrivalCity || params.date;
        if (hasSearchCriteria) {
          const message = this.flightList.length ? '查询成功' : '未找到符合条件的航班';
          this.$message[this.flightList.length ? 'success' : 'info'](message);
        }
      } else {
        this.$message.error(response.data.message || '获取航班列表失败');
      }
    } catch (error) {
      console.error('获取航班列表失败:', error);
      this.$message.error('获取航班列表失败，请稍后重试');
    } finally {
      this.loading = false;
    }
    },

    // 查询按钮点击事件
    handleSearch() {
      // 验证选择的日期是否早于今天
      const selectedDate = new Date(this.searchForm.date);
      const today = new Date();
      //清除时间
      today.setHours(0, 0, 0, 0);

      if(selectedDate < today){
        this.$message.error('不能查询今天之前的航班')
        return;
      }

      // 重置页码到第一页
      this.searchForm.page = 1

      // 去除出发城市和到达城市的首尾空格并执行搜索
      this.searchForm.departureCity = this.searchForm.departureCity ? this.searchForm.departureCity.trim():'';
      this.searchForm.arrivalCity = this.searchForm.arrivalCity ? this.searchForm.arrivalCity.trim():'';
      
      // 更新查询参数并执行查询
      this.searchFlights()
    },

    // 重置按钮点击事件
    resetQuery() {
      // 重置查询参数
      this.searchForm = {
        page: 1,
        pageSize: 10,
        departureCity: '',
        arrivalCity:'',
        departureTime:'',
        arrivalTime:''
      }
       // 提示用户
       this.$message.success('已重置查询条件')

      // 重新获取列表
      this.searchFlights()
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
    },
    // 处理分页大小变化
    handleSizeChange(val) {
      this.searchForm.pageSize = val;
      this.searchFlights();
    },
    
    // 处理页码变化
    handleCurrentChange(val) {
      this.searchForm.page = val;
      this.searchFlights();
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