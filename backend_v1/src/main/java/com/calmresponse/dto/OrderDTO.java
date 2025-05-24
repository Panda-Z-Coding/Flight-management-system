package com.calmresponse.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
//import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
//用户下单时提交的订单信息
public class OrderDTO {
    private static final long serialVersionUID = 1L;

    private Long id; // 主键ID
    private String username; // 用户名
    //private LocalDateTime createTime; // 创建时间
    //private LocalDateTime updateTime; // 修改时间
    private Double price; // 价格
    private String flightNumber; // 航班号
    //private String paymentMethod; // 支付方式(提交订单不需要支付方式，等到支付才要）
    //private Integer status; // 状态（0未支付 1已支付 2退款）
    //private Double serviceFee; // 手续费
    private String seatNumber; // 座位号
    private String passengerName; // 乘客姓名
    private String passengerIdCard; // 乘客身份证号
    //private Byte isDelete; // 逻辑删除标记（0未删除 1已删除）
}