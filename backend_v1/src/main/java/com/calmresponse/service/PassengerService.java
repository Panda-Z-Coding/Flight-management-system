package com.calmresponse.service;



import com.calmresponse.dto.PageQueryDTO;
import com.calmresponse.dto.PassengerDTO;
import com.calmresponse.dto.PassengerPageQueryDTO;
import com.calmresponse.dto.UserPageQueryDTO;
import com.calmresponse.entity.Passenger;
import com.github.pagehelper.PageInfo;

import java.util.List;

public interface PassengerService {
    //添加乘客信息
    void createPassenger(PassengerDTO passengerDTO);
    //根据ids批量删除乘客
    void deletePassenger(List<Long> ids);

    //根据id修改乘客信息
    void updatePassenger(PassengerDTO passengerDTO);

    //分页查询乘客信息
    PageInfo<Passenger> pageQuery(UserPageQueryDTO userPageQueryDTO);
}
