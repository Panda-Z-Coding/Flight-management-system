package com.calmresponse.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.calmresponse.model.domain.BookedSeat;
import com.calmresponse.service.BookedSeatService;
import com.calmresponse.mapper.BookedSeatMapper;
import org.springframework.stereotype.Service;

/**
* @author yufeng
* @description 针对表【booked_seat(已被预定的座位表)】的数据库操作Service实现
* @createDate 2025-05-15 18:56:04
*/
@Service
public class BookedSeatServiceImpl extends ServiceImpl<BookedSeatMapper, BookedSeat>
    implements BookedSeatService{

}




