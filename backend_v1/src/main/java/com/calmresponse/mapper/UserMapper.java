package com.calmresponse.mapper;

import com.calmresponse.dto.UserPageQueryDTO;
import com.calmresponse.entity.User;
import com.calmresponse.vo.UserVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface UserMapper {
    //管理员查询所有用户信息
    public List<UserVO> pageQuery(UserPageQueryDTO userPageQueryDTO);

    void update(User user);

    @Update("update user set balance = #{balance} where username = #{username}")
    void updateBalance(@Param("balance") Double balance, @Param("username") String username);

    @Select("select balance from user where username = #{username}")
    Double getBalance(String username);
}
