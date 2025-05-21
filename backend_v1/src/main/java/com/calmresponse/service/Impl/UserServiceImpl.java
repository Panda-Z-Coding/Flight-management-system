package com.calmresponse.service.Impl;


import com.calmresponse.dto.UserPageQueryDTO;
import com.calmresponse.entity.User;
import com.calmresponse.mapper.UserMapper;
import com.calmresponse.service.UserService;
import com.calmresponse.vo.UserVO;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;
    //管理员分页查询所有用户信息
    public PageInfo<UserVO> pageQuery(UserPageQueryDTO userPageQueryDTO) {
        //设置分页参数
        PageHelper.startPage(userPageQueryDTO.getPage(), userPageQueryDTO.getPageSize());
        List<UserVO> userVOList=userMapper.pageQuery(userPageQueryDTO);
        return new PageInfo<>(userVOList);
    }

    // 启用/禁用用户
    public void startOrStop(Long id, Integer status) {
        User user = new User();
        user.setId(id);
        user.setStatus(status);
        userMapper.update(user);
    }
}
