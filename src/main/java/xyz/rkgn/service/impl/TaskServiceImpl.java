package xyz.rkgn.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.rkgn.common.Result;
import xyz.rkgn.dto.TaskQueryDto;
import xyz.rkgn.entity.Task;
import xyz.rkgn.mapper.TaskMapper;
import xyz.rkgn.service.StudentTaskService;
import xyz.rkgn.service.TaskService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;

/**
 * @author ljx
 * @description 针对表【tb_task(任务)】的数据库操作Service实现
 * @createDate 2024-02-17 14:26:55
 */
@Slf4j
@Service
public class TaskServiceImpl extends ServiceImpl<TaskMapper, Task> implements TaskService {
    // 每页固定10条数据
    private static final Integer PAGE_SIZE = 10;

    private final StudentTaskService studentTaskService;

    public TaskServiceImpl(StudentTaskService studentTaskService) {
        this.studentTaskService = studentTaskService;
    }


    /**
     * 新增任务
     * @param task 封装任务数据的实体对象
     * @return 操作结果的Result对象（包含新增任务的ID）
     */
    @Override
    @Transactional
    public Result addTask(Task task) {
        boolean saved = save(task);
        if (!saved) return Result.fail("任务创建失败");
        // 若任务状态为发布状态 则关联学生
        if (task.getIsPublished() == 1)  studentTaskService.linkStudentTask(task);
        return Result.ok("任务创建成功");
    }

    /**
     * 根据ID删除任务（
     * @param id 任务ID
     * @return 操作结果的Result对象
     */
    @Override
    @Transactional
    public Result deleteTaskById(Long id) {
        // 使用 studentTaskService 删除学生任务关联记录
        studentTaskService.removeByTaskId(id);
        boolean removed = removeById(id);
        return removed ? Result.ok("删除成功") : Result.fail("任务不存在或已删除");
    }

    /**
     * 条件查询任务（支持多字段匹配）
     * @param queryDTO 封装查询条件的实体对象
     * @return 包含任务列表的Result对象
     */
    @Override
    public Result page(TaskQueryDto queryDTO, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<Task> queryWrapper = new LambdaQueryWrapper<>();

        if (queryDTO.getId() != null) { queryWrapper.eq(Task::getId, queryDTO.getId());}
        if (queryDTO.getTitle() != null && !queryDTO.getTitle().trim().isEmpty()) { queryWrapper.like(Task::getTitle, queryDTO.getTitle().trim());}
        if (queryDTO.getIsPublished() != null) {    queryWrapper.eq(Task::getIsPublished, queryDTO.getIsPublished());}

        //转换格式
        List<DateTimeFormatter> formatters = Arrays.asList(
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"),
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")
        );

        LocalDateTime queryStart = null;
        if (queryDTO.getCreateTime() != null && !queryDTO.getCreateTime().isEmpty()) {
            queryStart = parseDateTime(queryDTO.getCreateTime(), formatters);
        }

        LocalDateTime queryEnd = null;
        if (queryDTO.getDeadline() != null && !queryDTO.getDeadline().isEmpty()) {
            queryEnd = parseDateTime(queryDTO.getDeadline(), formatters);
        }

        if (queryStart != null) {   queryWrapper.ge(Task::getCreateTime,    queryStart);}
        if (queryEnd != null)   {   queryWrapper.le(Task::getDeadline,      queryEnd);}

        Page<Task> page = new Page<>(pageNum, pageSize);
        baseMapper.selectPage(page, queryWrapper);
        return Result.ok(page);
    }



    // 辅助方法：解析时间字符串
    private LocalDateTime parseDateTime(String timeStr, List<DateTimeFormatter> formatters) {
        for (DateTimeFormatter formatter : formatters) {
            try {
                return LocalDateTime.parse(timeStr, formatter);
            } catch (DateTimeParseException ignored) {
            }
        }
        log.warn("无法解析时间格式: {}");
        return null;
    }

    /**
     * 更新任务
     * @param task 封装更新数据的实体对象（必须包含ID）
     * @return 操作结果的Result对象
     */
    @Override
    public Result updateTask(Task task) {
        boolean updated = updateById(task);
        if (!updated) {
            return Result.fail("任务id不存在");
        }
        return Result.ok();
    }

    /**
     * 分页查询所有任务（每页10条）
     * @param pageNum 页码（默认第1页）
     * @return 包含分页数据的Result对象
     */
    @Override
    public Result pageAllTasks(Integer pageNum) {
        int currentPage = (pageNum == null || pageNum < 1) ? 1 : pageNum;

        Page<Task> page = new Page<>(currentPage, PAGE_SIZE);

        LambdaQueryWrapper<Task> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Task::getCreateTime);
        IPage<Task> taskPage = baseMapper.selectPage(page, queryWrapper);

        return Result.ok(taskPage);
    }
}