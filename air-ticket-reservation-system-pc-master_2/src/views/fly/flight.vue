<template>
  <div class="flight">
    <el-breadcrumb separator-class="el-icon-arrow-right">
      <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item>航班管理</el-breadcrumb-item>
    </el-breadcrumb>
    <!--条件查询-->
    <el-form :inline="true" :model="formInline" class="user-search">
      <el-form-item label="搜索：">
        <el-select size="small" v-model="formInline.departureCity" filterable clearable placeholder="请选择起飞城市">
          <el-option
            v-for="item in city"
            :key="item.id"
            :label="item.city"
            :value="item.city">
          </el-option>
        </el-select>

        <el-select size="small" v-model="formInline.arrivalCity" filterable clearable placeholder="请选择降落城市">
          <el-option
            v-for="item in city"
            :key="item.id"
            :label="item.city"
            :value="item.city">
          </el-option>
        </el-select>
        <el-form-item label="">
          <el-date-picker
            v-model="formInline.departureTime"
            type="datetime"
            value-format="yyyy-MM-dd HH:mm:ss"
            :picker-options="departureDateOptions"
            placeholder="选择日期">
          </el-date-picker>
        </el-form-item>
      </el-form-item>
      <el-form-item>
        <el-button size="small" type="primary" icon="el-icon-search" @click="search">搜索</el-button>
      </el-form-item>
      <el-row>
        <el-col :span="24">
      <el-form-item>
        <el-button size="small" type="primary" icon="el-icon-circle-plus" v-show="this.userKind !== '-2'" @click="addFlight() ">添加航班</el-button>
      </el-form-item>
      <el-form-item>
        <el-button size="small" type="primary" icon="el-icon-circle-plus" @click="exportData()">导出数据</el-button>
      </el-form-item>
    </el-col>
  </el-row>
    </el-form>

    <!-- 表格操作提示 -->
    <div style="margin-bottom: 15px;">
      <el-alert
        title="提示：您可以通过表格右侧的操作按钮对航班进行编辑和删除"
        type="info"
        show-icon
        :closable="false">
      </el-alert>
    </div>

    <!--列表-->
    <el-table 
      size="small" 
      :data="flightData" 
      highlight-current-row 
      v-loading="loading" 
      border 
      element-loading-text="拼命加载中" 
      style="width: 100%;"
      @selection-change="handleSelectionChange">
      <el-table-column
        type="selection"
        width="55">
      </el-table-column>
      <el-table-column align="center"  prop="departureCity" label="起飞城市" width="150">
      </el-table-column>
      <el-table-column align="center"  prop="arrivalCity" label="降落城市" width="150">
      </el-table-column>
      <el-table-column align="center"  prop="flightNumber" label="航班号" width="150">
      </el-table-column>
      <el-table-column align="center" sortable prop="departureTime" label="起飞时间" width="150">
      </el-table-column>
      <el-table-column align="center" sortable prop="arrivalTime" label="到达时间" width="150">
      </el-table-column>
      <el-table-column align="center" sortable prop="remainingSeats" label="剩余座位" width="150">
      </el-table-column>
      <el-table-column align="center" sortable prop="totalSeats" label="总座位数" width="150">
      </el-table-column>
      <el-table-column align="center" sortable prop="price" label="价格" width="150">
      </el-table-column>
      <el-table-column align="center" sortable prop="aircraftModel" label="飞机型号" width="150">
      </el-table-column>
      <el-table-column align="center"  prop="airline" label="航空公司" width="150">
      </el-table-column>
      <el-table-column align="center" sortable prop="createTime" label="创建时间" width="150">
      </el-table-column>
      <el-table-column align="center" sortable prop="updateTime" label="修改时间" width="150">
      </el-table-column>
      <el-table-column label="操作" min-width="180" align="center">
        <template slot-scope="scope">
          <el-button size="mini" type="primary" icon="el-icon-edit" v-show="userKind !== '-2'" @click="handleEdit(scope.row)">编辑</el-button>
          <el-button size="mini" type="danger" icon="el-icon-delete" v-show="userKind !== '-2'" @click="deleteFlight(scope.row.id || scope.row.flightId)" :disabled="scope.row.status === 1">删除</el-button>
          <el-tooltip v-if="scope.row.status === 1" content="正在飞行的航班不能删除" placement="top">
            <i class="el-icon-info" style="margin-left: 5px; color: #E6A23C;"></i>
          </el-tooltip>
        </template>
      </el-table-column>
    </el-table>
    <!-- 编辑界面 -->
          <el-dialog :title="title" :visible.sync="editFormVisible" width="50%" @click='closeDialog("edit")'>
      <el-form label-width="150px" ref="editFlightForm" :model="editFlightForm" :rules="rules">
        <el-form-item label="起飞城市" prop="departureCity">
          <el-select size="small" @change='queryByStartAirport(editFlightForm.departureCity)' v-model="editFlightForm.departureCity" filterable clearable placeholder="请选择起飞城市">
            <el-option
              v-for="item in city"
              :key="item.id"
              :label="item.city"
              :value="item.city">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="降落城市" prop="arrivalCity">
          <el-select size="small" v-model="editFlightForm.arrivalCity"  :disabled = disabled filterable clearable placeholder="请选择降落城市">
            <el-option
              v-for="item in startRoute"
              :key="item.id"
              :label="item.city"
              :value="item.city">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="航班号" prop="flightNumber">
          <el-input size="small" v-model="editFlightForm.flightNumber" placeholder="请输入航班号"></el-input>
        </el-form-item>
        <el-form-item label="飞机型号" prop="aircraftModel">
          <el-input size="small" v-model="editFlightForm.aircraftModel" placeholder="请输入飞机型号"></el-input>
        </el-form-item>
        <el-form-item label="航空公司" prop="airline">
          <el-input size="small" v-model="editFlightForm.airline" placeholder="请输入航空公司"></el-input>
        </el-form-item>
        <el-form-item label="总座位数" prop="totalSeats">
          <el-input-number size="small" v-model="editFlightForm.totalSeats" :min="0" auto-complete="off" placeholder="请输入总座位数"></el-input-number>
        </el-form-item>
        <el-form-item label="剩余座位数" prop="remainingSeats">
          <el-input-number size="small" v-model="editFlightForm.remainingSeats" :min="0" auto-complete="off" :max="editFlightForm.totalSeats" placeholder="请输入剩余座位数"></el-input-number>
        </el-form-item>
        <el-form-item label="票价" prop="price">
          <el-input-number type="number" size="small" v-model="editFlightForm.price" :min="0" auto-complete="off" placeholder="请输入票价"></el-input-number>
        </el-form-item>
        <el-form-item label="离开时间" prop="departureTime">
          <el-date-picker
            v-model="editFlightForm.departureTime"
            type="datetime"
            value-format="yyyy-MM-dd HH:mm:ss"
            :picker-options="departureDateOptions"
            placeholder="选择日期">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="到达时间" prop="arrivalTime">
          <el-date-picker
            v-model="editFlightForm.arrivalTime"
            type="datetime"
            value-format="yyyy-MM-dd HH:mm:ss"
            :picker-options="departureDateOptions"
            placeholder="选择日期">
          </el-date-picker>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button size="small" @click='closeDialog("edit")'>取消</el-button>
        <el-button size="small" type="primary" :loading="loading" class="title" @click="submitForm('editFlightForm')">保存</el-button>
      </div>
    </el-dialog>
    <!--    新增航线-->
    <el-dialog :title="title1" :visible.sync="addFormVisible" width="50%" @click='closeDialog("edit")'>
      <el-form label-width="150px" ref="editAddFlightForm" :model="editAddFlightForm" :rules="rules">
        <el-form-item label="起飞城市" prop="departureCity">
          <el-select size="small" @change='queryByStartAirport($event)' v-model="editAddFlightForm.departureCity" filterable clearable placeholder="请选择起飞城市">
            <el-option
              v-for="item in city"
              :key="item.id"
              :label="item.city"
              :value="item.city">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="降落城市" prop="arrivalCity">
          <el-select size="small" v-model="editAddFlightForm.arrivalCity"  :disabled = disabled filterable clearable placeholder="请选择降落城市">
            <el-option
              v-for="item in startRoute"
              :key="item.id"
              :label="item.city"
              :value="item.city">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="航班号" prop="flightNumber">
          <el-input size="small" v-model="editAddFlightForm.flightNumber" placeholder="请输入航班号"></el-input>
        </el-form-item>
        <el-form-item label="飞机型号" prop="aircraftModel">
          <el-input size="small" v-model="editAddFlightForm.aircraftModel" placeholder="请输入飞机型号"></el-input>
        </el-form-item>
        <el-form-item label="航空公司" prop="airline">
          <el-input size="small" v-model="editAddFlightForm.airline" placeholder="请输入航空公司"></el-input>
        </el-form-item>
        <el-form-item label="总座位数" prop="totalSeats">
          <el-input-number size="small" v-model="editAddFlightForm.totalSeats" :min="0" auto-complete="off" placeholder="请输入总座位数"></el-input-number>
        </el-form-item>
        <el-form-item label="剩余座位数" prop="remainingSeats">
          <el-input-number size="small" v-model="editAddFlightForm.remainingSeats" :min="0" auto-complete="off" :max="editAddFlightForm.totalSeats" placeholder="请输入剩余座位数（默认等于总座位数）"></el-input-number>
          <span class="form-tip">不填写则默认等于总座位数</span>
        </el-form-item>
        <el-form-item label="票价" prop="price">
          <el-input-number type="number" size="small" v-model="editAddFlightForm.price" :min="0" auto-complete="off" placeholder="请输入票价"></el-input-number>
        </el-form-item>
        <el-form-item label="离开时间" prop="departureTime">
          <el-date-picker
            v-model="editAddFlightForm.departureTime"
            type="datetime"
            value-format="yyyy-MM-dd HH:mm:ss"
            :picker-options="departureDateOptions"
            placeholder="选择日期">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="到达时间" prop="arrivalTime">
          <el-date-picker
            v-model="editAddFlightForm.arrivalTime"
            type="datetime"
            value-format="yyyy-MM-dd HH:mm:ss"
            :picker-options="departureDateOptions"
            placeholder="选择日期">
          </el-date-picker>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button size="small" @click='closeDialog("edit")'>取消</el-button>
        <el-button size="small" type="primary" :loading="loading" class="title" @click="submitAddForm('editAddFlightForm')">保存</el-button>
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
  name: "flight",
  data(){
    /* 定义初始化变量 */
    return{
      // 基本信息
      userKind:'',
      editAddFlightForm:{
        departureCity : '',
        arrivalCity:'',
        flightNumber: '',
        aircraftModel: '',
        departureTime:'',
        arrivalTime:'',
        status: 0,
        totalSeats:0,
        remainingSeats:0,
        price:0,
        airline: ''
      },
      placeholder:'',
      disabledFlight: true,
      disabled : true,
      title1:"新增航班",
      title: "修改航班",
      loading: false, //显示加载
      editFormVisible: false,
      addFormVisible : false,
      // 列表信息
      flightData: [],
      pageNum: 1,
      pageSize: 10,
      total: 0,
      // 条件查找
      formInline: {
        departureCity:'',
        arrivalCity:'',
        flightNumber:'',
        departureTime:''
      },
      editFlightForm:{
        departureCity : '',
        arrivalCity:'',
        flightNumber: '',
        aircraftModel: '',
        departureTime:'',
        arrivalTime:'',
        status:'',
        totalSeats:0,
        remainingSeats:0,
        price:0,
        airline: '',
        id: ''
      },
      rules: {
        flightNumber: [
          {required: true, message: '请输入航班号', trigger: 'blur'},
          { pattern: /^[A-Z0-9]{2,8}$/, message: '航班号格式应为2-8位大写字母和数字组合', trigger: 'blur' }
        ],
        departureCity: [
          {required: true, message: '请选择起飞机场', trigger: 'change'}
        ],
        arrivalCity: [
          { validator: this.validateDestinationAirport, trigger: 'change' }
        ],
        aircraftModel: [
          {required: true, message: '请输入飞机型号', trigger: 'blur'}
        ],
        departureTime: [
          {required: true, message: '请选择出发时间', trigger: 'change'}
        ],
        arrivalTime: [
          { validator: this.validateArrivalTime, trigger: 'change' }
        ],
        status: [
          {required: true, message: '请选择航班状态', trigger: 'change'}
        ],
        totalSeats: [
          {required: true, message: '请输入总座位数', trigger: 'blur'},
          { type: 'number', min: 0, message: '座位数不能小于0', trigger: 'blur' }
        ],
        remainingSeats: [
          {required: true, message: '请输入剩余座位数', trigger: 'blur'},
          { type: 'number', min: 0, message: '座位数不能小于0', trigger: 'blur' },
          
        ],
        price: [
          {required: true, message: '请输入票价', trigger: 'blur'},
          { type: 'number', min: 0, message: '价格不能小于0', trigger: 'blur' }
        ],
        airline: [
          {required: true, message: '请输入航空公司', trigger: 'blur'}
        ],
      },
      city:[],
      aircraftType:[],
      startRoute:[],
      airlines:[],
      plane: [],
      // 日期选择器选项
      departureDateOptions: {
        disabledDate(time) {
          return time.getTime() < Date.now() - 8.64e7; // 禁用今天之前的日期
        }
      },
    }
  },
  /* 定义事件函数 */
  methods:{
    exportData() {
      // 模拟要导出的数据
      const data = this.flightData;
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

    selectPlane(id){
      // 加载状态开始
      this.disabledFlight = true;
      this.loading = true;

      // 调用后端接口获取飞机类型信息
      this.$axios.get('/admin/flight/aircraft/' + id)
        .then(res => {
          if (res.data.code === 200) {
            this.aircraftType = res.data.data; // 接收后端返回的真实数据
            this.disabledFlight = false;
          } else {
            this.$message.error('获取飞机类型失败');
            this.disabledFlight = true;
          }
        })
        .catch(error => {
          console.error('请求飞机类型出错:', error);
          this.$message.error('网络异常，请重试');
          this.disabledFlight = true;
        })
        .finally(() => {
          this.loading = false; // 加载结束
        });
    },
    addFlight(){
      // 重置表单
      this.resetAddForm();
      // 打开对话框
      this.addFormVisible = true;
      this.disabledFlight = true;
      this.disabled = true;
      // 加载数据
      this.queryAllCity();
      this.queryPlaneMsg();
      this.queryAirlines();
    },
    queryByStartAirport(cityName){
      if (cityName) {
        // 过滤掉当前选择的起飞城市，其他城市都可以作为目的地
        this.startRoute = this.city.filter(airport => airport.city !== cityName);
        this.disabled = false;
      } else {
        // 如果没有选择起飞城市，则清空可选目的地机场并禁用相关控件
        this.startRoute = [];
        this.disabled = true;
      }
    },
    submitForm(formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          this.loading = true;
          
          // 确保 remainingSeats 是数字类型
          const flightData = { ...this.editFlightForm };
          
          // 格式化时间字段
          if (flightData.departureTime) {
            flightData.departureTime = this.formatDateTime(flightData.departureTime);
          }
          if (flightData.arrivalTime) {
            flightData.arrivalTime = this.formatDateTime(flightData.arrivalTime);
          }
          // 确保数值字段是数字类型
          if (flightData.remainingSeats !== undefined && flightData.remainingSeats !== null && flightData.remainingSeats !== '') {
            flightData.remainingSeats = parseInt(flightData.remainingSeats, 10);
          }
          if (flightData.totalSeats !== undefined) {
            flightData.totalSeats = Number(flightData.totalSeats);
          }
          if (flightData.price !== undefined) {
            flightData.price = Number(flightData.price);
          }
          
          // 数据验证
          if (flightData.remainingSeats > flightData.totalSeats) {
            this.loading = false;
            this.$message.error('剩余座位数不能大于总座位数');
            return;
          }
          
          // 保存修改后的航班信息
          this.$axios.put("/admin/flights", flightData, {
          headers: {
            'Authorization': sessionStorage.getItem('token') ? `Bearer ${sessionStorage.getItem('token')}` : '',
            'Content-Type': 'application/json',
            'Accept': 'application/json'
        }
      }).then(res => {
            // 无论成功失败都结束加载状态
            this.loading = false;
            
            if (res.data.code === 1) {
              // 关闭对话框
              this.editFormVisible = false
              // 刷新数据
              this.queryAll()
              // 刷新城市数据
              this.queryAllCity()
              // 显示成功消息
              this.$message.success("修改成功")
            } else {
              this.$message.error(res.data.data || "修改失败，请检查输入数据");
            }
          }).catch(err => {
            this.loading = false;
            this.$message.error("提交失败：" + (err.message || "未知错误"));
          });
        } else {
          this.$message.warning("请填写完整的表单信息");
          return false;
        }
      });
    },
    deleteFlight(flightId){
      // 获取航班信息以检查状态
      const flight = this.flightData.find(f => f.id === flightId || f.flightId === flightId);
      
      // 检查航班是否正在飞行中
      if(flight && flight.status === 1) {
        this.$message.error("该航班正在飞行，不能删除");
        return;
      }
      
      this.$confirm(`确定要删除航班 ${flight ? flight.flightNumber : flightId} 吗?`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
        center: true
      }).then(() => {
        // 设置加载状态
        this.loading = true;
        
        // 执行删除操作
        this.$axios.delete("/admin/flights").then(res =>{
          // 无论成功失败都结束加载状态
          this.loading = false;
          
          if(res.data.code === 200){
            this.pageNum = 1
            this.pageSize = 10
            this.queryAll()
            this.queryAllCity()
            this.$message.success("删除成功")
          } else {
            this.$message.error(res.data.data || "删除失败，请稍后重试");
          }
        }).catch(err => {
          this.loading = false;
          this.$message.error("删除失败：" + (err.message || "未知错误"));
        });
      }).catch(() => {
        // 用户取消删除操作
        this.$message({
          type: 'info',
          message: '已取消删除操作'
        });
      });
    },
    submitAddForm(formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          this.loading = true;
          
          // 创建一个新对象，避免直接修改表单数据
          const flightData = { ...this.editAddFlightForm };

          // 格式化时间字段
          if (flightData.departureTime) {
            flightData.departureTime = this.formatDateTime(flightData.departureTime);
          }
          if (flightData.arrivalTime) {
            flightData.arrivalTime = this.formatDateTime(flightData.arrivalTime);
          }
          // 确保数值字段是数字类型
          if (flightData.remainingSeats !== undefined && flightData.remainingSeats !== null && flightData.remainingSeats !== '') {
            flightData.remainingSeats = parseInt(flightData.remainingSeats, 10);
          }
          if (flightData.totalSeats !== undefined) {
            flightData.totalSeats = Number(flightData.totalSeats);
          }
          if (flightData.price !== undefined) {
            flightData.price = Number(flightData.price);
          }
          
          // 如果剩余座位数未设置，则默认等于总座位数（保持数字类型）
          if ((!flightData.remainingSeats || flightData.remainingSeats === 0) && flightData.totalSeats) {
            flightData.remainingSeats = flightData.totalSeats; // 修复：保持数字类型，不转换为字符串
          }
          
          // 数据验证
          if (flightData.remainingSeats > flightData.totalSeats) {
            this.loading = false;
            this.$message.error('剩余座位数不能大于总座位数');
            return;
          }
          
          // 提交数据到正确的接口
          this.$axios.post("/admin/flights", flightData, {
            headers: {
              'Authorization': sessionStorage.getItem('token') ? `Bearer ${sessionStorage.getItem('token')}` : '',
              'Content-Type': 'application/json',
              'Accept': 'application/json'
            }
          }).then(res => {
            this.loading = false;
            
            if (res.data.code === 1) { // 修复：后端成功返回码应该是200
              this.addFormVisible = false;
              this.queryAll();
              this.queryAllCity();
              this.$message.success("新增成功");
            } else {
              this.$message.error(res.data.message || "添加失败，请检查输入数据");
            }
          }).catch(err => {
            this.loading = false;
            this.$message.error("提交失败：" + (err.message || "未知错误"));
          });
        } else {
          this.$message.warning("请填写完整的表单信息");
          return false;
        }
      });
    },
    handleSizeChange(val) {
      this.pageSize = val
      this.queryAll();
      this.queryAllCity()
    },
    handleCurrentChange(val) {
      this.pageNum = val
      this.queryAll()
      this.queryAllCity()
    },
    // 打开编辑窗口
    handleEdit(flight) {
      // 重置表单
      this.resetEditForm();
      
      // 查询飞机和机场数据
      this.queryAllCity();
      this.queryPlaneMsg();
      this.queryAirlines();
      
      // 首先启用目的机场选择器
      this.disabled = false;
      this.disabledFlight = false;
      
      // 设置数据（确保对象属性映射正确）
      this.editFlightForm = {
        id: flight.id || flight.flightId,
        flightNumber: flight.flightNumber || flight.aircraftCode,
        aircraftModel: flight.aircraftModel || '',
        departureCity: flight.departureCity || flight.departureAirport,
        arrivalCity: flight.arrivalCity || flight.destinationAirport,
        departureTime: flight.departureTime,
        arrivalTime: flight.arrivalTime,
        price: flight.price || flight.economyClassPrice,
        totalSeats: flight.totalSeats || (flight.economyClassNum + flight.firstClassNum),
        remainingSeats: flight.remainingSeats || flight.economyClassNum,
        status: flight.status,
        airline: flight.airline || flight.airlineName,
        creator: flight.creator,
        modifier: flight.modifier
      };
      
      // 加载飞机型号信息
      if (flight.aircraftId) {
        this.selectPlane(flight.aircraftId);
      }
      
      // 加载目的地信息
      if (flight.departureCity || flight.departureAirport) {
        this.queryByStartAirport(flight.departureCity || flight.departureAirport);
      }
      
      // 打开对话框
      this.editFormVisible = true;
    },
    queryPlaneMsg(){
      // 不再调用已删除的飞机信息API，使用静态数据
      const staticPlaneData = [
        { id: 1, aircraftCode: 'B737', aircraftType: '波音737' },
        { id: 2, aircraftCode: 'B777', aircraftType: '波音737' },
        { id: 3, aircraftCode: 'A320', aircraftType: '空客A320' },
        { id: 4, aircraftCode: 'A330', aircraftType: '空客A330' },
      ];
      
      this.plane = staticPlaneData;
    },
    // 编辑窗口关闭
    closeDialog(type) {
      if (type === 'edit') {
        this.editFormVisible = false;
        this.addFormVisible = false;
        // 重置表单
        this.resetEditForm();
        this.resetAddForm();
      }
    },
    search(){
      this.pageNum = 1
      this.queryAll()
      this.queryAllCity()
    },
    queryAllCity(){
      // 不再调用已删除的机场API，而是使用静态数据
      const staticAirportData = [
        { id: 1, airportName: '北京首都国际机场', city: '北京' },
        { id: 2, airportName: '上海浦东国际机场', city: '上海' },
        { id: 3, airportName: '广州白云国际机场', city: '广州' },
        { id: 4, airportName: '深圳宝安国际机场', city: '深圳' },
        { id: 5, airportName: '成都双流国际机场', city: '成都' },
        { id: 6, airportName: '厦门高崎国际机场', city: '厦门' },
        { id: 7, airportName: '重庆江北国际机场', city: '重庆' },
      ];
      
      this.city = staticAirportData;
      this.startRoute = this.city;
    },
    getUserKind(){
      const userDataJSON = sessionStorage.getItem("user");
      // 防止userData为null时访问vipStatus属性
      if (userDataJSON) {
        const userData = JSON.parse(userDataJSON);
        this.userKind = userData.vipStatus || '';
      } else {
        this.userKind = '';
      }
    },
    // 获取航班列表
    queryAll() {
      this.loading = true;
      
      // 构建查询参数
      const params = {
        page: this.pageNum,
        pageSize: this.pageSize,
        departureCity: this.formInline.departureCity || null,
        arrivalCity: this.formInline.arrivalCity || null,
        flightNumber: this.formInline.flightNumber || null,
        departureTime: this.formInline.departureTime || null
      };
      
      // 移除值为null或undefined的属性
      Object.keys(params).forEach(key => {
        if (params[key] === null || params[key] === undefined) {
          delete params[key];
        }
      });
      
      // 使用POST方法发送请求，符合接口文档要求
      this.$axios.post('/admin/flights/page', params)
        .then(res => {
          this.loading = false;
          if (res.data.code === 1) {
            // 更新数据列表和分页信息
            this.flightData = res.data.data.list;
            this.total = res.data.data.total;
            
            // 如果没有数据，显示提示信息
            if (this.flightData.length === 0) {
              this.$message.info('没有找到符合条件的航班');
            }
          } else {
            this.$message.error(res.data.message || '获取航班列表失败');
          }
        })
        .catch(error => {
          this.loading = false;
          console.error('获取航班列表失败:', error);
          this.$message.error('获取航班列表失败，请稍后重试');
        });
    },
    // 搜索按钮点击事件
    search() {
      this.pageNum = 1; // 重置为第一页
      this.queryAll();
    },
    queryAirlines(){
      // 使用静态数据代替API调用
      const staticAirlinesData = [
        { id: 1, name: '中国国际航空', code: 'CA' },
        { id: 2, name: '中国东方航空', code: 'MU' },
        { id: 3, name: '中国南方航空', code: 'CZ' },
        { id: 4, name: '海南航空', code: 'HU' },
        { id: 5, name: '厦门航空', code: 'MF' },
        { id: 6, name: '春秋航空', code: '9C' },
      ];
      
      this.airlines = staticAirlinesData;
    },
    // 重置表单
    resetAddForm() {
      this.editAddFlightForm = {
        departureCity: '',
        arrivalCity: '',
        flightNumber: '',
        aircraftModel: '',
        departureTime: '',
        arrivalTime: '',
        status: 0,
        totalSeats: 0,    // 确保是数字类型
        remainingSeats: 0, // 确保是数字类型
        price: 0,         // 确保是数字类型
        airline: ''
      };
      // 如果表单已经挂载，则重置表单验证
      if (this.$refs.editAddFlightForm) {
        this.$refs.editAddFlightForm.resetFields();
      }
    },
    
    resetEditForm() {
      this.editFlightForm = {
        departureCity: '',
        arrivalCity: '',
        flightNumber: '',
        aircraftModel: '',
        departureTime: '',
        arrivalTime: '',
        status: '',
        totalSeats: 0,    // 确保是数字类型
        remainingSeats: 0, // 确保是数字类型
        price: 0,         // 确保是数字类型
        airline: '',
        id: ''
      };
      // 如果表单已经挂载，则重置表单验证
      if (this.$refs.editFlightForm) {
        this.$refs.editFlightForm.resetFields();
      }
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
    this.queryAll();
    this.queryAllCity();
    this.queryAirlines();
    this.getUserKind();
  },

  mounted() {
    // 监听键盘事件，支持回车键提交表单
    this.handleKeyUp = (e) => {
      if (e.key === 'Enter') {
        if (this.editFormVisible) {
          this.submitForm('editFlightForm');
        } else if (this.addFormVisible) {
          this.submitAddForm('editAddFlightForm');
        }
      }
    };
    
    document.addEventListener('keyup', this.handleKeyUp);
  },
  
  beforeDestroy() {
    // 移除事件监听
    document.removeEventListener('keyup', this.handleKeyUp);
  }
}

</script>

<style scoped>

</style>
