package com.calmresponse.model.domain;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 已被预定的座位表
 * @TableName booked_seat
 */
@TableName(value ="booked_seat")
@Data
public class BookedSeat implements Serializable {
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
     * 座位类型（头等舱/经济舱等）
     */
    @TableField(value = "seat_type")
    private String seatType;

    /**
     * 座位号
     */
    @TableField(value = "seat_number")
    private String seatNumber;

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
     * 逻辑删除标记（0未删除 1已删除）
     */
    @TableLogic
    @TableField(value = "is_delete")
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}