package com.calmresponse.service;

import com.calmresponse.model.DTO.PassengerDTO;
import com.calmresponse.model.domain.Passenger;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author yufeng
* @description 针对表【passenger(乘客表)】的数据库操作Service
* @createDate 2025-05-15 18:55:42
*/
public interface PassengerService extends IService<Passenger> {
    //添加乘客信息
    void createPassenger(PassengerDTO passengerDTO);
    //根据ids批量删除乘客
    void deletePassenger(List<Long> ids);
    //查询所有乘客
    List<Passenger> selectAll();
    //根据id修改乘客信息
    void updatePassenger(PassengerDTO passengerDTO);
}
