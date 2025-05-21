package com.calmresponse.entity;

import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookedSeat implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id; // 主键ID
    private String flightNumber; // 航班号
    private String seatType; // 座位类型（头等舱/经济舱等）
    private String seatNumber; // 座位号
    private LocalDateTime createTime; // 创建时间
    private LocalDateTime updateTime; // 修改时间
    @TableLogic
    private Byte isDelete; // 逻辑删除标记（0未删除 1已删除）
}
