package com.calmresponse.mapper;

import com.calmresponse.dto.FlightPageQueryDTO;
import com.calmresponse.entity.Flight;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface FlightMapper {

    Boolean insert(Flight flight);

    @Select("select * from flight where id=#{id}")
    Flight getById(Long id);

    @Delete("delete from flight where id=#{id}")
    Boolean deleteById(Long id);

    Boolean update(Flight flight);

    @Select("select * from flight where flight_number=#{flightNumber}")
    Flight getByFlightNumber(String flightNumber);

    List<Flight> pageQuery(FlightPageQueryDTO queryDTO);
}
