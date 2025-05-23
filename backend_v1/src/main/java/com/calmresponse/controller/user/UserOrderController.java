package com.calmresponse.controller.user;

import com.calmresponse.common.ErrorCode;
import com.calmresponse.constant.UserConstant;
import com.calmresponse.dto.*;
import com.calmresponse.entity.Orders;
import com.calmresponse.exception.BusinessException;
import com.calmresponse.result.Result;
import com.calmresponse.service.OrderService;
import com.calmresponse.vo.OrderSubmitVO;
import com.calmresponse.vo.PendingVO;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


@RestController
@Slf4j
@RequestMapping("/user/order")
@CrossOrigin(origins = "http://localhost:9999", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS}, allowCredentials = "true")
public class UserOrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/page")
    public Result<PageInfo<Orders>> pageQuery(@RequestBody OrderPageQueryDTO orderPageQueryDTO, HttpServletRequest request) {
        // 从 session 中获取用户信息
        Object userObj = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATUS);
        if (userObj == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN, "用户未登录");
        }
        log.info("用户根据乘车人分页查询所有订单:{}",orderPageQueryDTO);
        UserDTO userDTO = (UserDTO) userObj;
        String username = userDTO.getUsername();
        orderPageQueryDTO.setUsername(username);
        PageInfo<Orders> order = orderService.pageQuery(orderPageQueryDTO);
        return Result.success(order);
    }

    @PostMapping("/submit")
    public Result<OrderSubmitVO> submit(@RequestBody OrderDTO orderDTO,HttpServletRequest request){
        // 从 session 中获取用户信息
        Object userObj = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATUS);
        if (userObj == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN, "用户未登录");
        }
        log.info("提交订单：{}",orderDTO);
        UserDTO userDTO = (UserDTO) userObj;
        String username = userDTO.getUsername();
        orderDTO.setUsername(username);
        OrderSubmitVO orderSubmitVO=orderService.submit(orderDTO);
        return Result.success(orderSubmitVO);
    }

    @PutMapping("/payment")
    public Result payment(@RequestBody OrdersPaymentDTO ordersPaymentDTO){
        log.info("支付订单：{}",ordersPaymentDTO);
        orderService.payment(ordersPaymentDTO);
        return Result.success();
    }

    @PutMapping("/pending")
    public Result<PendingVO> pending(@RequestBody PendingOrdersDTO pendingOrdersDTO){
        log.info("退款：{}",pendingOrdersDTO);
        PendingVO pendingVO=orderService.pending(pendingOrdersDTO);
        return Result.success(pendingVO);
    }

    @PutMapping("/confirmed")
    public Result confirmed(@RequestBody ConfirmedOrdersDTO confirmedOrdersDTO){
        log.info("确认退款：{}",confirmedOrdersDTO);
        orderService.confirmed(confirmedOrdersDTO);
        return Result.success();
    }
}
