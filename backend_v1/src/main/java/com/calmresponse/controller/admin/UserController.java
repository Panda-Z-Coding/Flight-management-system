package com.calmresponse.controller.admin;


import com.calmresponse.dto.UserPageQueryDTO;
import com.calmresponse.result.Result;
import com.calmresponse.service.UserService;
import com.calmresponse.vo.UserVO;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/user")
@Slf4j
//用户管理
public class UserController {

    @Autowired
    private UserService userService;

    // 分页查询所有用户信息
/*    @GetMapping("/page")
    public Result<UserVO> getAllUsers(UserPageQueryDTO userPageQueryDTO) {
        log.info("查询所有用户信息");
        PageInfo<UserVO> users = userService.page(userPageQueryDTO);
        return users;
    }*/


    //分页查询所有用户信息
    @PostMapping("/page")
    public Result<PageInfo<UserVO>> pageQuery(@RequestBody UserPageQueryDTO userPageQueryDTO) {
        log.info("管理员根据用户名分页查询所有用户信息：{}", userPageQueryDTO);
        PageInfo<UserVO> users = userService.pageQuery(userPageQueryDTO);
        return Result.success(users);
    }
    // 启用/禁用用户
    @PostMapping("/status/{status}")
    public Result updateStatus(@PathVariable Integer status,Long id) {
        log.info("启用or禁用：status-{},id-{}",status,id);
        userService.startOrStop(id, status);
        return Result.success();
    }
}