package com.calmresponse.controller.user;



import com.calmresponse.dto.FlightPageQueryDTO;
import com.calmresponse.dto.PageQueryDTO;
import com.calmresponse.dto.PassengerDTO;
import com.calmresponse.dto.PassengerPageQueryDTO;
import com.calmresponse.entity.Flight;
import com.calmresponse.entity.Passenger;
import com.calmresponse.result.Result;
import com.calmresponse.service.PassengerService;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/user/passenger")
@CrossOrigin(origins = "http://localhost:9999", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS}, allowCredentials = "true")
public class PassengerController {
    @Autowired
    private PassengerService passengerService;
    @PostMapping
    public Result createPassenger(@RequestBody PassengerDTO passengerDTO){
        log.info("新增乘客信息:{}",passengerDTO);
        passengerService.createPassenger(passengerDTO);
        return Result.success() ;

    }

    @DeleteMapping
    public Result deletePassenger(@RequestParam List<Long> ids){
        log.info("删除乘客信息:{}",ids);
        passengerService.deletePassenger(ids);
        return Result.success() ;
    }
    @PostMapping("/page")
    public Result<PageInfo<Passenger>> pageQuery(@RequestBody PageQueryDTO pageQueryDTO) {
        log.info("分页查询该用户乘客信息:{}",pageQueryDTO);
        PageInfo<Passenger> passengerList = passengerService.pageQuery(pageQueryDTO);
        return Result.success(passengerList);
    }

    @PutMapping
    public Result updatePassenger(@RequestBody PassengerDTO passengerDTO) {
        log.info("修改乘客信息:{}",passengerDTO);
        passengerService.updatePassenger(passengerDTO);
        return Result.success();
    }



}
