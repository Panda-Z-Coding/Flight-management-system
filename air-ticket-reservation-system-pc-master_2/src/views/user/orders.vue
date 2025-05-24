<template>
  <div class="orders-container">
    <div class="header">
      <h2>我的订单</h2>
      <el-select v-model="filterStatus" placeholder="订单状态" @change="getOrders">
        <el-option label="全部" value=""></el-option>
        <el-option label="未支付" :value="0"></el-option>
        <el-option label="已支付" :value="1"></el-option>
        <el-option label="已退款" :value="2"></el-option>
      </el-select>
    </div>

    <el-table
      v-loading="loading"
      :data="orderList"
      border
      style="width: 100%">
      <el-table-column
        prop="orderNumber"
        label="订单号"
        width="180">
      </el-table-column>
      <el-table-column
        prop="flightNumber"
        label="航班号"
        width="120">
      </el-table-column>
      <el-table-column
        prop="passengerName"
        label="乘客姓名"
        width="120">
      </el-table-column>
      <el-table-column
        prop="passengerIdCard"
        label="身份证号"
        width="180">
      </el-table-column>
      <el-table-column
        prop="seatNumber"
        label="座位号"
        width="120">
      </el-table-column>
      <el-table-column
        prop="price"
        label="票价(元)"
        width="100">
        <template slot-scope="scope">
          {{ scope.row.price.toFixed(2) }}
        </template>
      </el-table-column>
      <el-table-column
        prop="serviceFee"
        label="手续费(元)"
        width="100">
        <template slot-scope="scope">
          {{ scope.row.serviceFee.toFixed(2) }}
        </template>
      </el-table-column>
      <el-table-column
        prop="paymentMethod"
        label="支付方式"
        width="120">
      </el-table-column>
      <el-table-column
        label="状态"
        width="100">
        <template slot-scope="scope">
          <el-tag :type="getStatusType(scope.row.status)">
            {{ getStatusText(scope.row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column
        prop="createTime"
        label="创建时间"
        width="180">
        <template slot-scope="scope">
          {{ formatDateTime(scope.row.createTime) }}
        </template>
      </el-table-column>
      <el-table-column
        prop="updateTime"
        label="修改时间"
        width="180">
        <template slot-scope="scope">
          {{ formatDateTime(scope.row.updateTime) }}
        </template>
      </el-table-column>
      <el-table-column
        label="操作"
        width="200">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="primary"
            @click="showOrderDetail(scope.row)"
            plain>详情</el-button>
          <el-button
            v-if="scope.row.status === 0"
            size="mini"
            type="success"
            @click="showPayDialog(scope.row)">支付</el-button>
            <el-button
              v-if="scope.row.status === 1 && !isFlightDeparted(scope.row)"
              size="mini"
              type="danger"
              @click="showRefundDialog(scope.row)">退票</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页区域 -->
    <div class="pagination-container">
      <el-pagination
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="currentPage"
        :page-sizes="[10, 20, 30, 50]"
        :page-size="currentPageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total">
      </el-pagination>
    </div>

    <!-- 订单详情对话框 -->
    <el-dialog title="订单详情" :visible.sync="detailDialogVisible" width="600px">
      <div v-if="selectedOrder" class="order-detail">
        <div class="detail-item">
          <span class="label">订单号:</span>
          <span>{{ selectedOrder.orderNumber }}</span>
        </div>
        <div class="detail-item">
          <span class="label">乘客姓名:</span>
          <span>{{ selectedOrder.passengerName }}</span>
        </div>
        <div class="detail-item">
          <span class="label">身份证号:</span>
          <span>{{ selectedOrder.passengerIdCard }}</span>
        </div>
        <div class="detail-item">
          <span class="label">座位号:</span>
          <span>{{ selectedOrder.seatNumber }}</span>
        </div>
        <div class="detail-item">
          <span class="label">票价:</span>
          <span>{{ selectedOrder.price.toFixed(2) }} 元</span>
        </div>
        <div class="detail-item">
          <span class="label">手续费:</span>
          <span>{{ selectedOrder.serviceFee.toFixed(2) }} 元</span>
        </div>
        <div class="detail-item">
          <span class="label">支付方式:</span>
          <span>{{ selectedOrder.paymentMethod || '未支付' }}</span>
        </div>
        <div class="detail-item">
          <span class="label">订单状态:</span>
          <span>{{ getStatusText(selectedOrder.status) }}</span>
        </div>
        <div class="detail-item">
          <span class="label">创建时间:</span>
          <span>{{ selectedOrder.createTime }}</span>
        </div>
        <div class="detail-item" v-if="selectedOrder.updateTime">
          <span class="label">更新时间:</span>
          <span>{{ selectedOrder.updateTime }}</span>
        </div>
      </div>
    </el-dialog>

    <!-- 支付对话框 -->
    <el-dialog title="订单支付" :visible.sync="payDialogVisible" width="500px">
      <div v-if="selectedOrder" class="pay-dialog">
        <div class="pay-info">
          <p>订单号: {{ selectedOrder.orderNumber }}</p>
          <p>航班: {{ selectedOrder.flightNumber }}</p>
          <p>票价: {{ selectedOrder.price.toFixed(2) }} 元</p>
          <p>手续费: {{ selectedOrder.serviceFee.toFixed(2) }} 元</p>
          <p class="price">应付金额: <span>{{ selectedOrder.price + selectedOrder.serviceFee }}</span> 元</p>
        </div>
        <el-divider></el-divider>
        <el-form :model="payForm" ref="payForm" :rules="payRules" label-width="100px">
          <el-form-item label="支付方式" prop="paymentMethod">
            <el-select v-model="payForm.paymentMethod" placeholder="请选择支付方式">
              <el-option label="信用卡" value="BALANCE"></el-option>
              <el-option label="支付宝" value="ALIPAY"></el-option>
              <el-option label="微信支付" value="WECHAT"></el-option>
            </el-select>
          </el-form-item>
        </el-form>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="payDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="payOrder" :loading="submitting">确认支付</el-button>
      </div>
    </el-dialog>

    <!-- 退票对话框 -->
    <el-dialog title="申请退票" :visible.sync="refundDialogVisible" width="500px" :close-on-click-modal="false">
      <div v-if="selectedOrder && refundInfo" class="refund-dialog">
        <div class="refund-info">
          <p>订单号: {{ refundInfo.orderNumber }}</p>
          <p>航班: {{ refundInfo.flightNumber }}</p>
          <p>票价: {{ refundInfo.price.toFixed(2) }} 元</p>
          <p>手续费: {{ refundInfo.serviceFee.toFixed(2) }} 元</p>
          <p class="refund-amount">退款金额: {{ (refundInfo.price - refundInfo.serviceFee).toFixed(2) }} 元</p>
          <p class="warning">注意: 退票手续费根据距离起飞时间计算，请确认退款金额。</p>
        </div>
        <el-divider></el-divider>
        <el-form :model="refundForm" ref="refundForm" :rules="refundRules" label-width="100px">
          <el-form-item label="退票原因" prop="reason">
            <el-input type="textarea" v-model="refundForm.reason" placeholder="请输入退票原因"></el-input>
          </el-form-item>
        </el-form>
      </div>
      <div v-else-if="preparingRefund" class="loading-container">
        <el-spinner></el-spinner>
        <p>正在计算退款信息...</p>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="refundDialogVisible = false">取消</el-button>
        <el-button type="danger" @click="refundOrder" :loading="submitting" :disabled="preparingRefund">确认退票</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'UserOrders',
  data() {
    return {
      loading: false,
      orderList: [],
      filterStatus: '', // 筛选状态
      detailDialogVisible: false,
      payDialogVisible: false,
      refundDialogVisible: false,
      selectedOrder: null,
      submitting: false,
      preparingRefund: false,
      refundInfo: null,
      payForm: {
        paymentMethod: 'BALANCE'
      },
      payRules: {
        paymentMethod: [
          { required: true, message: '请选择支付方式', trigger: 'change' }
        ]
      },
      refundForm: {
        reason: ''
      },
      refundRules: {
        reason: [
          { required: true, message: '请输入退票原因', trigger: 'blur' },
          { min: 2, max: 100, message: '长度在 2 到 100 个字符', trigger: 'blur' }
        ]
      }
    };
  },
  methods: {
    // 获取订单列表
    async getOrders() {
      this.loading = true;

      try {
        // 从会话存储中获取用户信息
        const userInfo = JSON.parse(sessionStorage.getItem('user')) || {};
        const username = userInfo.username;

        if(!username){
          this.$message.warning('请先登录');
          this.loading = false;
          return;
        }
        const params = {
          username:username,
          page: this.currentPage || 1,
          pageSize: this.pageSize || 10
        };

        // 添加状态筛选条件
        if (this.filterStatus !== '') {
          params.status = this.filterStatus;
          filterStatus;
        }

        // 添加请求超时处理
        const response = await this.$axios.post('/user/order/page', params)

        if(response.data.code === 1){
          this.orderList = response.data.data.list; 
          this.total = response.data.data.total;
          
          // 如果没有数据，显示提示信息
          if (this.orderList.length === 0) {
            this.$message.info('暂无订单数据');
          }
        } else {
          this.$message.warning(response.data.message || '获取订单列表失败');
        }
      } catch (error) { 
        console.error('获取订单列表错误:', error);
        // 区分网络错误和其他错误
        if (error.message && error.message.includes('timeout')) {
          this.$message.error('请求超时，请检查网络连接');
        } else if (error.response) {
          this.$message.error(`获取订单列表失败: ${error.response.data.message || '服务器错误'}`);
        } else {
          this.$message.error('获取订单列表失败，请稍后重试');
        }
      } finally {
        this.loading = false;
      }
    },
    
    // 获取状态文本
    getStatusText(status) {
      const statusMap = {
        0: '未支付',
        1: '已支付',
        2: '已退款'
      };
      return statusMap[status] || '未知状态';
    },
    
    // 获取状态类型（用于el-tag的type属性）
    getStatusType(status) {
      const typeMap = {
        0: 'warning',
        1: 'success',
        2: 'info'
      };
      return typeMap[status] || '';
    },
    
    // 获取座位类型文本
    getSeatTypeText(type) {
      const typeMap = {
        'ECONOMY': '经济舱',
        'BUSINESS': '商务舱',
        'FIRST': '头等舱'
      };
      return typeMap[type] || '未知';
    },
    
    // 显示订单详情
    showOrderDetail(order) {
      this.selectedOrder = order;
      this.detailDialogVisible = true;
    },
    
    // 显示支付对话框
    showPayDialog(order) {
      this.selectedOrder = order;
      this.payForm.paymentMethod = 'BALANCE';
      this.payDialogVisible = true;
    },
    
    // 检查航班是否已起飞
    isFlightDeparted(order) {
      // 这里需要根据您的实际数据结构来判断
      // 假设有一个departureTime字段和当前时间比较
      if (!order.departureTime) return false;
      return new Date(order.departureTime) < new Date();
    },

    // 显示退票对话框
    async showRefundDialog(order) {
      
      try {
        // 先检查航班状态
        if (this.isFlightDeparted(order)) {
          this.$message.error('该航班已启程，无法退款');
          return;
        }
        this.selectedOrder = order;
        this.refundForm.reason = '';
        
        // 先调用准备退款接口获取退款信息
        this.preparingRefund = true;
        this.refundInfo = null; // 重置退款信息
      
        // 确保所有必要的参数都转换为字符串
        const pendingData = {
          orderId: String(order.id),
          flightId: order.flightId !== undefined ? String(order.flightId) : null,
          seatNumber: order.seatNumber
        };
        
        // 添加请求头
        const headers = {
          'Content-Type': 'application/json'
        };
        
        const response = await this.$axios.put('/user/order/pending', pendingData, { headers });
        
        // 检查响应状态
        if (response.data.code === 1) {
          this.refundInfo = response.data.data;
          this.refundDialogVisible = true;
        } else {
          throw new Error(response.data.message || '准备退款失败');
        }
      } catch(error) {
        console.error('准备退款失败:', error);
        this.$message.error(error.message || '准备退款失败，请稍后重试');
        this.refundDialogVisible = false; // 关闭对话框
      } finally {
        this.preparingRefund = false;
      }
    },
    
    // 退票
    async refundOrder() {
      if (!this.$refs.refundForm) {
        this.$message.error('表单验证失败');
        return;
      }

      try {
        const valid = await this.$refs.refundForm.validate();
        if (!valid) return;

        // 检查必要数据
        if (!this.selectedOrder || !this.refundInfo) {
          this.$message.error('订单或退款信息不存在');
          return;
        }

        this.submitting = true;

        // 确保所有参数都是字符串类型
        const confirmData = {
          orderNumber: String(this.refundInfo.orderNumber),
          serviceFee: String(this.refundInfo.serviceFee),
          updateTime: this.refundInfo.updateTime
        };

        const headers = {
          'Content-Type': 'application/json'
        };

        // 发送确认退票请求
        const response = await this.$axios.put('/user/order/confirmed', confirmData, { headers });

        if (response.data && response.data.code === 1) {
          const refundAmount = this.refundInfo.price - this.refundInfo.serviceFee;
          this.$message.success(`退票成功，退款金额: ${refundAmount.toFixed(2)} 元`);
          this.refundDialogVisible = false;
          await this.getOrders(); // 刷新列表
        } else {
          throw new Error(response.data.message || '退票失败');
        }
      } catch(error) {
        console.error('退票失败:', error);
        this.$message.error(error.message || '退票失败，请稍后重试');
      } finally {
        this.submitting = false;
      }
    },
    
    // 支付订单
    payOrder() {
      this.$refs.payForm.validate(valid => {
        if (valid) {
          this.submitting = true;
          
          // 构建符合新接口的请求参数
          const paymentData = {
            orderNumber: this.selectedOrder.orderNumber,
            paymentMethod: this.payForm.paymentMethod
          };
          
          this.$axios.put('/user/order/payment', paymentData)
            .then(response => {
              this.submitting = false;
              if (response.data.code === 1) {
                this.$message.success('支付成功');
                this.payDialogVisible = false;
                this.getOrders(); // 刷新列表
              } else {
                this.$message.error(response.data.msg || '支付失败');
              }
            })
            .catch(error => {
              this.submitting = false;
              if (error.response && error.response.status === 402) {
                this.$message.error('余额不足，请选择其他支付方式或充值');
              } else if (error.response && error.response.data) {
                this.$message.error(error.response.data.msg || '支付失败');
              } else {
                this.$message.error('支付失败，请稍后重试');
              }
              console.error(error);
            });
        }
      });
    },
    
    // 格式化日期时间
    formatDateTime(dateTimeStr) {
      if (!dateTimeStr) return '';
      const date = new Date(dateTimeStr);
      return date.toLocaleString();
    },
    
    // 处理页码变化
    handleCurrentChange(page) {
      this.currentPage = page;
      this.getOrders();
    },
    
    // 处理每页条数变化
    handleSizeChange(size) {
      this.pageSize = size;
      this.currentPage = 1;
      this.getOrders();
    },

    // 处理状态筛选变化
    handleStatusChange() {
      this.currentPage = 1; // 重置为第一页
      this.getOrders();
    }
  },
  mounted() {
    // 检查是否有从航班预订页面传来的订单ID
    const orderId = this.$route.query.orderId;
    if (orderId) {
      // 如果有，获取订单详情并显示支付对话框
      this.$axios.get(`/user/orders/${orderId}`)
        .then(response => {
          if (response.data.code === 200) {
            this.selectedOrder = response.data.data;
            this.showPayDialog(this.selectedOrder);
          }
        });
    }
    
    this.getOrders();
  }
};
</script>

<style scoped>
.orders-container {
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
.order-detail {
  padding: 10px;
}
.detail-item {
  margin-bottom: 10px;
  display: flex;
}
.detail-item .label {
  width: 100px;
  color: #606266;
}
.pay-info, .refund-info {
  padding: 10px;
  background-color: #f5f7fa;
  border-radius: 4px;
}
.pay-info .price {
  font-weight: bold;
}
.pay-info .price span {
  color: #f56c6c;
  font-size: 18px;
}
.refund-info .warning {
  color: #e6a23c;
  font-size: 13px;
}
</style>