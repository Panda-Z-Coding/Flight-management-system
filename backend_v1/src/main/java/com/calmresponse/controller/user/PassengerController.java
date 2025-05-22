package com.calmresponse.controller.user;



import com.calmresponse.common.ErrorCode;
import com.calmresponse.constant.UserConstant;
import com.calmresponse.dto.*;
import com.calmresponse.entity.Flight;
import com.calmresponse.entity.Passenger;
import com.calmresponse.exception.BusinessException;
import com.calmresponse.result.Result;
import com.calmresponse.service.PassengerService;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/user/passenger")
@CrossOrigin(origins = "http://localhost:9999", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS}, allowCredentials = "true")
public class PassengerController {
    @Autowired
    private PassengerService passengerService;
    @PostMapping
    public Result createPassenger(@RequestBody PassengerDTO passengerDTO){
        log.info("新增乘客信息:{}",passengerDTO);
        passengerService.createPassenger(passengerDTO);
        return Result.success() ;

    }

    @DeleteMapping
    public Result deletePassenger(@RequestParam List<Long> ids){
        log.info("删除乘客信息:{}",ids);
        passengerService.deletePassenger(ids);
        return Result.success() ;
    }
    @PostMapping("/page")
    public Result<PageInfo<Passenger>> pageQuery(@RequestBody UserPageQueryDTO userPageQueryDTO, HttpServletRequest request) {
        // 从 session 中获取用户信息
        Object userObj = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATUS);
        if (userObj == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN, "用户未登录");
        }
        log.info("分页查询该用户乘客信息:{}",userPageQueryDTO);
        UserDTO userDTO = (UserDTO) userObj;
        String username = userDTO.getUsername();
        userPageQueryDTO.setUsername(username);
        PageInfo<Passenger> passengerList = passengerService.pageQuery(userPageQueryDTO);
        return Result.success(passengerList);
    }

    @PutMapping
    public Result updatePassenger(@RequestBody PassengerDTO passengerDTO) {
        log.info("修改乘客信息:{}",passengerDTO);
        passengerService.updatePassenger(passengerDTO);
        return Result.success();
    }



}
