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
public class FlightPageQueryDTO {
    //private Long id; // 主键ID
    //private String flightNumber; // 航班号
    //private String seatLayout; // 座位布局
    private String departureCity; // 起飞城市
    private String arrivalCity; // 着陆城市
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime departureTime; // 起飞时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime arrivalTime; // 着陆时间
    private Integer page; // 页码
    private Integer pageSize; // 每页数量
}
