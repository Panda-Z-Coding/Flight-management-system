package com.calmresponse.request;


import lombok.Data;

import java.io.Serializable;

/**
 * 用户登录请求体
 *
 */
@Data
public class UserLoginRequest implements Serializable {

    private static final long serialVersionUID = 3191241716373120793L;

    /**
     * 用户账号
     */
    private String username;

    /**
     * 用户密码
     */
    private String userPassword;

}
