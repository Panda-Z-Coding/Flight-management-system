package com.calmresponse.controller;

import cn.hutool.core.util.StrUtil;
import com.calmresponse.common.BaseResponse;
import com.calmresponse.common.ErrorCode;

import com.calmresponse.common.ResultUtils;
import com.calmresponse.exception.BusinessException;
import com.calmresponse.model.VO.UserVO;
import com.calmresponse.model.request.UserLoginRequest;
import com.calmresponse.model.request.UserRegisterRequest;
import com.calmresponse.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户接口
 *
 * @author yufeng
 * @since 2025/5/15 19:30
 */
@RestController
@RequestMapping("user")
@Slf4j
@CrossOrigin(origins = "http://localhost:9999", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS}, allowCredentials = "true")
public class UserController {
    @Autowired
    private UserService userService;
    /**
     * 用户登录
     * @param userLoginRequest
     * @return
     */
    @PostMapping("/login")
    public BaseResponse loginUser(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest httpServletRequest){
        return userService.login(userLoginRequest,httpServletRequest);
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

        long BaseResponse = userService.userRegister(username, password, checkPassword);
        return ResultUtils.success(BaseResponse);
    }

    // 查询所有用户信息 鉴权
    @GetMapping
    public BaseResponse<List<UserVO>> getAllUsers() {
        log.info("查询所有用户信息");
        List<UserVO> users = userService.selectAll();
        return ResultUtils.success(users);
    }

    // 启用/禁用用户
    @PostMapping("/status/{status}")
    public BaseResponse updateStatus(@PathVariable Integer status,Long id) {
        log.info("启用or禁用：status-{},id-{}",status,id);
        boolean data = userService.startOrStop(id, status);
        return ResultUtils.success(data);
    }

}
