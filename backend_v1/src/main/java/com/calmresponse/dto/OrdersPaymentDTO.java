package com.calmresponse.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrdersPaymentDTO implements Serializable {
    //订单号
    private String orderNumber;

    //付款方式
    private String paymentMethod;

}
