package com.calmresponse.controller.admin;


import com.calmresponse.dto.OrderPageQueryDTO;
import com.calmresponse.dto.PageQueryDTO;
import com.calmresponse.entity.Orders;
import com.calmresponse.result.PageResult;
import com.calmresponse.result.Result;
import com.calmresponse.service.OrderService;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/admin/order")
@CrossOrigin(origins = "http://localhost:9999", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS}, allowCredentials = "true")
//订单管理
public class AdminOrderController {
    @Autowired
    private OrderService orderService;
    @PostMapping("/page")
    public Result<PageInfo<Orders>> page(@RequestBody OrderPageQueryDTO orderPageQueryDTO){
        log.info("管理员根据订单号分页查询所有订单：{}",orderPageQueryDTO);
        PageInfo<Orders> orderList = orderService.pageQuery(orderPageQueryDTO);
        //return orderList;
        return Result.success(orderList);
    }
}