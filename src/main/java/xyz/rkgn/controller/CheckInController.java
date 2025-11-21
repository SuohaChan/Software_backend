package xyz.rkgn.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import xyz.rkgn.annotation.mySystemLog;
import xyz.rkgn.common.Result;
import xyz.rkgn.service.CheckInService;


/**
 * @author SuohaChan
 * @data 2025/9/1
 */
@RestController
@RequestMapping("/check-in")
public class CheckInController {
    @Autowired
    private CheckInService checkInService;
    @Autowired
    private xyz.rkgn.aspect.myLogAspect myLogAspect;

    /**
     * 签到
     *
     * @param studentId
     * @return
     */
    @PostMapping("/today/{studentId}")
    @Transactional
    @mySystemLog(xxbusinessName = "签到")
    public Result checkInToday(@PathVariable Long studentId) {
        return checkInService.checkInToday(studentId);
    }

    @GetMapping("/week/{studentId}")
    @mySystemLog(xxbusinessName = "获取本周签到状态")
    public Result getWeekCheckStatus(@PathVariable Long studentId){
        return checkInService.getWeekCheckStatus(studentId);
    }
}
