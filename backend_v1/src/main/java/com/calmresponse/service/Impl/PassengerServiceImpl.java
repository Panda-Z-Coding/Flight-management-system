package com.calmresponse.service.Impl;


import com.calmresponse.dto.*;
import com.calmresponse.entity.Flight;
import com.calmresponse.entity.Passenger;
import com.calmresponse.mapper.PassengerMapper;
import com.calmresponse.service.PassengerService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PassengerServiceImpl implements PassengerService {

    @Autowired
    private PassengerMapper passengerMapper;

    //添加乘客信息
    public void createPassenger(PassengerDTO passengerDTO) {
        //创建乘客，拷贝
        Passenger passenger = new Passenger();
        BeanUtils.copyProperties(passengerDTO, passenger);
        //创建时间，修改时间
        passenger.setCreateTime(LocalDateTime.now());
        passenger.setUpdateTime(LocalDateTime.now());
        //默认情况
        passenger.setIsDelete((byte)0);
        //mapper中insert
        passengerMapper.insert(passenger);
    }

    //根据ids批量删除乘客
    public void deletePassenger(List<Long> ids) {
        for (Long id : ids) {
            passengerMapper.deleteById(id);
        }
    }

    //根据用户名分页查询乘客
    public PageInfo<Passenger> pageQuery(UserPageQueryDTO userPageQueryDTO) {
        // 开启分页
        PageHelper.startPage(userPageQueryDTO.getPage(), userPageQueryDTO.getPageSize());
        // 调用 mapper 层方法进行查询
        List<Passenger> passengers = passengerMapper.pageQuery(userPageQueryDTO);
        return new PageInfo<>(passengers);
    }


    //根据id修改乘客信息
    public void updatePassenger(PassengerDTO passengerDTO) {
        Passenger passenger = new Passenger();
        BeanUtils.copyProperties(passengerDTO, passenger);
        passengerMapper.update(passenger);
    }

}

