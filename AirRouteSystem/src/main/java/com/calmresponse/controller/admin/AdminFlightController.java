package com.calmresponse.controller.admin;


import com.calmresponse.dto.FlightDTO;
import com.calmresponse.dto.FlightPageQueryDTO;
import com.calmresponse.entity.Flight;
import com.calmresponse.result.Result;
import com.calmresponse.service.FlightService;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


@RestController
@Slf4j
@RequestMapping("/admin/flights")
//航班管理
public class AdminFlightController {
    @Autowired
    private FlightService flightService;
    @PostMapping
    public Result createFlight(@RequestBody FlightDTO flightDTO, HttpServletRequest request){
        log.info("添加航班信息：{}",flightDTO);
        Flight flight = flightService.createFlight(flightDTO, request);
        return Result.success();
    }

    @DeleteMapping
    public Result deleteFlight(@RequestParam List<Long> ids,HttpServletRequest request){
        log.info("批量删除航班信息:{}", ids);
        flightService.deleteFlight(ids,request);
        return Result.success();
    }
    @PostMapping("/page")
    public PageInfo<Flight> pageQuery(@RequestBody FlightPageQueryDTO queryDTO) {
        return flightService.pageQuery(queryDTO);
    }

    @PutMapping
    public Result updateFlight(@RequestBody FlightDTO flightDTO,HttpServletRequest request){
        flightService.updateFlight(flightDTO,request);
        return Result.success();
    }
}
