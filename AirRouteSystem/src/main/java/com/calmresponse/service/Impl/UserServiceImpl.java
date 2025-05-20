package com.calmresponse.service.Impl;


import com.calmresponse.entity.User;
import com.calmresponse.mapper.UserMapper;
import com.calmresponse.service.UserService;
import com.calmresponse.vo.UserVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;
    //管理员查询所有用户信息
    public List<UserVO> selectAll() {
        //查询所有权限为0的用户
        List<User> userList=userMapper.selectAll();
        List<UserVO> userVOList=new ArrayList<>();
        for (User user : userList) {
            if(user.getPermission()==0){
                UserVO userVO = new UserVO();
                BeanUtils.copyProperties(user,userVO);
                userVOList.add(userVO);
            }
        }
        return userVOList;
    }

    // 启用/禁用用户
    public void startOrStop(Long id, Integer status) {
        User user = new User();
        user.setId(id);
        user.setStatus(status);
        userMapper.update(user);
    }
}
