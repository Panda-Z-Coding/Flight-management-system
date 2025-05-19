package com.calmresponse.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.calmresponse.model.DTO.PassengerDTO;
import com.calmresponse.model.domain.Passenger;
import com.calmresponse.service.PassengerService;
import com.calmresponse.mapper.PassengerMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
* @author yufeng
* @description 针对表【passenger(乘客表)】的数据库操作Service实现
* @createDate 2025-05-15 18:55:42
*/
@Service
public class PassengerServiceImpl extends ServiceImpl<PassengerMapper, Passenger>
    implements PassengerService{

    @Autowired
    private PassengerMapper passengerMapper;

    //添加乘客信息
    public void createPassenger(PassengerDTO passengerDTO) {
        //创建乘客，拷贝
        Passenger passenger = new Passenger();
        BeanUtils.copyProperties(passengerDTO, passenger);
        //mapper中insert
        passengerMapper.insert(passenger);
    }

    //根据ids批量删除乘客
    public void deletePassenger(List<Long> ids) {
        for (Long id : ids) {
            passengerMapper.deleteById(id);
        }
    }

    //查询所有乘客信息
    public List<Passenger> selectAll() {
        return this.list();
    }

    //根据id修改乘客信息
    public void updatePassenger(PassengerDTO passengerDTO) {
        Passenger passenger = new Passenger();
        BeanUtils.copyProperties(passengerDTO, passenger);
        this.updateById(passenger);
    }
}




