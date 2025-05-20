package com.calmresponse.controller.admin;


import com.calmresponse.dto.OrderPageQueryDTO;
import com.calmresponse.dto.PageQueryDTO;
import com.calmresponse.result.PageResult;
import com.calmresponse.result.Result;
import com.calmresponse.service.OrderService;
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
    @GetMapping("/page")
    public Result<PageResult> page(OrderPageQueryDTO orderPageQueryDTO){
        log.info("订单分页查询：{}",orderPageQueryDTO);
        PageResult pageResult=orderService.page(orderPageQueryDTO);
        return Result.success(pageResult);
    }
}