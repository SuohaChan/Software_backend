package xyz.rkgn.service;

import xyz.rkgn.common.Result;
import xyz.rkgn.dto.TaskQueryDto;
import xyz.rkgn.entity.Task;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author ljx
* @description 针对表【tb_task(任务)】的数据库操作Service
* @createDate 2024-02-17 14:26:55
*/
public interface TaskService extends IService<Task> {
    /**
     * 根据ID删除任务
     */
    Result deleteTaskById(Long ids);

    /**
     * 更新任务
     */
    Result updateTask(Task task);

    /**
     * 新增任务
     */
    Result addTask(Task task);

    /**
     * 分页查询所有任务（每页10条）
     */
    Result pageAllTasks(Integer pageNum);

    /**
     * 条件查询任务
     */
   Result page(TaskQueryDto queryDTO, Integer pageNum, Integer pageSize);


}
