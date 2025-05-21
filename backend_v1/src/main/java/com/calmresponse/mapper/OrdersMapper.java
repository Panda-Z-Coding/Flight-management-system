package com.calmresponse.mapper;


import com.calmresponse.dto.OrderPageQueryDTO;
import com.calmresponse.dto.PageQueryDTO;
import com.calmresponse.entity.Orders;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface OrdersMapper {

    //用户添加订单
    void insert(Orders orders);

    //用户根据用户名分页查询所有订单
    //管理员根据订单号分页查询所有订单
    List<Orders> pageQuery(OrderPageQueryDTO orderPageQueryDTO);

/*    //根据用户名查询订单
    @Select("select * from orders where username=#{username}")
    List<Orders> getByUserName(String username);*/

    //根据id删除订单
    @Delete("delete from orders where id=#{id}")
    void deleteById(Long id);

    //修改订单
    void update(Orders orders);

    //根据订单号查询订单
    @Select("select * from orders where order_number=#{orderNumber}")
    Orders getByOrderNumber(String orderNumber);

    //根据id查询订单
    @Select("select * from orders where id=#{id}")
    Orders getById(Long id);

/*    //管理员分页查询所有订单
    Page<Orders> page(OrderPageQueryDTO orderPageQueryDTO);*/
}
