package com.calmresponse.controller.user;

import com.calmresponse.common.BaseResponse;
import com.calmresponse.common.ErrorCode;
import com.calmresponse.common.ResultUtils;
import com.calmresponse.constant.UserConstant;
import com.calmresponse.dto.UserDTO;
import com.calmresponse.exception.BusinessException;
import com.calmresponse.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;


@RestController
@Slf4j
@RequestMapping("/user")
@CrossOrigin(origins = "http://localhost:9999", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS}, allowCredentials = "true")
public class UserInfoController {

    /**
     * 获取当前登录用户信息
     * @param request
     * @return 用户信息
     */
    @GetMapping("/info")
    public Result getUserInfo(HttpServletRequest request) {
        log.info("获取当前登录用户信息");
        // 从session中获取用户信息
        Object userObj = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATUS);
        if (userObj == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN, "用户未登录");
        }
        UserDTO userDTO = (UserDTO) userObj;
        return Result.success(userDTO);
    }
}