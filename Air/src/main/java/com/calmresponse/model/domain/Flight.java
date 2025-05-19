package com.calmresponse.model.domain;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 航班表
 * @TableName flight
 */
@TableName(value ="flight")
@Data
public class Flight implements Serializable {
    /**
     * 
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 航班号
     */
    @TableField(value = "flight_number")
    private String flightNumber;

    /**
     * 飞机型号（波音747）
     */
    @TableField(value = "aircraft_model")
    private String aircraftModel;

    /**
     * 座位布局--票数
     */
    @TableField(value = "seat_layout")
    private String seatLayout;

    /**
     * 起飞城市
     */
    @TableField(value = "departure_city")
    private String departureCity;

    /**
     * 着陆城市
     */
    @TableField(value = "arrival_city")
    private String arrivalCity;

    /**
     * 起飞时间
     */
    @TableField(value = "departure_time")
    private Date departureTime;

    /**
     * 着陆时间
     */
    @TableField(value = "arrival_time")
    private Date arrivalTime;

    /**
     * 价格
     */
    @TableField(value = "price")
    private Double price;

    /**
     * 票数
     */
    @TableField(value = "total_seats")
    private Integer totalSeats;

    /**
     * 余票数
     */
    @TableField(value = "remaining_seats")
    private Integer remainingSeats;

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
     * 航空公司
     */
    @TableField(value = "airline")
    private String airline;

    /**
     * 逻辑删除标记（0未删除 1已删除）
     */
    @TableLogic
    @TableField(value = "is_delete")
    private Integer isDelete;
    /**
     * 飞行状态（0未起飞 1起飞）
     */
    private Integer status;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}