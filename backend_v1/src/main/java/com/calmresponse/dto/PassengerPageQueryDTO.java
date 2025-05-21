package com.calmresponse.dto;

import lombok.Data;

@Data
public class PassengerPageQueryDTO {
    private Integer page; // 页码
    private Integer pageSize; // 每页数量
    private String username;// 用户名

}
