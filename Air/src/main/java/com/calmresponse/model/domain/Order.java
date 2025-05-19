package com.calmresponse.model.domain;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 总订单表
 * @TableName order
 */
@TableName(value ="order")
@Data
public class Order implements Serializable {
    /**
     * 
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户名
     */
    @TableField(value = "username")
    private String username;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    private Date createTime;

    /**
     * 修改时间
     */
    @TableField(value = "update_time")
    private Date updateTime;

    /**
     * 价格
     */
    @TableField(value = "price")
    private Double price;

    /**
     * 航班号
     */
    @TableField(value = "flight_number")
    private String flightNumber;

    /**
     * 支付方式
     */
    @TableField(value = "payment_method")
    private String paymentMethod;

    /**
     * 状态（0未支付 1已支付 2退款）
     */
    @TableField(value = "status")
    private Integer status;

    /**
     * 手续费（默认值0）
     */
    @TableField(value = "service_fee")
    private Double serviceFee;

    /**
     * 座位号
     */
    @TableField(value = "seat_number")
    private String seatNumber;

    /**
     * 乘客姓名
     */
    @TableField(value = "passenger_name")
    private String passengerName;

    /**
     * 乘客身份证
     */
    @TableField(value = "passenger_id_card")
    private String passengerIdCard;

    /**
     * 逻辑删除标记（0未删除 1已删除）
     */
    @TableLogic//逻辑删除注解
    @TableField(value = "is_delete")
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}