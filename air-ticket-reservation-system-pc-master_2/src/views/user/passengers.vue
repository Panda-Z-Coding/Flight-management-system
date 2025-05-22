<template>
  <div class="passengers-container">
    <div class="header">
      <h2>乘客管理</h2>
      <div class="header-buttons">
        <el-button type="danger" @click="batchDelete" :disabled="selectedPassengers.length === 0">批量删除</el-button>
        <el-button type="primary" @click="showAddDialog">添加乘客</el-button>
      </div>
    </div>

    <el-table
      v-loading="loading"
      :data="passengerList"
      border
      @selection-change="handleSelectionChange"
      style="width: 100%">
      <el-table-column
        type="selection"
        width="55">
      </el-table-column>
      <el-table-column
        prop="name"
        label="姓名"
        width="120">
      </el-table-column>
      <el-table-column
        prop="idCard"
        label="身份证号"
        width="180">
      </el-table-column>
      <el-table-column
        prop="phoneNumber"
        label="电话号码"
        width="150">
      </el-table-column>
      <el-table-column
        prop="status"
        label="状态"
        width="100">
        <template slot-scope="scope">
          <el-tag :type="scope.row.status === 0 ? 'success' : 'info'">
            {{ scope.row.status === 0 ? '正常' : '其他' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column
        prop="createTime"
        label="创建时间"
        width="180">
      </el-table-column>
      <el-table-column
        prop="updateTime"
        label="更新时间"
        width="180">
      </el-table-column>
      <el-table-column
        label="操作"
        width="180">
        <template slot-scope="scope">
          <el-button
            size="mini"
            @click="editPassenger(scope.row)">编辑</el-button>
          <el-button
            size="mini"
            type="danger"
            @click="deletePassenger([scope.row.id])">删除</el-button>
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

    <!-- 添加/编辑乘客对话框 -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="500px">
      <el-form :model="passengerForm" :rules="passengerRules" ref="passengerForm" label-width="100px">
        <el-form-item label="姓名" prop="name">
          <el-input v-model="passengerForm.name" placeholder="请输入乘客姓名"></el-input>
        </el-form-item>
        <el-form-item label="身份证号" prop="idCard">
          <el-input v-model="passengerForm.idCard" placeholder="请输入身份证号"></el-input>
        </el-form-item>
        <el-form-item label="电话号码" prop="phoneNumber">
          <el-input v-model="passengerForm.phoneNumber" placeholder="请输入电话号码"></el-input>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="passengerForm.status" placeholder="请选择状态">
            <el-option :value="0" label="正常"></el-option>
            <el-option :value="1" label="其他"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitPassenger" :loading="submitting">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'UserPassengers',
  data() {
    // 身份证号验证规则
    const validateIdCard = (rule, value, callback) => {
      const reg = /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/;
      if (!reg.test(value)) {
        callback(new Error('请输入正确的身份证号码'));
      } else {
        callback();
      }
    };
    
    // 电话号码验证规则
    const validatePhone = (rule, value, callback) => {
      const reg = /^1[3-9]\d{9}$/;
      if (!reg.test(value)) {
        callback(new Error('请输入正确的手机号码'));
      } else {
        callback();
      }
    };
    
    return {
      loading: false,
      passengerList: [],
      selectedPassengers: [],
      dialogVisible: false,
      dialogTitle: '添加乘客',
      isEdit: false,
      submitting: false,
      currentPage: 1,
      currentPageSize: 10,
      total: 0,
      passengerForm: {
        id: null,
        name: '',
        idCard: '',
        phoneNumber: '',
        status: 0
      },
      passengerRules: {
        name: [
          { required: true, message: '请输入乘客姓名', trigger: 'blur' },
          { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
        ],
        idCard: [
          { required: true, message: '请输入身份证号', trigger: 'blur' },
          { validator: validateIdCard, trigger: 'blur' }
        ],
        phoneNumber: [
          { required: true, message: '请输入电话号码', trigger: 'blur' },
          { validator: validatePhone, trigger: 'blur' }
        ],
        status: [
          { required: true, message: '请选择乘客类型', trigger: 'change' }
        ]
      }
    };
  },
  methods: {
    // 表格选择项变化
    handleSelectionChange(val) {
      this.selectedPassengers = val;
    },
    
    // 批量删除
    batchDelete() {
      if (this.selectedPassengers.length === 0) {
        this.$message.warning('请至少选择一个乘客');
        return;
      }
      
      const ids = this.selectedPassengers.map(item => item.id);
      this.deletePassenger(ids);
    },
    
    // 获取乘客列表
    async getPassengers() {
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
          username: username,
          page: this.currentPage,
          pageSize: this.currentPageSize
        };

        const response = await this.$axios.post('/user/passenger/page', params);

        if(response.data.code === 1){
          this.passengerList = response.data.data.list;
          this.total = response.data.data.total;
        } else {
          this.$message.warning(response.data.message || '获取乘客列表失败');
        }
      } catch (error) { 
        console.error('获取乘客列表错误:', error);
        this.$message.error('获取乘客列表失败，请稍后重试');
      } finally {
        this.loading = false;
      }
    },
    
    // 处理页码变化
    handleCurrentChange(page) {
      this.currentPage = page;
      this.getPassengers();
    },
    
    // 处理每页条数变化
    handleSizeChange(size) {
      this.currentPageSize = size;
      this.currentPage = 1;
      this.getPassengers();
    },
    
    // 显示添加乘客对话框
    showAddDialog() {
      this.dialogVisible = false;
      
      this.$nextTick(() => {
        this.isEdit = false;
        this.dialogTitle = '添加乘客';

        // 重置表单数据
        this.passengerForm = {
          id: null,
          name: '',
          idCard: '',
          phoneNumber: '',
          status: 0
        };
      });
      
    // 重置表单验证
    if (this.$refs.passengerForm) {
      this.$refs.passengerForm.resetFields();
      this.$refs.passengerForm.clearValidate();
    }
      // 先打开对话框
      this.dialogVisible = true;
    },
    
    // 显示编辑乘客对话框
    editPassenger(passenger) {
      this.isEdit = true;
      this.dialogTitle = '编辑乘客';
      this.passengerForm = { ...passenger };
      this.dialogVisible = true;
    },
    
    // 提交乘客表单（添加或编辑）
    submitPassenger() {
      this.$refs.passengerForm.validate(valid => {
        if (valid) {
          this.submitting = true;
          
          // 获取用户信息并添加到表单中
          const userInfo = JSON.parse(sessionStorage.getItem('user')) || {};
          this.passengerForm.username = userInfo.username;
          
          // 根据isEdit判断是添加还是编辑
          const request = this.isEdit
            ? this.$axios.put('/user/passenger', this.passengerForm)
            : this.$axios.post('/user/passenger', this.passengerForm);
            
          request
            .then(response => {
              this.submitting = false;
              if (response.data.code === 1) {
                this.$message.success(this.isEdit ? '编辑成功' : '添加成功');
                this.dialogVisible = false;
                this.getPassengers(); // 刷新列表
              } else {
                this.$message.error(response.data.message || (this.isEdit ? '编辑失败' : '添加失败'));
              }
            })
            .catch(error => {
              this.submitting = false;
              if (error.response && error.response.data) {
                this.$message.error(error.response.data.message || (this.isEdit ? '编辑失败' : '添加失败'));
              } else {
                this.$message.error(this.isEdit ? '编辑失败，请稍后重试' : '添加失败，请稍后重试');
              }
              console.error(error);
            });
        }
      });
    },
    
    // 删除乘客
    deletePassenger(ids) {
      this.$confirm('确定要删除选中的乘客信息吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        // 构建参数，将ids数组转换为多个ids参数
        const params = new URLSearchParams();
        ids.forEach(id => {
          params.append('ids', id);
        });
        
        this.$axios.delete(`/user/passenger?${params.toString()}`)
          .then(response => {
            if (response.data.code === 1) {
              this.$message.success('删除成功');
              this.getPassengers(); // 刷新列表
              // 清空选择
              this.selectedPassengers = [];
            } else {
              this.$message.error(response.data.message || '删除失败');
            }
          })
          .catch(error => {
            if (error.response && error.response.data) {
              this.$message.error(error.response.data.message || '删除失败');
            } else {
              this.$message.error('删除失败，请稍后重试');
            }
            console.error(error);
          });
      }).catch(() => {
        // 取消删除操作
      });
    }
  },
  mounted() {
    this.getPassengers();
  }
};
</script>

<style scoped>
.passengers-container {
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
.header-buttons {
  display: flex;
  gap: 10px;
}
</style>