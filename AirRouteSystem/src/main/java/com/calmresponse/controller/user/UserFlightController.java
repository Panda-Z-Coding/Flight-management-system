package com.calmresponse.controller.user;


import com.calmresponse.dto.FlightPageQueryDTO;
import com.calmresponse.entity.Flight;
import com.calmresponse.result.Result;
import com.calmresponse.service.FlightService;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@Slf4j
@RequestMapping("/user/flights")

public class UserFlightController {

    @Autowired
    private FlightService flightService;
    @PostMapping("/page")
    public PageInfo<Flight> pageQuery(@RequestBody FlightPageQueryDTO queryDTO) {
        return flightService.pageQuery(queryDTO);
    }

}
