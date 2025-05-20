package com.calmresponse.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlightDTO {
    private Long id; // 主键ID
    private String flightNumber; // 航班号
    private String aircraftModel; // 飞机型号
    private String seatLayout; // 座位布局
    private String departureCity; // 起飞城市
    private String arrivalCity; // 着陆城市
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime departureTime; // 起飞时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime arrivalTime; // 着陆时间
    private Double price; // 价格
    private Integer totalSeats; // 总票数
    private Integer remainingSeats; // 余票数
    //private LocalDateTime createTime; // 创建时间
    //private LocalDateTime updateTime; // 修改时间
    private String airline; // 航空公司
    //private Integer isDelete; // 逻辑删除标记（0未删除 1已删除）？？
}
