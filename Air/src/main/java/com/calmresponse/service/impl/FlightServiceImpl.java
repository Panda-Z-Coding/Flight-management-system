package com.calmresponse.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.calmresponse.Utils.Utils;
import com.calmresponse.common.ErrorCode;
import com.calmresponse.exception.BusinessException;
import com.calmresponse.model.DTO.FlightDTO;
import com.calmresponse.model.domain.Flight;
import com.calmresponse.service.FlightService;
import com.calmresponse.mapper.FlightMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
* @author yufeng
* @description 针对表【flight(航班表)】的数据库操作Service实现
 * 部分需要管理员权限
* @createDate 2025-05-15 18:56:01
*/
@Service
public class FlightServiceImpl extends ServiceImpl<FlightMapper, Flight>
    implements FlightService{
    @Autowired
    private FlightMapper flightMapper;

    //添加航班
    @Override
    public Flight createFlight(FlightDTO flightDTO, HttpServletRequest request) {
        if (!Utils.isAuth(request)){
            throw new BusinessException(ErrorCode.NO_AUTH,"无管理员权限");
        }
        //创建flight，拷贝
        Flight flight = new Flight();
        BeanUtils.copyProperties(flightDTO, flight);
        //mapper中insert
        boolean save = this.save(flight);
        if (!save){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"添加航班失败");
        }
        return flight;
    }

    //根据ids批量删除航班
    @Override
    public List<Long> deleteFlight(List<Long> ids, HttpServletRequest request) {
        if (!Utils.isAuth(request)){
            throw new BusinessException(ErrorCode.NO_AUTH,"无管理员权限");
        }
        //状态为1的航班表示在飞行，不能删除
        for (Long id : ids) {
            Flight flight = this.getById(id);
            if (flight.getStatus() == 1) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "该航班正在飞行，不能删除");
            }
        }
            boolean remove = this.removeByIds(ids);
            if (!remove){
                throw new BusinessException(ErrorCode.SYSTEM_ERROR,"删除失败");
            }
            return ids;

    }

    //查询所有航班
    @Override
    public List<Flight> selectAll() {
        return this.list();
    }

    //根据航班号查询航班


    //根据id修改航班信息
    @Override
    public Boolean updateFlight(FlightDTO flightDTO,HttpServletRequest request) {
        Flight flight = new Flight();
        BeanUtils.copyProperties(flightDTO, flight);
        return this.updateById(flight);
    }
//    /**
//     * 修改航班
//     * @param flightDTO
//     * @param request
//     * @return
//     * @throws ParseException
//     */
//    @Override
//    public Flight updateFlight(@RequestBody FlightDTO flightDTO, HttpServletRequest request) throws ParseException {
//        if (!Utils.isAuth(request)){
//            throw new BusinessException(ErrorCode.NO_AUTH,"无管理员权限");
//        }
//        //判断到达时间和起飞时间的关系
//        String departureTime = flightDTO.getDepartureTime().toString();
//        String arrivalTime = flightDTO.getArrivalTime().toString();
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        Date departure = sdf.parse(departureTime);
//        Date arrival = sdf.parse(arrivalTime);
//        if(arrival.before(departure)){
//            throw new BusinessException(ErrorCode.PARAMS_ERROR,"到达时间不能早于出发时间");
//        }
//        //将DTO转为Flight
//        Flight flight=setFlightByDTO(flightDTO);
//        flight.setDepartureTime(departure);
//        flight.setArrivalTime(arrival);
//
//        boolean updated = this.updateById(flight);
//        if(!updated){
//            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"修改失败");
//        }
//        return flight;
//    }
//
//    private Flight setFlightByDTO(FlightDTO flightDTO){
//        Flight flight = new Flight();
//        flight.setId(flightDTO.getId());
//        flight.setFlightNumber(flightDTO.getFlightNumber());
//        flight.setAircraftModel(flightDTO.getAircraftModel());
//        flight.setSeatLayout(flightDTO.getSeatLayout());
//        flight.setDepartureCity(flightDTO.getDepartureCity());
//        flight.setArrivalCity(flightDTO.getArrivalCity());
//        flight.setPrice(flightDTO.getPrice());
//        flight.setTotalSeats(flightDTO.getTotalSeats());
//        flight.setRemainingSeats(flightDTO.getRemainingSeats());
//        flight.setAirline(flightDTO.getAirline());
//        return flight;
//
//
//
//    }
}




