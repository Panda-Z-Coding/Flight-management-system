package com.calmresponse.controller.user;

import com.calmresponse.dto.*;
import com.calmresponse.entity.Orders;
import com.calmresponse.result.Result;
import com.calmresponse.service.OrderService;
import com.calmresponse.vo.OrderSubmitVO;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@Slf4j
@RequestMapping("/user/order")
public class UserOrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/page")
    public Result<PageInfo<Orders>> pageQuery(@RequestBody OrderPageQueryDTO orderPageQueryDTO) {
        log.info("用户根据乘车人分页查询所有订单:{}",orderPageQueryDTO);
        PageInfo<Orders> order = orderService.pageQuery(orderPageQueryDTO);
        return Result.success(order);
    }

    @PostMapping("/submit")
    public Result<OrderSubmitVO> submit(@RequestBody OrderDTO orderDTO){
        log.info("提交订单：{}",orderDTO);
        OrderSubmitVO orderSubmitVO=orderService.submit(orderDTO);
        return Result.success(orderSubmitVO);
    }

    @PutMapping("/payment")
    public Result payment(@RequestBody OrdersPaymentDTO ordersPaymentDTO){
        log.info("支付订单：{}",ordersPaymentDTO);
        orderService.payment(ordersPaymentDTO);
        return Result.success();
    }

    @GetMapping("/pending")
    public Result<Double> pending(@RequestBody PendingOrdersDTO pendingOrdersDTO){
        log.info("改签or退款：{}",pendingOrdersDTO);
        Double serviceFee=orderService.pending(pendingOrdersDTO);
        return Result.success(serviceFee);
    }

    public Result confirmed(@RequestBody ConfirmedOrdersDTO confirmedOrdersDTO){
        log.info("确认改签or退款：{}",confirmedOrdersDTO);
        orderService.confirmed(confirmedOrdersDTO);
        return Result.success();
    }
}
