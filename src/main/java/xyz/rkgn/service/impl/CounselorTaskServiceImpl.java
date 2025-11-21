package xyz.rkgn.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.rkgn.common.Result;
import org.springframework.data.redis.core.StringRedisTemplate;
import xyz.rkgn.dto.ClassTaskStatsDto;
import xyz.rkgn.dto.CounselorTaskStatusDto;
import xyz.rkgn.entity.Counselor;
import xyz.rkgn.entity.StudentClass;
import xyz.rkgn.entity.StudentTask;
import xyz.rkgn.entity.Task;
import xyz.rkgn.mapper.CounselorMapper;
import xyz.rkgn.mapper.StudentClassMapper;
import xyz.rkgn.mapper.StudentTaskMapper;
import xyz.rkgn.mapper.TaskMapper;
import xyz.rkgn.service.CounselorTaskService;
import xyz.rkgn.utils.LoginUserUtils;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author SuohaChan
 * @data 2025/9/10
 */

@Service
@Transactional
@Slf4j
public class CounselorTaskServiceImpl extends ServiceImpl<TaskMapper, Task> implements CounselorTaskService {

    private final CounselorMapper counselorMapper;
    private final StudentClassMapper studentClassMapper;
    private final StudentTaskMapper studentTaskMapper;
    private final TaskMapper taskMapper;
    private final StringRedisTemplate stringRedisTemplate;

    public CounselorTaskServiceImpl(CounselorMapper counselorMapper, StudentClassMapper studentClassMapper, StudentTaskMapper studentTaskMapper, TaskMapper taskMapper, StringRedisTemplate stringRedisTemplate) {
        this.counselorMapper = counselorMapper;
        this.studentClassMapper = studentClassMapper;
        this.studentTaskMapper = studentTaskMapper;
        this.taskMapper = taskMapper;
        this.stringRedisTemplate = stringRedisTemplate;
    }


    /**
     * 查看辅导员发布最新任务的完成情况
     */
    @Override
    public Result getClassTask() {
        //获得当前登录辅导员信息
        Long counselorId = LoginUserUtils.getCurrentUserId(stringRedisTemplate);
        Counselor counselor = counselorMapper.selectById(counselorId);
        if (counselor == null) {
            return Result.fail("辅导员信息不存在");
        }

        //查询辅导员发布的最新（最晚创建）的已发布任务
        LambdaQueryWrapper<Task> taskWrapper = new LambdaQueryWrapper<>();
        taskWrapper.eq(Task::getTeacherId, counselorId)
                .eq(Task::getIsPublished, 1)  // 只看已发布任务
                .orderByDesc(Task::getCreateTime)  // 按创建时间倒序
                .last("LIMIT 1");  // 只取最新的一条

        Task latestTask = taskMapper.selectOne(taskWrapper);
        if (latestTask == null) {
            return Result.fail("未查询到您发布的任务");
        }

        Long taskId = latestTask.getId();
        String taskLevel = latestTask.getLevel();
        String taskCollege = latestTask.getCollege();
        String taskClazz = latestTask.getClazz();

        // 查询与该任务关联的所有学生任务
        List<StudentTask> studentTasks = studentTaskMapper.selectList(
                new LambdaQueryWrapper<StudentTask>().eq(StudentTask::getTaskId, taskId)
        );
        if (studentTasks.isEmpty())
            return Result.ok(new ArrayList<ClassTaskStatsDto>());

        // 提取学生ID列表
        List<Long> studentIds = studentTasks.stream()
                .map(StudentTask::getStudentId)
                .collect(Collectors.toList());

        // 查询这些学生的班级信息
        LambdaQueryWrapper<StudentClass> studentClassWrapper = new LambdaQueryWrapper<>();
        studentClassWrapper.in(StudentClass::getStudentId, studentIds);

        // 根据任务级别过滤班级范围
        if ("院级".equals(taskLevel)) {
            studentClassWrapper.eq(StudentClass::getCollege, taskCollege);
        } else if ("班级".equals(taskLevel)) {
            studentClassWrapper.eq(StudentClass::getCollege, taskCollege)
                    .eq(StudentClass::getClazz, taskClazz);
        }
        // 校级任务不需要额外过滤条件

        List<StudentClass> studentClasses = studentClassMapper.selectList(studentClassWrapper);

        // 建立学生ID到班级信息的映射
        Map<Long, StudentClass> studentClassMap = studentClasses.stream()
                .collect(Collectors.toMap(StudentClass::getStudentId, sc -> sc));

        // 按班级分组统计
        Map<String, List<StudentTask>> classGroupMap = new HashMap<>();

        for (StudentTask studentTask : studentTasks) {
            StudentClass studentClass = studentClassMap.get(studentTask.getStudentId());
            if (studentClass != null) {
                // 创建班级唯一标识
                String classKey = studentClass.getCollege() + "|" + studentClass.getClazz();
                classGroupMap.computeIfAbsent(classKey, k -> new ArrayList<>()).add(studentTask);
            }
        }

        // 统计每个班级的任务完成情况
        List<ClassTaskStatsDto> statsList = new ArrayList<>();

        for (Map.Entry<String, List<StudentTask>> entry : classGroupMap.entrySet()) {
            String classKey = entry.getKey();
            List<StudentTask> classStudentTasks = entry.getValue();

            String[] parts = classKey.split("\\|");
            String college = parts[0];
            String clazz = parts[1];

            ClassTaskStatsDto stats = new ClassTaskStatsDto();
            stats.setCollege(college);
            stats.setClazz(clazz);
            stats.setTaskId(taskId);
            stats.setTaskTitle(latestTask.getTitle());

            long totalStudents = classStudentTasks.size();
            stats.setTotalStudents(totalStudents);

            long completedStudents = classStudentTasks.stream()
                    .filter(st -> st.getStatus() != null && st.getStatus() == 1)
                    .count();

            stats.setCompletedStudents(completedStudents);
            stats.setUncompletedStudents(totalStudents - completedStudents);

            // 计算完成率（保留两位小数）
            double rate = totalStudents > 0 ? (double) completedStudents / totalStudents * 100 : 0;
            stats.setCompletionRate(Math.round(rate * 100) / 100.0);

            statsList.add(stats);
        }

        // 按学院和班级排序
        statsList.sort(Comparator.comparing(ClassTaskStatsDto::getCollege).thenComparing(ClassTaskStatsDto::getClazz));

        return Result.ok(statsList);
    }

    /**
     * 查看辅导员发布的任务状态统计（仅含数量）
     */
    @Override
    public Result getTaskStatus() {
        Long counselorId = LoginUserUtils.getCurrentUserId(stringRedisTemplate);
        if (counselorId == null)
            return Result.fail("请先登录");
        LambdaQueryWrapper<Task> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Task::getTeacherId, counselorId)
                .eq(Task::getIsPublished, 1);

        List<Task> tasks = taskMapper.selectList(queryWrapper);
        if (tasks.isEmpty())
            return Result.ok(new CounselorTaskStatusDto(0, 0, 0));

        //计算任务状态
        LocalDateTime now = LocalDateTime.now();
        long normalCount = 0; //正常进行
        long soonExpireCount = 0;//快要过期(24 小时)
        long expiredCount = 0; //已过期

        for (Task task : tasks) {
            LocalDateTime deadline = task.getDeadline();
            if (deadline == null){
                normalCount++;
                continue;
            }
            if (now.isAfter( deadline))
                expiredCount++;
            else if(ChronoUnit.HOURS.between(now,deadline) <= 24)
                soonExpireCount++;
            else
                normalCount++;
        }

        return Result.ok(new CounselorTaskStatusDto(normalCount, soonExpireCount, expiredCount));
    }

    @Override
    public Result getTaskByUserId(Long userId) {
        LambdaQueryWrapper<Task> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Task::getTeacherId, userId)
                .eq(Task::getIsPublished, 1);

        List<Task> tasks = taskMapper.selectList(queryWrapper);
        return Result.ok(tasks);
    }
}



