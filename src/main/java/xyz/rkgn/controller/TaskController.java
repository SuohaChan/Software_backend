package xyz.rkgn.controller;

import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import xyz.rkgn.annotation.mySystemLog;
import xyz.rkgn.common.Result;
import xyz.rkgn.dto.TaskQueryDto;
import xyz.rkgn.entity.Task;
import xyz.rkgn.service.TaskService;

/**
 * 任务接口
 */
@RestController
@RequestMapping("task")
public class TaskController {
    @Resource
    private TaskService taskService;

    /**
     * 添加任务
     *
     * @param task 要添加的任务
     * @return Result对象
     */
    @PostMapping
    public Result addTask(@RequestBody Task task) {
        return taskService.addTask(task);
    }


    /**
     * 条件分页查询任务
     * @param queryDTO 任务查询条件
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @return 分页结果
     */
    @mySystemLog(xxbusinessName = "条件分页查询任务")
    @GetMapping("/page")
    public Result page(TaskQueryDto queryDTO, Integer pageNum, Integer pageSize) {
        return taskService.page(queryDTO, pageNum, pageSize);
    }
    /**
     * 修改任务
     *
     * @param task id必需，其他需要修改的字段不为空
     * @return Result对象
     */
    @PutMapping
    public Result updateTask(@RequestBody Task task) {
        return taskService.updateTask(task);
    }

    /**
     * 通过id删除任务
     *
     * @param ids 要删除的任务的id，可以有多个，用逗号分隔
     * @return Result对象
     */
    @DeleteMapping
    public Result deleteTaskById(@RequestParam("ids") Long ids) {
        return taskService.deleteTaskById(ids);
    }

}
