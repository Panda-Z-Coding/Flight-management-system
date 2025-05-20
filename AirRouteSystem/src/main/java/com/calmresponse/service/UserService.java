package com.calmresponse.service;


import com.calmresponse.vo.UserVO;

import java.util.List;

public interface UserService {

    //管理员查询所有用户信息
    List<UserVO> selectAll();
    // 启用/禁用用户
    void startOrStop(Long id, Integer status);
}
