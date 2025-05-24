package com.calmresponse.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
//确认改签or退款
public class ConfirmedOrdersDTO {
    private String orderNumber;//订单号
    private Double serviceFee;//手续费
    private LocalDateTime updateTime; // 修改时间
    //private String orderType;//订单类型 “改签”or“退票”
}
