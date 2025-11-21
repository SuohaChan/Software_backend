package xyz.rkgn.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * @author SuohaChan
 * @data 2025/9/2
 */
@Data
public class CheckInWeekDto {
    //今日签到状态
   private boolean todayChecked;

   //本周七天内的签到状态
    private List<Boolean> weekCheckStatus;
}