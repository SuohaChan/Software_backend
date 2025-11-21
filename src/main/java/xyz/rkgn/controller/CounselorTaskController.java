package xyz.rkgn.controller;

import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xyz.rkgn.annotation.mySystemLog;
import xyz.rkgn.aspect.myLogAspect;
import xyz.rkgn.common.Result;
import xyz.rkgn.service.CounselorTaskService;

/**
 * @author SuohaChan
 * @data 2025/9/10
 */
@RestController
@RequestMapping("counselorTask")
public class CounselorTaskController {
    @Resource
    private CounselorTaskService counselorTaskService;
    @Autowired
    private xyz.rkgn.aspect.myLogAspect myLogAspect;

    /**
     * 查看辅导员发布最新任务各班的完成情况
     * 用于生成辅导员端教务中心运行总览处 各班完成任务人数图
     */
    @PostMapping("/class-completion")
    @mySystemLog(xxbusinessName = "查看辅导员发布最新任务的完成情况")
    public Result  getClassTaskCompletion() {
        return counselorTaskService.getClassTask();
    }

    /**
     * 查看辅导员发布的任务状态统计（仅含数量）
     * 用于生成辅导员端教务中心运行总览处图 任务完成状态图
     */
    @PostMapping("/task-status")
    @mySystemLog(xxbusinessName = "查看辅导员发布的任务状态统计")
    public Result getTaskStatus() {
        return counselorTaskService.getTaskStatus();
    }
    /**
     *辅导员端page3 显示任务列表
     */
    @GetMapping("/userId/{userId}")
    public Result getTaskByUserId(@PathVariable Long userId) {
        return counselorTaskService.getTaskByUserId(userId);
    }
}
