package com.calmresponse.entity;

import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Flight implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id; // 主键ID
    private String flightNumber; // 航班号
    private String aircraftModel; // 飞机型号
    private String seatLayout; // 座位布局
    private String departureCity; // 起飞城市
    private String arrivalCity; // 着陆城市
    private LocalDateTime departureTime; // 起飞时间
    private LocalDateTime arrivalTime; // 着陆时间
    private Double price; // 价格
    private Integer totalSeats; // 总票数
    private Integer remainingSeats; // 余票数
    private LocalDateTime createTime; // 创建时间
    private LocalDateTime updateTime; // 修改时间
    private String airline; // 航空公司
    @TableLogic
    private Integer isDelete; // 逻辑删除标记（0未删除 1已删除）
    private Integer status;//1正在飞行的航班，默认0未出发的航班。
}