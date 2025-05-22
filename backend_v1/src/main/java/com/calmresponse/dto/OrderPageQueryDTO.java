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
public class OrderPageQueryDTO implements Serializable {


    //页码
    private int page;

    //每页显示记录数
    private int pageSize;

    //订单号
    private String orderNumber;

    //乘车人
    private String passengerName;

    //身份证
    private String passengerIdCard;

    private String username;
}