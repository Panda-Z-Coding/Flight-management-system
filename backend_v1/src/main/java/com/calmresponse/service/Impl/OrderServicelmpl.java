package com.calmresponse.service.Impl;

import com.calmresponse.mapper.UserMapper;
import com.calmresponse.vo.PendingVO;
import com.github.pagehelper.PageHelper;
import com.calmresponse.dto.*;
import com.calmresponse.entity.Flight;
import com.calmresponse.entity.Orders;
import com.calmresponse.mapper.FlightMapper;
import com.calmresponse.mapper.OrdersMapper;
import com.calmresponse.service.OrderService;
import com.calmresponse.vo.OrderSubmitVO;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderServicelmpl implements OrderService{

    @Autowired
    private OrdersMapper orderMapper;
    @Autowired
    private FlightMapper flightMapper;
    @Autowired
    private UserMapper userMapper;
    //添加订单
    public void createOrder(OrderDTO orderDTO) {
        //创建订单，拷贝
        Orders order = new Orders();
        BeanUtils.copyProperties(orderDTO, order);
        //创建时间，修改时间
        order.setCreateTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        //默认情况
        order.setIsDelete((byte)0);
        //mapper中insert
        orderMapper.insert(order);
    }

    //根据ids批量删除订单
    public void deleteOrder(List<Long> ids) {
        for (Long id : ids) {
            orderMapper.deleteById(id);
        }
    }

    //用户根据用户名分页查询所有订单
    //管理员根据订单号分页查询所有订单
    public PageInfo<Orders> pageQuery(OrderPageQueryDTO orderPageQueryDTO) {
        // 开启分页
        PageHelper.startPage(orderPageQueryDTO.getPage(), orderPageQueryDTO.getPageSize());
        // 调用 mapper 层方法进行查询
        List<Orders> orders = orderMapper.pageQuery(orderPageQueryDTO);
        return new PageInfo<>(orders);
    }

/*    //管理员根据订单号分页查询所有订单
    public PageResult page(OrderPageQueryDTO orderPageQueryDTO) {
        //设置分页参数
        PageHelper.startPage(orderPageQueryDTO.getPage(), orderPageQueryDTO.getPageSize());
        Page<Orders> list=orderMapper.page(orderPageQueryDTO);
        return new PageResult(list.getTotal(),list.getResult());//查询总记录数、当前页数据集合
    }*/

   /* //查询该用户的订单信息信息
    public List<Orders> getByUserName(String username)  {
        return orderMapper.getByUserName(username);
    }

*/    //提交订单
    public OrderSubmitVO submit(OrderDTO orderDTO) {
        //创建订单，拷贝
        Orders order = new Orders();
        BeanUtils.copyProperties(orderDTO, order);
        //生成订单号
        order.setOrderNumber(System.currentTimeMillis() + "" + (int) (Math.random() * 10000));
        //创建时间，修改时间
        order.setCreateTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        //修改订单状态为已支付
        order.setStatus(0);
        //默认情况
        order.setServiceFee(0.0);//手续费
        order.setIsDelete((byte)0);
        order.setPaymentMethod("支付宝");//默认支付方式
        //mapper中insert
        orderMapper.insert(order);

        //返回订单提交信息
        OrderSubmitVO orderSubmitVO = new OrderSubmitVO();
        orderSubmitVO.setOrderNumber(order.getOrderNumber());
        orderSubmitVO.setPrice(order.getPrice());
        orderSubmitVO.setOrderTime(LocalDateTime.now());
        //支付时间倒计时先忽略
        return orderSubmitVO;
    }

    //支付订单
    @Transactional
    //涉及user表余额修改和orders表订单状态修改，开启事务注解
    public void payment(OrdersPaymentDTO ordersPaymentDTO) {
        //根据订单号查询订单
        Orders order = orderMapper.getByOrderNumber(ordersPaymentDTO.getOrderNumber());
        //如果订单存在
        if (order != null) {
            //修改用户余额
            String username = order.getUsername();
            //判断用户余额是否充足
            Double balance = userMapper.getBalance(username);
            if(balance<order.getPrice()){
                throw new RuntimeException("余额不足");
            }
            userMapper.updateBalance(balance-order.getPrice(), username);
            //修改订单状态为已支付
            order.setStatus(1);
            //修改支付时间
            order.setUpdateTime(LocalDateTime.now());
            //修改支付方式
            order.setPaymentMethod(ordersPaymentDTO.getPaymentMethod());
            orderMapper.update(order);
        }
    }

    //退款
    public PendingVO pending(PendingOrdersDTO pendingOrdersDTO) {
        //根据id查询订单
        Orders order = orderMapper.getById(pendingOrdersDTO.getOrderId());
        Double serviceFee = order.getServiceFee();
        //如果订单存在
        if (order!= null) {
            //修改orderUpdateTime
            order.setUpdateTime(LocalDateTime.now());
            //order.setStatus(2);还无法退款，得等确认退款
                /*全额退款or部分退款（手续费）
                航班起飞时间-修改订单时间
                起飞前7天外手续费20%(头等舱前7天免收费)，2-7天40%,4h-48h 60%*/
            String flightNumber = order.getFlightNumber();
            Flight flight =flightMapper.getByFlightNumber(flightNumber);
            LocalDateTime departureTime = flight.getDepartureTime();
            LocalDateTime updateTime = order.getUpdateTime();
            long hours = Duration.between(updateTime,departureTime).toHours();
            //long hours = Duration.between(departureTime,updateTime).toHours();
            if(hours>7*24){
                serviceFee=order.getPrice()*0.2;
            }else if(hours>48 && hours<=7*24){
                serviceFee=order.getPrice()*0.4;
            }else if(hours>4 && hours<=48){
                serviceFee=order.getPrice()*0.6;
            }else {
                //该航班已启程，退款失败
                throw new RuntimeException("该航班已启程，退款失败");
            }
            order.setServiceFee(serviceFee);
        }
        PendingVO pendingVO = new PendingVO();
        BeanUtils.copyProperties(order, pendingVO);
        return pendingVO;
    }

    //确认退款
    @Transactional
    public void confirmed(ConfirmedOrdersDTO confirmedOrdersDTO) {
        Orders order = orderMapper.getByOrderNumber(confirmedOrdersDTO.getOrderNumber());
        //如果订单存在
        if (order!= null) {
            //已经退款过的订单就没有退款这一说了，抛异常
            //否则，修改订单状态为已退款，修改手续费
            if (order.getStatus()==2) {
                throw new RuntimeException("该订单已经退款过了");
            }else {
                order.setStatus(2);
                order.setServiceFee(confirmedOrdersDTO.getServiceFee());
                order.setUpdateTime(LocalDateTime.now());
                orderMapper.update(order);
                //修改用户余额
                String username = order.getUsername();
                Double balance = userMapper.getBalance(username);
                userMapper.updateBalance(balance+order.getPrice()-order.getServiceFee(), username);
            }
        }
    }

    //根据id修改订单
    public void updateOrder(OrderDTO orderDTO) {
        Orders order = new Orders();
        BeanUtils.copyProperties(orderDTO, order);
        orderMapper.update(order);
    }

}
