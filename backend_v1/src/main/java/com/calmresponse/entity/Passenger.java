package com.calmresponse.entity;

import com.baomidou.mybatisplus.annotation.TableLogic;
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
public class Passenger implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id; // 主键ID
    private String name; // 姓名
    private String idCard; // 身份证号
    private Byte status; // 状态（0成人 1学生）
    private String phoneNumber; // 电话号码
    private LocalDateTime createTime; // 创建时间
    private LocalDateTime updateTime; // 修改时间
    @TableLogic
    private Byte isDelete; // 逻辑删除标记（0未删除 1已删除）
    private String username;// 用户名

}