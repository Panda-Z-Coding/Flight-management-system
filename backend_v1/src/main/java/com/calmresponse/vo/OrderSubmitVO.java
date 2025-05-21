package com.calmresponse.vo;

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
public class OrderSubmitVO implements Serializable {
    //订单id
    //private Long id;
    //订单号
    private String orderNumber;
    private Double price; // 价格
    //下单时间
    private LocalDateTime orderTime;
}