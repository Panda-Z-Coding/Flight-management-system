package com.calmresponse.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PassengerDTO {
    private Long id; // 主键ID
    private String name;// 乘客姓名
    private String idCard;// 身份证号
    private Byte status;// 乘客状态
    private String phoneNumber;// 手机号
    private String username;// 用户名
    //private LocalDateTime createTime; // 创建时间
    //private LocalDateTime updateTime;// 修改时间
    //private Integer isDelete;// 逻辑删除标记（0未删除 1已删除）
}
