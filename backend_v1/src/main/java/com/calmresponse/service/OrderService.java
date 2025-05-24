package com.calmresponse.service;



import com.calmresponse.dto.*;
import com.calmresponse.entity.Orders;
import com.calmresponse.result.PageResult;
import com.calmresponse.vo.OrderSubmitVO;
import com.calmresponse.vo.PendingVO;
import com.github.pagehelper.PageInfo;

import java.util.List;

public interface OrderService {
    //用户根据用户名分页查询所有订单
    //管理员根据订单号分页查询所有订单
    PageInfo<Orders> pageQuery(OrderPageQueryDTO orderPageQueryDTO);

    //提交订单
    OrderSubmitVO submit(OrderDTO orderDTO);

    //支付订单
    void payment(OrdersPaymentDTO ordersPaymentDTO);

/*
    //管理员对订单分页查询
    PageResult page(OrderPageQueryDTO orderPageQueryDTO);*/

    //改签or退款
    PendingVO pending(PendingOrdersDTO pendingOrdersDTO);

    //确认退款
    void confirmed(ConfirmedOrdersDTO confirmedOrdersDTO);
}
