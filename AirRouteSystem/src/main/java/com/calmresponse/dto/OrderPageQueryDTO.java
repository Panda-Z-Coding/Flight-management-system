package com.calmresponse.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class OrderPageQueryDTO implements Serializable {


    //页码
    private int page;

    //每页显示记录数
    private int pageSize;

    //订单号
    private String orderNumber;

    //用户名
    private String username;

}