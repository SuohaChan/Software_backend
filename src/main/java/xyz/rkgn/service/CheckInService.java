package xyz.rkgn.service;

import com.baomidou.mybatisplus.extension.service.IService;
import xyz.rkgn.common.Result;
import xyz.rkgn.entity.CheckIn;

public interface CheckInService extends IService<CheckIn> {

    Result checkInToday(Long studentId);

    Result getWeekCheckStatus(Long studentId);
}
