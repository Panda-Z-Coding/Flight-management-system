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
    <el-form v-show="showDeleteButton" :inline="true" :model="formInline" class="user-search">
      <el-form-item>
        <el-button size="small" type="primary" icon="el-icon-close" @click="closeDeleteButton()">取消</el-button>
      </el-form-item>
      <el-form-item>
        <el-button size="small" type="danger" icon="el-icon-delete" style="margin-left: 1000px"
                   @click="deleteList(ids)">删除
        </el-button>
      </el-form-item>
    </el-form>
    <!--列表-->
    <el-table size="small" @selection-change="selectChange" :data="userData" highlight-current-row v-loading="loading" border element-loading-text="拼命加载中" style="width: 100%;">
      <el-table-column align="center" type="selection" width="50">
      </el-table-column>
      <el-table-column align="center" sortable prop="username" label="用户名" width="120">
      </el-table-column>
      <el-table-column align="center" sortable prop="balance" label="余额" width="150">
      </el-table-column>
      <el-table-column align="center" sortable prop="permission" label="权限" width="100">
        <template slot-scope="scope">
          {{ scope.row.permission === 0 ? '用户' : '管理员' }}
        </template>
      </el-table-column>
      <el-table-column align="center" sortable prop="createTime" label="创建时间" width="180">
      </el-table-column>
      <el-table-column align="center" sortable prop="updateTime" label="修改时间" width="180">
      </el-table-column>
      <el-table-column align="center" sortable prop="status" label="状态" min-width="100">
        <template slot-scope="scope">
          <el-switch v-model="scope.row.status" :active-value="0" :inactive-value="1" active-color="#13ce66" inactive-color="#ff4949" @change="editStatus(scope.row)">
          </el-switch>
        </template>
      </el-table-column>
      <el-table-column label="操作" min-width="300">
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
        <el-form-item label="余额" prop="balance">
          <el-input-number size="small" v-model="editUserForm.balance" :precision="2" :step="100" :min="0" auto-complete="off" placeholder="请输入余额"></el-input-number>
        </el-form-item>
        <el-form-item label="权限" prop="permission">
          <el-select size="small" v-model="editUserForm.permission" placeholder="请选择权限" class="userRole">
            <el-option label="用户" :value="0"></el-option>
            <el-option label="管理员" :value="1"></el-option>
          </el-select>
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
        permission: [
          {required: true, message: '请选择权限', trigger: 'blur'}
        ],
        status: [
          {required: true, message: '请选择账号状态', trigger: 'blur'}
        ]
      },
      editUserForm: {
        id:'',
        username:'',
        balance: 0,
        permission: 0,
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
    selectChange(val) {
      this.ids = []
      val.forEach((item, index) => {
        this.ids.push(item.id)
      })
      if (this.ids.length > 0) {
        this.showDeleteButton = true
      } else {
        this.showDeleteButton = false
      }
    },
    deleteList(ids) {
      this.$confirm('确定要删除吗?', '信息', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        const params = {
          id: ids.join(',')
        };
        this.$axios.delete("/admin/user", {params}).then(res => {
          if (res.data.code === 200) {
            this.pageNum = 1
            this.pageSize = 10
            this.queryAll()
            this.$message.success("删除成功")
          } else {
            this.$message.error(res.data.data)
          }
        })
      })
        .catch(() => {
          this.$message({
            type: 'info',
            message: '已取消删除！'
          })
        })
    },
    closeDeleteButton() {
      this.ids = []
      this.showDeleteButton = false
      this.queryAll()

    },
    submitForm(formName) {
      this.$axios.put("/user/updateUser", this.editUserForm).then(res => {
        if (res.data.code === 200) {
          this.editFormVisible = false
          this.queryAll()
          this.$message.success("修改成功")
        } else {
          this.$message.error(res.data.data)
        }
      })
    },
    handleEdit(user) {
      this.editFormVisible = true
      this.editUserForm = {...user}
    },
    deleteUser(id) {
      this.$confirm('确定要删除吗?', '信息', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        // 删除
        this.$axios.delete("/user/delete?id=" + id).then(res => {
          if (res.data.code === 200) {
            this.pageNum = 1
            this.pageSize = 10
            this.queryAll()
            this.$message.success("删除成功")
          } else {
            this.$message.error(res.data.data)
          }
        })
      })
        .catch(() => {
          this.$message({
            type: 'info',
            message: '已取消删除！'
          })
        })
    },
    resetpwd(id) {
      this.$confirm('确定要重置吗?', '信息', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$axios.put("/user/updatePassword?id=" + id).then(res => {
          if (res.data.code === 200) {
            this.queryAll()
            this.$message.success("重置成功")
          } else {
            this.$message.error(res.data.data)
          }
        })
      })
        .catch(() => {
          this.$message({
            type: 'info',
            message: '已取消重置！'
          })
        })
    },
    closeDialog() {
      this.editFormVisible = false
    },
    editStatus(row) {
      this.editForm.id = row.id
      this.editForm.status = row.status
      this.$axios.put('/user/updateUserStatus', this.editForm).then(res => {
        if (res.data.code === 200) {
          this.$message.success("修改成功")
          this.queryAll()
        } else {
          this.$message.error(res.data.data)
        }
      })
    },
    handleSizeChange(val) {
      this.pageSize = val
      this.queryAll();
    },
    handleCurrentChange(val) {
      this.pageNum = val
      this.queryAll()
    },
    queryByName() {
      this.pageNum = 1
      this.queryAll()
    },
    queryAll() {
      this.$axios.get('/admin/user/page', {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          username: this.formInline.username,
          status: this.formInline.status
        }
      }).then(res => {
        if (res.data.code === 1) {
          this.userData = res.data.data.records
          this.pageNum = res.data.data.current
          this.total = res.data.data.total
        } else {
          this.$message.error(res.data.message || '获取用户列表失败')
        }
      }).catch(error => {
        console.error('获取用户列表失败:', error)
        this.$message.error('获取用户列表失败，请稍后重试')
      })
    }
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
