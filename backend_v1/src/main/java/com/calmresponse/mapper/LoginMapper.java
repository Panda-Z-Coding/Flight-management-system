package com.calmresponse.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.calmresponse.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LoginMapper extends BaseMapper<User> {

}