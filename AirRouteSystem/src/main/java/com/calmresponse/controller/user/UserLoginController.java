package com.calmresponse.controller.user;

import cn.hutool.core.util.StrUtil;
import com.calmresponse.common.BaseResponse;
import com.calmresponse.common.ErrorCode;
import com.calmresponse.common.ResultUtils;
import com.calmresponse.exception.BusinessException;
import com.calmresponse.request.UserLoginRequest;
import com.calmresponse.request.UserRegisterRequest;
import com.calmresponse.result.Result;
import com.calmresponse.service.LoginService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@Slf4j
@RequestMapping("/user")
public class UserLoginController {
    @Autowired
    private LoginService loginService;
    /**
     * 用户登录
     * @param userLoginRequest
     * @return
     */
    @PostMapping("/login")
    public Result loginUser(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request){
        return loginService.login(userLoginRequest,request);
    }

    /**
     * 用户注册接口
     * @param userRegisterRequest
     * @return 用户id
     */
    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest){
        if (userRegisterRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String username = userRegisterRequest.getUsername();
        String password = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        if (!StrUtil.isAllNotBlank(username,password,checkPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号、密码、校验密码不能为空");
        }

        long userId = loginService.userRegister(username, password, checkPassword);
        return ResultUtils.success(userId);
    }
}
