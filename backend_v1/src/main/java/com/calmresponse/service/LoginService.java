package com.calmresponse.service;

import com.calmresponse.common.BaseResponse;
import com.calmresponse.request.AdminLoginRequest;
import com.calmresponse.request.UserLoginRequest;
import com.calmresponse.result.Result;

import javax.servlet.http.HttpServletRequest;

public interface LoginService {




    Result login(UserLoginRequest userLoginRequest, HttpServletRequest request);

    Result login(AdminLoginRequest adminLoginRequest, HttpServletRequest request);

    long userRegister(String username, String password, String checkPassword);
}
