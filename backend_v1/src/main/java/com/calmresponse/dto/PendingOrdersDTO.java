package com.calmresponse.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
//考虑修改的订单信息（改签or退票）
public class PendingOrdersDTO {
    private Long orderId;//订单id
    private String orderType;//订单类型 “改签”or“退票”
    private Long flightId;//航班id
    private String seatNumber;//座位号
}
