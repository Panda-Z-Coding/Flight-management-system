package com.calmresponse.controller.admin;

import com.calmresponse.common.BaseResponse;
import com.calmresponse.request.AdminLoginRequest;
import com.calmresponse.request.UserLoginRequest;
import com.calmresponse.result.Result;
import com.calmresponse.service.LoginService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;


@RestController
@Slf4j
@RequestMapping("/admin/login")
@CrossOrigin(origins = "http://localhost:9999", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS}, allowCredentials = "true")
public class AdminLoginController {
    @Autowired
    private LoginService loginService;
    /**
     * 管理员登录
     * @param adminLoginRequest
     * @return
     */
    @PostMapping
    public Result loginAdmin(@RequestBody AdminLoginRequest adminLoginRequest, HttpServletRequest request){
        return loginService.login(adminLoginRequest, request);
    }
}
