package com.calmresponse.mapper;

import com.calmresponse.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserMapper {
    //管理员查询所有用户信息
    @Select("select * from user;")
    public List<User> selectAll();

    void update(User user);
}
