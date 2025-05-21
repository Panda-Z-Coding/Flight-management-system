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
public class UserVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id; // 主键ID
    private String username; // 用户名
    //private String password; // 密码
    private Double balance; // 余额
    private LocalDateTime createTime; // 创建时间
    private LocalDateTime updateTime; // 修改时间
    private Integer status; // 状态（0正常1封号）
    private Byte permission; // 权限（0用户 1管理员）
    private Byte isDelete; // 逻辑删除标记（0未删除 1已删除）
}