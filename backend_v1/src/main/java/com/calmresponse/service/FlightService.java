package com.calmresponse.service;

import com.calmresponse.dto.FlightDTO;
import com.calmresponse.dto.FlightPageQueryDTO;
import com.calmresponse.entity.Flight;
import com.github.pagehelper.PageInfo;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface FlightService {
    //添加航班
    Flight createFlight(FlightDTO flightDTO);
    //根据ids批量删除航班
    List<Long> deleteFlight(List<Long> ids);
    /**
     * 根据条件进行航班分页查询
     * @param flightPageQueryDTO 分页查询条件
     * @return 分页查询结果
     */
    PageInfo<Flight> pageQuery(FlightPageQueryDTO flightPageQueryDTO);
    //查询所有航班

    //根据id修改航班信息
    void updateFlight(FlightDTO flightDTO);
}
