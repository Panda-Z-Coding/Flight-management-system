package com.calmresponse.model.DTO;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * 航班表
 * @TableName flight
 */
@TableName(value ="flight")
@Data
public class FlightDTO implements Serializable {
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

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime departureTime; // 起飞时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime arrivalTime; // 着陆时间

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
     * 航空公司
     */
    @TableField(value = "airline")
    private String airline;


    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}