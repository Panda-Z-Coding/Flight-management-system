package com.calmresponse.service;

import com.calmresponse.common.BaseResponse;
import com.calmresponse.model.VO.UserVO;
import com.calmresponse.model.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.calmresponse.model.request.UserLoginRequest;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
* @author yufeng
* @description 针对表【user(用户表)】的数据库操作Service
* @createDate 2025-05-15 18:45:44
*/
public interface UserService extends IService<User> {
    /**
     * 登录
     * @param userLoginRequest
     * @return
     */
    BaseResponse login(UserLoginRequest userLoginRequest, HttpServletRequest request);

    long userRegister(String username, String userPassword, String checkPassword);

    List<UserVO> selectAll();

    // 启用/禁用用户
    boolean startOrStop(Long id, Integer status);
}
