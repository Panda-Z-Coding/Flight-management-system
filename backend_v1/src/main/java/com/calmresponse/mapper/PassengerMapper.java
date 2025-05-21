package com.calmresponse.mapper;

import com.calmresponse.dto.PageQueryDTO;
import com.calmresponse.dto.PassengerPageQueryDTO;
import com.calmresponse.entity.Passenger;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PassengerMapper {
    void insert(Passenger passenger);

    @Select("select * from passenger")
    List<Passenger> selectAll();


    @Delete("delete from passenger where id=#{id}")
    void deleteById(Long id);

    void update(Passenger passenger);

    List<Passenger> pageQuery(PageQueryDTO queryDTO);
}
