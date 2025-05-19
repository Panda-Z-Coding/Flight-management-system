package com.calmresponse.controller;

import com.calmresponse.common.BaseResponse;
import com.calmresponse.common.ResultUtils;
import com.calmresponse.model.DTO.FlightDTO;
import com.calmresponse.model.domain.Flight;
import com.calmresponse.service.FlightService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.List;

/**
 * 航班接口
 *
 * @author yufeng
 * @since 2025/5/15 19:31
 */

@RestController
@Slf4j
@RequestMapping("/flights")
public class FlightController {
    @Autowired
    private FlightService flightService;
    @PostMapping
    public BaseResponse createFlight(@RequestBody FlightDTO flightDTO,HttpServletRequest request){
        log.info("添加航班信息：{}",flightDTO);
        Flight flight = flightService.createFlight(flightDTO, request);
        return ResultUtils.success(flight);
    }

    @DeleteMapping
    public BaseResponse deleteFlight(@RequestParam List<Long> ids, HttpServletRequest request){
        log.info("批量删除航班信息:{}", ids);
        List<Long> idss = flightService.deleteFlight(ids, request);
        return ResultUtils.success(idss);
    }

    @GetMapping("/selectAll")
    public BaseResponse<List<Flight>> selectFlights(){
        log.info("查询所有航班信息:{}");
        List<Flight> flights=flightService.selectAll();
        return ResultUtils.success(flights);
    }

    @PutMapping
    public BaseResponse updateFlight(@RequestBody FlightDTO flightDTO,HttpServletRequest request) throws ParseException {
        log.info("修改航班信息：{}",flightDTO);
        Boolean b = flightService.updateFlight(flightDTO, request);
        return ResultUtils.success(b);
    }

}
