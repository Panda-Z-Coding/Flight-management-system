package com.calmresponse.request;

import lombok.Data;

import java.io.Serializable;
/**
 * 管理员登录请求体
 *
 */
@Data
public class AdminLoginRequest implements Serializable {

    private static final long serialVersionUID = 3191241716373120793L;

    /**
     * 管理员账号
     */
    private String username;

    /**
     * 管理员密码
     */
    private String userPassword;

}