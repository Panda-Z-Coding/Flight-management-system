package com.calmresponse.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户表
 * @TableName user
 */
@TableName(value ="user")
@Data
public class UserDTO implements Serializable {
    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户名
     */
    @TableField(value = "username")
    private String username;



    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    private Date createTime;

    /**
     * 修改时间
     */
    @TableField(value = "update_time")
    private Date updateTime;

    /**
     * 状态（0正常1封号）
     */
    @TableField(value = "status")
    private Integer status;

    /**
     * 权限（0用户 1管理员）
     */
    @TableField(value = "permission")
    private Byte permission;

    /**
     * 用户余额
     */
    @TableField(value = "balance")
    private Double balance;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}