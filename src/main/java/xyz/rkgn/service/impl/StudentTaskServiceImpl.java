package xyz.rkgn.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import xyz.rkgn.common.Result;
import xyz.rkgn.dto.TaskStatusDto;
import xyz.rkgn.entity.StudentClass;
import xyz.rkgn.entity.StudentTask;
import xyz.rkgn.entity.Task;
import xyz.rkgn.mapper.StudentClassMapper;
import xyz.rkgn.mapper.TaskMapper;
import xyz.rkgn.service.StudentTaskService;
import xyz.rkgn.mapper.StudentTaskMapper;
import org.springframework.stereotype.Service;
import xyz.rkgn.utils.LoginUserUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
* @author ljx
* @description 针对表【tb_student_task(学生和任务关系)】的数据库操作Service实现
* @createDate 2024-02-28 17:59:39
*/

@Slf4j
@Service
public class StudentTaskServiceImpl extends ServiceImpl<StudentTaskMapper, StudentTask> implements StudentTaskService{

    private final StudentClassMapper studentClassMapper;

    private final StringRedisTemplate stringRedisTemplate;

    private final TaskMapper taskMapper;

    private final StudentTaskMapper studentTaskMapper;


    public StudentTaskServiceImpl(StringRedisTemplate stringRedisTemplate,
                                  StudentClassMapper studentClassMapper,
                                  TaskMapper taskMapper,
                                  StudentTaskMapper studentTaskMapper) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.studentClassMapper = studentClassMapper;
        this.taskMapper = taskMapper;
        this.studentTaskMapper = studentTaskMapper;
    }

    /**
     * 根据任务类型获取任务列表
     * @param type 任务类型（"校级"、"院级"、"班级"）
     * @return 包含任务列表的Result对象
     */
    @Override
    public Result getTasksByType(String type) {
        Long studentId = LoginUserUtils.getCurrentUserId(stringRedisTemplate);
        log.info("学生ID: {}", studentId);

        StudentClass studentClass = studentClassMapper.selectOne(
                Wrappers.<StudentClass>lambdaQuery()
                        .eq(StudentClass::getStudentId, studentId)
        );
        if (studentClass == null) return Result.fail("学生班级学院信息不存在");

        String studentCollege = studentClass.getCollege();
        String studentClazz = studentClass.getClazz();

        List<Task> tasks;
        LambdaQueryWrapper<Task> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Task::getIsPublished, 1); // 只查已发布任务

        switch (type) {
            case "校级":
                queryWrapper.eq(Task::getLevel, "校级");
                tasks = taskMapper.selectList(queryWrapper);
                break;
            case "院级":
                queryWrapper.eq(Task::getLevel, "院级")
                        .eq(Task::getCollege, studentCollege);
                tasks = taskMapper.selectList(queryWrapper);
                break;
            case "班级":
                queryWrapper.eq(Task::getLevel, "班级")
                        .eq(Task::getClazz, studentClazz);
                tasks = taskMapper.selectList(queryWrapper);
                break;
            case "未完成":
                tasks =baseMapper.selectUnfinishedTasks(studentId, studentCollege, studentClazz);
                break;
            default:
                return Result.fail("无效的任务类型");
        }

        List<TaskStatusDto> taskDTOs = new ArrayList<>(tasks.stream().map(task -> {
                    TaskStatusDto dto = new TaskStatusDto();
                    BeanUtils.copyProperties( task,dto);
                    Integer status = baseMapper.getTaskStatus(studentId, task.getId());
                    log.info("任务ID: {}, 学生ID: {}, 状态: {}", task.getId(), studentId, status);
                    dto.setCompleted(status != null && status == 1);
                    return dto;
                })
                .toList());

        if (!"未完成".equals(type)) {
            taskDTOs.sort((t1, t2) -> t2.getDeadline().compareTo(t1.getDeadline()));
        }

        return Result.ok(taskDTOs);
    }

    // 关联学生任务
    @Override
    public void linkStudentTask(Task task) {
        List<Long> targetStudentIds = getTargetStudentIds(task);
        if (targetStudentIds.isEmpty()) {
            log.warn("任务【{}】未找到匹配的学生", task.getId());
            return;
        }

        List<StudentTask> studentTasks = targetStudentIds.stream().map(studentId -> {
            StudentTask st = new StudentTask();
            st.setStudentId(studentId);
            st.setTaskId(task.getId());
            st.setStatus(0); // 0-未完成
            st.setCreateTime(LocalDateTime.now());
            st.setUpdateTime(LocalDateTime.now());
            return st;
        }).collect(Collectors.toList());

        studentTaskMapper.batchInsert(studentTasks);
        log.info("任务【{}】已关联 {} 名学生", task.getId(), studentTasks.size());
    }

    /**
     * 根据任务级别查询目标范围学生ID列表
     * 校级任务查询校级学生
     * 院级任务查询院级学生
     * 班级任务查询班级学生
     */
    @Override
    public List<Long> getTargetStudentIds(Task task) {
        String level = task.getLevel();
        LambdaQueryWrapper<StudentClass> queryWrapper = new LambdaQueryWrapper<>();

        switch (level) {
            case "校级":
                return studentClassMapper.selectList(queryWrapper).stream()
                        .map(StudentClass::getStudentId)
                        .collect(Collectors.toList());
            case "院级":
                queryWrapper.eq(StudentClass::getCollege, task.getCollege());
                break;
            case "班级":
                queryWrapper.eq(StudentClass::getCollege, task.getCollege())
                        .eq(StudentClass::getClazz, task.getClazz());
                break;
            default:
                log.error("无效的任务级别: {}");
                return Collections.emptyList();
        }
        return studentClassMapper.selectList(queryWrapper).stream()
                .map(StudentClass::getStudentId)
                .collect(Collectors.toList());
    }

    @Override
    public void removeByTaskId(Long id) {
        LambdaQueryWrapper<StudentTask> stQuery = new LambdaQueryWrapper<>();
        stQuery.eq(StudentTask::getTaskId, id);
        studentTaskMapper.delete(stQuery);
    }
}




