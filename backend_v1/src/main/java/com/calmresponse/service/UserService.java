package com.calmresponse.service;


import com.calmresponse.dto.UserPageQueryDTO;
import com.calmresponse.vo.UserVO;
import com.github.pagehelper.PageInfo;

import java.util.List;

public interface UserService {

    //管理员分页查询所有用户信息
    PageInfo<UserVO> pageQuery(UserPageQueryDTO userPageQueryDTO);
    // 启用/禁用用户
    void startOrStop(Long id, Integer status);
}
