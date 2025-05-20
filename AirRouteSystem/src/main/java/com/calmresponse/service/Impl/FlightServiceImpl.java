package com.calmresponse.service.Impl;


import com.calmresponse.common.ErrorCode;
import com.calmresponse.dto.FlightDTO;
import com.calmresponse.dto.FlightPageQueryDTO;
import com.calmresponse.entity.Flight;
import com.calmresponse.exception.BusinessException;
import com.calmresponse.mapper.FlightMapper;
import com.calmresponse.service.FlightService;
import com.calmresponse.utils.Utils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

@Service

public class FlightServiceImpl implements FlightService {
    @Autowired
    private FlightMapper flightMapper;
    //添加航班
    public Flight createFlight(FlightDTO flightDTO, HttpServletRequest request) {
        if (!Utils.isAuth(request)){//无管理员权限则无法添加
            throw new BusinessException(ErrorCode.NO_AUTH,"无管理员权限");
        }
        //创建flight，拷贝
        Flight flight = new Flight();
        BeanUtils.copyProperties(flightDTO,flight);
        //创建时间，修改时间
        flight.setCreateTime(LocalDateTime.now());
        flight.setUpdateTime(LocalDateTime.now());
        //默认情况
        flight.setIsDelete(1);
        flight.setRemainingSeats(flightDTO.getTotalSeats());
        //mapper中insert
        boolean insert=flightMapper.insert(flight);
        if(!insert){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"添加航班失败");
        }
        return flight;
    }

    //根据ids批量删除航班
    public List<Long> deleteFlight(List<Long> ids, HttpServletRequest request) {
        if (!Utils.isAuth(request)){//无管理员权限则无法添加
            throw new BusinessException(ErrorCode.NO_AUTH,"无管理员权限");
        }
        //状态为1的航班表示在飞行，不能删除
        for (Long id : ids) {
            Flight flight=flightMapper.getById(id);
            if(flight.getStatus()==1){
                throw new RuntimeException("该航班正在飞行，不能删除");
            }
            boolean delete=flightMapper.deleteById(id);
            if(!delete){
                throw new BusinessException(ErrorCode.SYSTEM_ERROR,"删除航班失败");
            }
        }
        return ids;
    }
    //根据起飞、着陆城市，起飞、着陆时间 分页查询航班
    @Override
    public PageInfo<Flight> pageQuery(FlightPageQueryDTO queryDTO) {
        // 开启分页
        PageHelper.startPage(queryDTO.getPage(), queryDTO.getPageSize());
        // 调用 mapper 层方法进行查询
        List<Flight> flights = flightMapper.pageQuery(queryDTO);
        return new PageInfo<>(flights);
    }

    //根据id修改航班信息
    public void updateFlight(FlightDTO flightDTO,HttpServletRequest request) {
        if (!Utils.isAuth(request)){//无管理员权限则无法添加
            throw new BusinessException(ErrorCode.NO_AUTH,"无管理员权限");
        }
        Flight flight = new Flight();
        BeanUtils.copyProperties(flightDTO,flight);
        //着陆时间不能早于起飞时间
        if(flight.getArrivalTime().isBefore(flight.getDepartureTime())){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"着陆时间不能早于起飞时间");
        }
        //余票数不能多于总票数
        if(flight.getRemainingSeats()>flight.getTotalSeats()){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"余票数不能多于总票数");
        }
        boolean update=flightMapper.update(flight);
        if(!update){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"修改航班失败");
        }
    }


}
