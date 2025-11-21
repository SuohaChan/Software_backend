package xyz.rkgn.controller;

import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import xyz.rkgn.annotation.mySystemLog;
import xyz.rkgn.common.Result;
import xyz.rkgn.service.StudentTaskService;
import xyz.rkgn.service.TaskService;

@RestController
@RequestMapping("studentTask")
public class StudentTaskController {

    @Resource
    private StudentTaskService studentTaskService;

    @Resource
    private TaskService taskService;
    /**
     * 通过任务类型查询任务
     * 通过token获取学生信息，根据学生信息获取任务
     * @param type 任务类型
     * @return Result对象
     */
    @GetMapping("/by-type")
    @mySystemLog(xxbusinessName = "通过任务类型查询任务")
    public Result getTasksByType(@RequestParam String type){
        return studentTaskService.getTasksByType(type);
    }
}
