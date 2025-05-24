<template>
  <div class="user">
    <el-breadcrumb separator-class="el-icon-arrow-right">
      <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item>用户管理</el-breadcrumb-item>
    </el-breadcrumb>

    <!--条件查询-->
    <el-form :inline="true" :model="formInline" class="user-search" v-show="!showDeleteButton">
      <el-form-item label="搜索：">
      </el-form-item>
      <el-form-item label="">
        <el-input size="small" v-model="formInline.username" placeholder="输入用户名"></el-input>
      </el-form-item>
      <el-form-item>
        <el-button size="small" type="primary" icon="el-icon-search" @click="queryByName()">搜索</el-button>
      </el-form-item>
      <el-form-item>
        <el-button size="small" type="primary" icon="el-icon-circle-plus" @click="exportData()">导出数据</el-button>
      </el-form-item>
    </el-form>
    <!-- 删除这个批量删除的表单区域 -->
    <!-- <el-form v-show="showDeleteButton" :inline="true" :model="formInline" class="user-search"> -->
    <!--   <el-form-item> -->
    <!--     <el-button size="small" type="primary" icon="el-icon-close" @click="closeDeleteButton()">取消</el-button> -->
    <!--   </el-form-item> -->
    <!--   <el-form-item> -->
    <!--     <el-button size="small" type="danger" icon="el-icon-delete" style="margin-left: 1000px" -->
    <!--                @click="deleteList(ids)">删除 -->
    <!--     </el-button> -->
    <!--   </el-form-item> -->
    <!-- </el-form> -->
    <!--列表-->
    <el-table size="small" :data="userData" highlight-current-row v-loading="loading" border element-loading-text="拼命加载中" style="width: 100%;">
      <el-table-column align="center" sortable prop="id" label="用户ID" width="120">
      </el-table-column>
      <el-table-column align="center" sortable prop="username" label="用户名" width="120">
      </el-table-column>
      <el-table-column align="center" sortable prop="balance" label="余额" width="150">
      </el-table-column>
      <el-table-column align="center" sortable prop="createTime" label="创建时间" width="180">
      </el-table-column>
      <el-table-column align="center" sortable prop="updateTime" label="修改时间" width="180">
      </el-table-column>
      <el-table-column align="center" sortable prop="status" label="状态" min-width="100">
        <template slot-scope="scope">
          <el-tag :type="scope.row.status === 0 ? 'success' : 'danger'">{{ scope.row.status === 0 ? '正常' : '封号' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column align="center"label="操作" min-width="100">
        <template slot-scope="scope">
          <el-button size="mini" @click="handleEdit(scope.row)">编辑</el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 编辑界面 -->
    <el-dialog :title="title" :visible.sync="editFormVisible" width="30%" @click='closeDialog("edit")'>
      <el-form label-width="80px" ref="editUserForm" :model="editUserForm" :rules="rules">
        <el-form-item label="用户名" prop="username">
          <el-input size="small" v-model="editUserForm.username" auto-complete="off" placeholder="请输入用户名"></el-input>
        </el-form-item>
        <el-form-item label="用户状态" prop="status">
          <el-select size="small" v-model="editUserForm.status" placeholder="请选择" class="userRole">
            <el-option label="正常" :value="0"></el-option>
            <el-option label="封号" :value="1"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button size="small" @click='closeDialog("edit")'>取消</el-button>
        <el-button size="small" type="primary" :loading="loading" class="title" @click="submitForm('editUserForm')">保存</el-button>
      </div>
    </el-dialog>
    <!--    分页条-->
    <div class="pagination">
      <el-pagination
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="pageNum"
        :page-sizes="[10, 15, 20, 25]"
        :page-size="pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total">
      </el-pagination>
    </div>
  </div>
</template>

<script>
import FileSaver from 'file-saver';
export default {
  name: "user",
  data(){
    /* 定义初始化变量 */
    return{
      showDeleteButton: false,
      pageNum: 1,
      pageSize: 10,
      total: 0,
      ids: [],
      userData: [],
      nshow: true, //switch开启
      fshow: false, //switch关闭
      title: '用户修改',
      editFormVisible: false,
      editForm: {
        id:'',
        status: '',
      },
      rules: {
        username: [
          {required: true, message: '用户名不能为空', trigger: 'blur'}
        ],
        balance: [
          {required: true, message: '余额不能为空', trigger: 'blur'}
        ],
        status: [
          {required: true, message: '请选择账号状态', trigger: 'blur'}
        ]
      },
      editUserForm: {
        id:'',
        username:'',
        balance: 0,
        status: 0
      },
      formInline: {
        username:'',
        status:''
      }
    }
  },
  /* 定义事件函数 */
  methods:{
    exportData() {
      // 模拟要导出的数据
      const data = this.userData;
      // 将数据转换为CSV格式
      const csvData = this.convertToCSV(data);
      // 创建Blob对象
      const blob = new Blob([csvData], { type: 'text/csv;charset=utf-8' });
      // 使用FileSaver.js保存文件
      FileSaver.saveAs(blob, 'data.csv');
    },
    convertToCSV(data) {
      // 将数据转换为CSV格式的字符串
      const csvRows = [];
      const headers = Object.keys(data[0]);
      csvRows.push(headers.join(','));

      for (const row of data) {
        const values = headers.map(header => {
          const escaped = ('' + row[header]).replace(/"/g, '\\"');
          return `"${escaped}"`;
        });
        csvRows.push(values.join(','));
      }

      return csvRows.join('\n');
    },
    
    
    handleEdit(user) {
      this.editFormVisible = true
      this.editUserForm = {...user}
    },
    
    
    closeDialog() {
      this.editFormVisible = false
    },

    editStatus(row) {
      // 检查当前用户是否为管理员
      const role = sessionStorage.getItem('role');
      if (role !== '1') {
        this.$message.error('只有管理员才能修改用户状态');
        // 恢复原状态值
        this.$nextTick(() => {
          row.status = row.status === 0 ? 1 : 0;
        });
        return;
      }
      
      // 获取要设置的状态值（注意：接口中0表示正常，1表示封号）
      const statusToSet = row.status === 0 ? 1 : 0;
      
      // 调用新接口 - 修改为符合后端要求的格式
      this.$axios.post(`/admin/user/status/${statusToSet}`, {
        id: row.id
      }).then(res => {
        if (res.data.code === 1) {
          this.$message.success(statusToSet === 1 ? "用户已封号" : "用户已启用");
          this.queryAll();
        } else {
          // 操作失败，恢复原状态值
          row.status = row.status === 0 ? 1 : 0;
          this.$message.error(res.data.message || '操作失败');
        }
      }).catch(error => {
        // 发生错误，恢复原状态值
        row.status = row.status === 0 ? 1 : 0;
        console.error('修改用户状态失败:', error);
        this.$message.error('修改用户状态失败，请稍后重试');
      });
    },
    
    // 添加submitForm方法处理编辑页面保存按钮点击事件
    submitForm(formName) {
      this.$refs[formName].validate(valid => {
        if (valid) {
          this.loading = true;
          // 修复：添加status路径参数，并将id作为请求参数传递
          const status = this.editUserForm.status;
          const params = { id: this.editUserForm.id };
          
          this.$axios.post(`/admin/user/status/${status}`, null, { params }).then(res => {
            if (res.data.code === 1) {
              this.editFormVisible = false;
              this.queryAll();
              this.$message.success("修改成功");
            } else {
              this.$message.error(res.data.message || res.data.data || "修改失败");
            }
          }).catch(error => {
            console.error('修改用户状态失败:', error);
            this.$message.error('修改用户状态失败，请稍后重试');
          }).finally(() => {
            this.loading = false;
          });
        } else {
          this.$message.warning("请填写完整有效的表单信息");
          return false;
        }
      });
    },
    
    queryByName() {
      this.pageNum = 1
      this.queryAll()
    },
    async queryAll() {
      this.loading = true
      
      try{
        const params = {
          page: this.pageNum,
          pageSize: this.pageSize,
          username: this.formInline.username || undefined
        }

        const response = await this.$axios.post('/admin/user/page', params);

        if(response.data.code === 1){
          this.userData = response.data.data.list;
          this.total = response.data.data.total;
        } else {
          this.$message.warning(response.data.message || '获取用户列表失败');
        }
      }catch(error){
        console.error('获取乘客列表错误:', error);
        this.$message.error('获取乘客列表失败，请稍后重试');
      }finally {
        this.loading = false;
      };
      
    },

     // 处理页码变化
     handleCurrentChange(val) {
      this.pageNum = val;
      this.queryAll();
    },
    
    // 处理每页条数变化
    handleSizeChange(val) {
      this.pageSize = val;
      this.queryAll();
    },
    // 格式化日期时间
    formatDateTime(dateTimeStr) {
      if (!dateTimeStr) return ''
      
      // 处理ISO格式的日期时间字符串
      const date = new Date(dateTimeStr)
      if (isNaN(date.getTime())) return dateTimeStr
      
      const year = date.getFullYear()
      const month = String(date.getMonth() + 1).padStart(2, '0')
      const day = String(date.getDate()).padStart(2, '0')
      const hours = String(date.getHours()).padStart(2, '0')
      const minutes = String(date.getMinutes()).padStart(2, '0')
      const seconds = String(date.getSeconds()).padStart(2, '0')
      
      return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
    },
  },
  /* vue的生命周期函数
     当页面加载完毕就会执行created里面的函数
  */
  created() {
    this.queryAll()
  }
}
</script>

<!-- scoped此属性可以防止当前页面的样式不会影响其他页面样式 -->
<style scoped>

</style>
