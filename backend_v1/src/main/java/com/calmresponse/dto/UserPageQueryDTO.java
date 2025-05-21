package com.calmresponse.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
public class UserPageQueryDTO implements Serializable {


    //页码
    private int page;

    //每页显示记录数
    private int pageSize;

    //用户名
    private String username;

}