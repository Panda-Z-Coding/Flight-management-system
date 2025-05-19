package com.calmresponse.service;

import com.calmresponse.model.DTO.FlightDTO;
import com.calmresponse.model.domain.Flight;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RequestBody;

import java.text.ParseException;
import java.util.List;

/**
* @author yufeng
* @description 针对表【flight(航班表)】的数据库操作Service
* @createDate 2025-05-15 18:56:01
*/
public interface FlightService extends IService<Flight> {



    //添加航班
    Flight createFlight(FlightDTO flightDTO, HttpServletRequest request);

    //根据ids批量删除航班
    List<Long> deleteFlight(List<Long> ids, HttpServletRequest request);

    //查询所有航班
    List<Flight> selectAll();


    Boolean updateFlight(@RequestBody FlightDTO flightDTO, HttpServletRequest request) throws ParseException;
}
