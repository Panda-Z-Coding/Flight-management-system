<template>
  <div class="seats-container">
    <div class="header">
      <h2>座位管理</h2>
      <div class="search-form">
        <el-form :inline="true" :model="searchForm">
          <el-form-item label="航班号">
            <el-input v-model="searchForm.flightNumber" placeholder="请输入航班号"></el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="searchSeats" :loading="loading">查询</el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>

    <div v-if="currentFlight" class="flight-info">
      <h3>航班信息</h3>
      <div class="info-card">
        <div class="info-item">
          <span class="label">航班号:</span>
          <span>{{ currentFlight.flightNumber }}</span>
        </div>
        <div class="info-item">
          <span class="label">航线:</span>
          <span>{{ currentFlight.departure }} - {{ currentFlight.arrival }}</span>
        </div>
        <div class="info-item">
          <span class="label">起飞时间:</span>
          <span>{{ currentFlight.departureTime }}</span>
        </div>
        <div class="info-item">
          <span class="label">到达时间:</span>
          <span>{{ currentFlight.arrivalTime }}</span>
        </div>
        <div class="info-item">
          <span class="label">总座位数:</span>
          <span>{{ currentFlight.seats }}</span>
        </div>
        <div class="info-item">
          <span class="label">剩余座位:</span>
          <span>{{ currentFlight.seatsLeft }}</span>
        </div>
      </div>
    </div>

    <div v-if="currentFlight" class="seat-selection">
      <div class="seat-filter">
        <el-radio-group v-model="seatType" @change="loadSeats">
          <el-radio-button label="ECONOMY">经济舱</el-radio-button>
          <el-radio-button label="BUSINESS">商务舱</el-radio-button>
          <el-radio-button label="FIRST">头等舱</el-radio-button>
        </el-radio-group>
      </div>

      <div class="seat-map" v-loading="seatsLoading">
        <div class="airplane">
          <div class="cabin-header">
            <div class="row-label"></div>
            <div v-for="col in columns" :key="col" class="column-label">{{ col }}</div>
          </div>
          <div v-for="row in rows" :key="row" class="seat-row">
            <div class="row-label">{{ row }}</div>
            <div 
              v-for="col in columns" 
              :key="`${row}${col}`" 
              class="seat"
              :class="{ 'booked': isSeatBooked(row, col) }"
              @click="toggleSeat(row, col)">
              {{ row }}{{ col }}
            </div>
          </div>
        </div>
      </div>

      <div class="seat-legend">
        <div class="legend-item">
          <div class="seat-sample available"></div>
          <span>可选</span>
        </div>
        <div class="legend-item">
          <div class="seat-sample booked"></div>
          <span>已预订</span>
        </div>
      </div>
    </div>

    <div v-if="!currentFlight && !loading" class="no-flight">
      <el-empty description="请先选择一个航班"></el-empty>
    </div>

    <!-- 座位操作对话框 -->
    <el-dialog :title="selectedSeat ? '座位操作' : ''" :visible.sync="seatDialogVisible" width="400px">
      <div v-if="selectedSeat" class="seat-operation">
        <p>座位号: {{ selectedSeat }}</p>
        <p>状态: {{ isSeatBooked(selectedSeat.charAt(0), selectedSeat.charAt(1)) ? '已预订' : '可选' }}</p>
        
        <div class="operation-buttons">
          <el-button 
            v-if="!isSeatBooked(selectedSeat.charAt(0), selectedSeat.charAt(1))" 
            type="primary" 
            @click="bookSeat"
            :loading="operationLoading">
            标记为已预订
          </el-button>
          <el-button 
            v-else 
            type="danger" 
            @click="unbookSeat"
            :loading="operationLoading">
            取消预订
          </el-button>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'AdminSeats',
  data() {
    return {
      loading: false,
      searchForm: {
        flightNumber: ''
      },
      currentFlight: null,
      seatType: 'ECONOMY',
      seats: [],
      seatsLoading: false,
      columns: ['A', 'B', 'C', 'D', 'E', 'F'],
      rows: Array.from({ length: 30 }, (_, i) => i + 1),
      selectedSeat: null,
      seatDialogVisible: false,
      operationLoading: false
    };
  },
  methods: {
    // 查询航班
    searchSeats() {
      if (!this.searchForm.flightNumber) {
        this.$message.warning('请输入航班号');
        return;
      }
      
      this.loading = true;
      this.$axios.get(`/admin/flights/${this.searchForm.flightNumber}`)
        .then(response => {
          this.loading = false;
          if (response.data.code === 200) {
            this.currentFlight = response.data.data;
            this.loadSeats();
          } else {
            this.$message.error(response.data.message || '查询航班失败');
          }
        })
        .catch(error => {
          this.loading = false;
          if (error.response && error.response.status === 404) {
            this.$message.error('未找到该航班');
          } else {
            this.$message.error('查询航班失败，请稍后重试');
          }
          console.error(error);
        });
    },
    
    // 加载座位信息
    loadSeats() {
      if (!this.currentFlight) return;
      
      this.seatsLoading = true;
      this.$axios.get(`/flights/${this.currentFlight.id}/seats`, { params: { type: this.seatType } })
        .then(response => {
          this.seatsLoading = false;
          if (response.data.code === 200) {
            this.seats = response.data.data;
          } else {
            this.$message.error(response.data.message || '获取座位信息失败');
          }
        })
        .catch(error => {
          this.seatsLoading = false;
          this.$message.error('获取座位信息失败，请稍后重试');
          console.error(error);
        });
    },
    
    // 检查座位是否已预订
    isSeatBooked(row, col) {
      const seatNumber = `${row}${col}`;
      return this.seats.some(seat => seat.seatNumber === seatNumber && seat.isBooked);
    },
    
    // 点击座位
    toggleSeat(row, col) {
      this.selectedSeat = `${row}${col}`;
      this.seatDialogVisible = true;
    },
    
    // 标记座位为已预订
    bookSeat() {
      this.operationLoading = true;
      this.$axios.post('/admin/seats', {
        flightId: this.currentFlight.id,
        seatNumber: this.selectedSeat,
        seatType: this.seatType
      })
        .then(response => {
          this.operationLoading = false;
          if (response.data.code === 200) {
            this.$message.success('座位已标记为已预订');
            this.seatDialogVisible = false;
            this.loadSeats(); // 重新加载座位信息
          } else {
            this.$message.error(response.data.message || '操作失败');
          }
        })
        .catch(error => {
          this.operationLoading = false;
          if (error.response && error.response.status === 409) {
            this.$message.error('座位已被预订，请刷新座位信息');
          } else {
            this.$message.error('操作失败，请稍后重试');
          }
          console.error(error);
        });
    },
    
    // 取消座位预订
    unbookSeat() {
      this.operationLoading = true;
      this.$axios.delete(`/admin/seats/${this.currentFlight.id}/${this.selectedSeat}`)
        .then(response => {
          this.operationLoading = false;
          if (response.data.code === 200) {
            this.$message.success('座位已取消预订');
            this.seatDialogVisible = false;
            this.loadSeats(); // 重新加载座位信息
          } else {
            this.$message.error(response.data.message || '操作失败');
          }
        })
        .catch(error => {
          this.operationLoading = false;
          this.$message.error('操作失败，请稍后重试');
          console.error(error);
        });
    }
  }
};
</script>

<style scoped>
.seats-container {
  padding: 20px;
}
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
.header h2 {
  margin: 0;
}
.flight-info {
  margin-bottom: 30px;
}
.info-card {
  background-color: #f5f7fa;
  padding: 15px;
  border-radius: 4px;
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 15px;
}
.info-item {
  display: flex;
}
.info-item .label {
  width: 80px;
  color: #606266;
}
.seat-selection {
  margin-top: 20px;
}
.seat-filter {
  margin-bottom: 20px;
}
.seat-map {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}
.airplane {
  display: inline-block;
  background-color: #f5f7fa;
  padding: 20px;
  border-radius: 8px;
}
.cabin-header, .seat-row {
  display: flex;
  margin-bottom: 10px;
}
.row-label {
  width: 30px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
}
.column-label {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
}
.seat {
  width: 40px;
  height: 40px;
  margin: 0 5px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #67c23a;
  color: white;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s;
}
.seat:hover {
  transform: scale(1.05);
}
.seat.booked {
  background-color: #f56c6c;
}
.seat-legend {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}
.legend-item {
  display: flex;
  align-items: center;
  margin: 0 15px;
}
.seat-sample {
  width: 20px;
  height: 20px;
  margin-right: 5px;
  border-radius: 3px;
}
.seat-sample.available {
  background-color: #67c23a;
}
.seat-sample.booked {
  background-color: #f56c6c;
}
.no-flight {
  margin-top: 50px;
}
.seat-operation {
  text-align: center;
}
.operation-buttons {
  margin-top: 20px;
}
</style>