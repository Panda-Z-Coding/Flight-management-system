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
@CrossOrigin(origins = "http://localhost:9999", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS}, allowCredentials = "true")
//航班管理
public class AdminFlightController {
    @Autowired
    private FlightService flightService;
    @PostMapping
    public Result createFlight(@RequestBody FlightDTO flightDTO){
        log.info("添加航班信息：{}",flightDTO);
        Flight flight = flightService.createFlight(flightDTO);
        return Result.success();
    }

    @DeleteMapping
    public Result deleteFlight(@RequestParam List<Long> ids){
        log.info("批量删除航班信息:{}", ids);
        flightService.deleteFlight(ids);
        return Result.success();
    }
    @PostMapping("/page")
    public Result<PageInfo<Flight>> pageQuery(@RequestBody FlightPageQueryDTO queryDTO) {
        log.info("分页查询所有航班信息：{}", queryDTO);
        return Result.success(flightService.pageQuery(queryDTO));
    }

    @PutMapping
    public Result updateFlight(@RequestBody FlightDTO flightDTO){
        log.info("修改航班信息：{}",flightDTO);
        flightService.updateFlight(flightDTO);
        return Result.success();
    }
}
