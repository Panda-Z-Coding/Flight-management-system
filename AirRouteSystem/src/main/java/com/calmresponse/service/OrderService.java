package com.calmresponse.service;



import com.calmresponse.dto.*;
import com.calmresponse.entity.Orders;
import com.calmresponse.result.PageResult;
import com.calmresponse.vo.OrderSubmitVO;

import java.util.List;

public interface OrderService {
    //查询该用户的订单信息信息
    List<Orders> getByUserName(String username);

    //提交订单
    OrderSubmitVO submit(OrderDTO orderDTO);

    //支付订单
    void payment(OrdersPaymentDTO ordersPaymentDTO);


    //分页查询
    PageResult page(OrderPageQueryDTO orderPageQueryDTO);

    //改签or退款
    Double pending(PendingOrdersDTO pendingOrdersDTO);

    //确认改签or退款
    void confirmed(ConfirmedOrdersDTO confirmedOrdersDTO);
}
