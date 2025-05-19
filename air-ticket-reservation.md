## 功能

- 注册
- 登录

### 用户

- 查询航班
- 下单
- 支付
- 退票
- 查询订单信息
- crud乘客信息

### 管理员

- crud航班信息
- crud用户信息
- crud订单信息
- crud公告
- crud已预定的座位表？？？
- 数据分析？？？



### 接口文档

ai







## 数据库

### user表

- id bigint
- username 用户名 varchar(255)
- password 密码 varchar
- 余额 double
- 创建时间 
- 修改时间
- 状态（0正常1封号）tinyint
- 权限（0用户 1管理员）？ tinyint



### 航班表

- id bigint
- 航班号 varchar
- 飞机型号（波音747） 座位布局--票数 varchar
- 起飞城市 
- 着陆城市
- 起飞时间 datetime
- 着陆时间
- 价格 （todo 头等舱）double
- 票数
- 余票数
- 创建时间
- 修改时间
- 航空公司



### 总订单表

- id（根据创建时间 非常有规律）bigint
- username 
- 创建时间
- 修改时间
- 价格 double
- 航班号
- 支付方式 varchar

- 状态（0未支付 1已支付 2退款） 备注：根据时间收取手续费
- 手续费（默认值0） double
- 座位号 ？？
- 乘客姓名



### 乘客表

- id
- 姓名
- 身份证 varchar
- 状态（默认0成人 1学生）
- 电话号码



### 已被预定的座位表

- id
- 航班号
- 座位类型（头等舱）
- 座位号 varchar



### 地点表？？

### 公告表





