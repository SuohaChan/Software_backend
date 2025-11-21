package xyz.rkgn.service;

import xyz.rkgn.common.Result;
import xyz.rkgn.entity.StudentTask;
import com.baomidou.mybatisplus.extension.service.IService;
import xyz.rkgn.entity.Task;

import java.util.List;

/**
 * @author ljx
 * 学生和任务关系表(tb_student_task)的数据库操作Service接口
 * @since 2024-02-28
 */
public interface StudentTaskService extends IService<StudentTask> {
    /**
     * 根据任务类型获取任务列表
     *
     * @param type 任务类型（"校级"、"院级"、"班级"、"未完成"）
     * @return 包含任务列表的Result对象
     */
    Result getTasksByType(String type);

    /**
     * 关联学生任务
     *
     * @param task 任务对象
     */
    void linkStudentTask(Task task);

    /**
     * 根据任务级别查询目标范围学生ID列表
     *
     * @param task 任务对象
     * @return 学生ID列表
     */
    List<Long> getTargetStudentIds(Task task);

    /**
     * 根据任务ID删除关联关系
     *
     * @param id 任务ID
     */
    void removeByTaskId(Long id);
}
