<template>
  <div class="order-container">
    <!-- 搜索区域 -->
    <div class="search-box">
      <el-form :inline="true" :model="queryParams" class="demo-form-inline">
        <el-form-item>
          <el-input v-model="queryParams.orderNumber" placeholder="订单编号" clearable></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 表格区域 -->
    <el-table :data="orderList" border style="width: 100%" v-loading="loading">
      <el-table-column align = "center" prop="orderNumber" label="订单编号" width="180"></el-table-column>
      <el-table-column align = "center"prop="username" label="用户名" width="120"></el-table-column>
      <el-table-column align = "center" prop="flightNumber" label="航班号" width="120"></el-table-column>
      <el-table-column align = "center" prop="passengerName" label="乘客姓名" width="120"></el-table-column>
      <el-table-column align = "center" prop="passengerIdCard" label="身份证号" width="180"></el-table-column>
      <el-table-column align = "center" prop="seatNumber" label="座位号" width="120"></el-table-column>
      <el-table-column align = "center" prop="price" label="票价" width="120">
        <template slot-scope="scope">
          ¥{{ scope.row.price.toFixed(2) }}
        </template>
      </el-table-column>
      <el-table-column align = "center" prop="serviceFee" label="服务费" width="120">
        <template slot-scope="scope">
          ¥{{ scope.row.serviceFee.toFixed(2) }}
        </template>
      </el-table-column>
      <el-table-column align = "center" prop="paymentMethod" label="支付方式" width="120"></el-table-column>
      <el-table-column align = "center" prop="status" label="订单状态" width="120">
        <template slot-scope="scope">
          <el-tag :type="getStatusType(scope.row.status)">
            {{ getStatusText(scope.row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column align = "center" prop="createTime" label="创建时间" width="180">
        <template slot-scope="scope">
          {{ formatDateTime(scope.row.createTime) }}
        </template>
      </el-table-column>
      <el-table-column align = "center" prop="updateTime" label="修改时间" width="180">
        <template slot-scope="scope">
          {{ formatDateTime(scope.row.updateTime) }}
        </template>
      </el-table-column>
      
    </el-table>

    <!-- 分页区域 -->
    <div class="pagination-container">
      <el-pagination
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="queryParams.page"
        :page-sizes="[10, 20, 30, 50]"
        :page-size="queryParams.pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total">
      </el-pagination>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Order',
  data() {
    return {
      // 查询参数
      queryParams: {
        page: 1,
        pageSize: 10,
        orderNumber: ''
      },
      // 订单列表
      orderList: [],
      // 总条数
      total: 0,
      // 加载状态
      loading: false
    }
  },
  created() {
    this.getList()
  },
  methods: {
    // 获取订单列表
    async getList() {
      this.loading = true
      try {
        // 构建查询参数
        const params = {
          page: this.queryParams.page,
          pageSize: this.queryParams.pageSize
        }
        
        // 只有当orderNumber有值且不为空字符串时才添加到查询参数中
        if (this.queryParams.orderNumber && this.queryParams.orderNumber.trim()) {
          params.orderNumber = this.queryParams.orderNumber.trim()
          // 当按订单号查询时，设置pageSize为1，确保只返回一条记录
          params.pageSize = 1
        }

        const response = await this.$axios.post('/admin/order/page', params,{
          headers: {
            'Authorization': sessionStorage.getItem('token') ? `Bearer ${sessionStorage.getItem('token')}` : '',
            'Content-Type': 'application/json',
            'Accept': 'application/json'
          }
        })

        if (response.data.code === 1) {
          this.orderList = response.data.data.list
          this.total = response.data.data.total
          
          // 如果是按订单号查询
          if (params.orderNumber) {
            if (this.orderList.length === 0) {
              this.$message.warning('未找到该订单号对应的订单')
            } else {
              this.$message.success('查询成功')
            }
          } else {
            // 普通分页查询
            if (this.orderList.length === 0) {
              this.$message.info('暂无订单数据')
            }
          }
        } else {
          this.$message.error(response.data.message || '获取订单列表失败')
        }
      } catch (error) {
        console.error('获取订单列表失败:', error)
        this.$message.error('获取订单列表失败，请稍后重试')
      } finally {
        this.loading = false
      }
    },

    // 查询按钮点击事件
    handleQuery() {
      // 重置页码到第一页
      this.queryParams.page = 1
      // 去除订单编号的首尾空格
      const orderNumber = this.queryParams.orderNumber.trim()
      
      // 如果订单编号为空，提示用户
      if (!orderNumber) {
        this.$message.warning('请输入订单编号')
        return
      }
      
      // 更新查询参数并执行查询
      this.queryParams.orderNumber = orderNumber
      this.getList()
    },

    // 重置按钮点击事件
    resetQuery() {
      // 重置查询参数
      this.queryParams = {
        page: 1,
        pageSize: 10,
        orderNumber: ''
      }
      // 重新获取列表
      this.getList()
      // 提示用户
      this.$message.success('已重置查询条件')
    },

    // 每页条数改变
    handleSizeChange(val) {
      // 只有在非订单号查询时才允许修改每页条数
      if (!this.queryParams.orderNumber) {
        this.queryParams.pageSize = val
        this.queryParams.page = 1
        this.getList()
      }
    },

    // 当前页改变
    handleCurrentChange(val) {
      // 只有在非订单号查询时才允许切换页码
      if (!this.queryParams.orderNumber) {
        this.queryParams.page = val
        this.getList()
      }
    },

    // 查看订单详情
    handleDetail(row) {
      // TODO: 实现查看订单详情功能
      console.log('查看订单详情:', row)
    },

    // 取消订单
    async handleDelete(row) {
      try {
        await this.$confirm('确认取消该订单吗？', '提示', {
          type: 'warning'
        })
        // TODO: 调用取消订单接口
        this.$message.success('订单已取消')
        this.getList()
      } catch (error) {
        console.error('取消订单失败:', error)
      }
    },

    // 格式化日期时间
    formatDateTime(dateTimeStr) {
      if (!dateTimeStr) return ''
      const date = new Date(dateTimeStr)
      return date.toLocaleString()
    },

    // 获取订单状态文本
    getStatusText(status) {
      const statusMap = {
        0: '待支付',
        1: '已支付',
        2: '已取消'
      }
      return statusMap[status] || '未知状态'
    },

    // 获取订单状态标签类型
    getStatusType(status) {
      const typeMap = {
        0: 'warning',
        1: 'success',
        2: 'info'
      }
      return typeMap[status] || ''
    }
  }
}
</script>

<style scoped>
.order-container {
  padding: 20px;
}

.search-box {
  margin-bottom: 20px;
  padding: 20px;
  background-color: #fff;
  border-radius: 4px;
}

.pagination-container {
  margin-top: 20px;
  text-align: right;
}

.el-table {
  margin-top: 20px;
}

.el-tag {
  margin-right: 5px;
}
</style>
