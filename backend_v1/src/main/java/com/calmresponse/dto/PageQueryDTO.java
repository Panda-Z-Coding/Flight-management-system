package com.calmresponse.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class PageQueryDTO implements Serializable {


    //页码
    private int page;

    //每页显示记录数
    private int pageSize;

}