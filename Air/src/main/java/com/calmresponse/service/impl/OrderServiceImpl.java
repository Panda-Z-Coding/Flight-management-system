package com.calmresponse.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.calmresponse.model.domain.Order;
import com.calmresponse.service.OrderService;
import com.calmresponse.mapper.OrderMapper;
import org.springframework.stereotype.Service;

/**
* @author yufeng
* @description 针对表【order(总订单表)】的数据库操作Service实现
* @createDate 2025-05-15 18:55:53
*/
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order>
    implements OrderService{

}




