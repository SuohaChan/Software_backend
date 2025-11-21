package xyz.rkgn.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.lang.UUID;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import xyz.rkgn.common.RedisConstants;
import xyz.rkgn.common.Result;
import xyz.rkgn.dto.LoginDto;
import xyz.rkgn.dto.RegisterDto;
import xyz.rkgn.dto.StudentDto;
import xyz.rkgn.dto.StudentShowDto;
import xyz.rkgn.entity.Student;
import xyz.rkgn.entity.StudentClass;
import xyz.rkgn.entity.StudentTask;
import xyz.rkgn.mapper.StudentClassMapper;
import xyz.rkgn.mapper.StudentMapper;
import xyz.rkgn.mapper.StudentTaskMapper;
import xyz.rkgn.mapper.TaskMapper;
import xyz.rkgn.service.StudentService;
import xyz.rkgn.entity.Task;
import xyz.rkgn.utils.LoginUserUtils;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author ljx
 * @description 针对表【tb_student(学生)】的数据库操作Service实现
 * @createDate 2024-02-17 14:26:22
 */
@Service
@Transactional
@Slf4j
public class StudentServiceImpl extends ServiceImpl<StudentMapper, Student>
        implements StudentService {
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    private final StudentTaskMapper studentTaskMapper;

    private final StudentClassMapper studentClassMapper;

    private final TaskMapper taskMapper;

    public StudentServiceImpl(StudentClassMapper studentClassMapper, StudentTaskMapper studentTaskMapper,  TaskMapper taskMapper) {
        this.studentClassMapper = studentClassMapper;
        this.studentTaskMapper = studentTaskMapper;
        this.taskMapper = taskMapper;
    }

    /**
     * 注册
     *
     * @param request
     * @return
     */
    @Override
    @Transactional
    public Result register(HttpServletRequest request, RegisterDto registerDto) {
//        Object numberValidated = request.getSession().getAttribute("numberValidated");
//        Object faceValidated = request.getSession().getAttribute("faceValidated");
//        if (numberValidated == null || !((Boolean) numberValidated)
//                || faceValidated == null || !(Boolean) faceValidated) {
//            return Result.fail("您还未验证或验证还未通过，请先验证");
//        }

        String username = registerDto.getUsername();
        if (StringUtils.isEmpty(username)) {
            Object idNumber = request.getSession().getAttribute("idNumber");
            registerDto.setUsername((String) idNumber);
        }

        //校验用户名唯一性 登录依靠用户名导致用户名不可重复 SMH
        LambdaQueryWrapper<Student> userWrapper = new LambdaQueryWrapper<>();
        userWrapper.eq(Student::getUsername, username);
        Student existingUser = getOne(userWrapper);
        if (existingUser != null) {
            return Result.fail("用户名已存在，请更换用户名");
        }

        //密码 MD5加密
        String password = DigestUtils.md5DigestAsHex(registerDto.getPassword().getBytes(StandardCharsets.UTF_8));
        registerDto.setPassword(password);

        Student student = BeanUtil.copyProperties(registerDto, Student.class, "id");
        save(student);

        //学生班级信息关联
        StudentClass studentClass = new StudentClass();
        studentClass.setStudentId(student.getId());
        String college = request.getParameter("college");
        String clazz = request.getParameter("clazz");
        if (StringUtils.isNotBlank(college)) {studentClass.setCollege(college);}
        if (StringUtils.isNotBlank(clazz)) {studentClass.setClazz(clazz);}
        int insertClass = studentClassMapper.insert(studentClass);
        if (insertClass <= 0) {
            throw new RuntimeException("班级信息关联失败");
        }

        Long studentId = student.getId();
        String studentCollege = studentClass.getCollege();
        String studentClazz = studentClass.getClazz();

        LambdaQueryWrapper<Task> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Task::getIsPublished, 1) // 只关联已发布的任务
                .and(w -> w
                        .eq(Task::getLevel, "校级")
                        .or(q -> q.eq(Task::getLevel, "院级").eq(Task::getCollege, studentCollege)) // 同院级任务
                        .or(q -> q.eq(Task::getLevel, "班级").eq(Task::getClazz, studentClazz)) // 同班级任务
                );
        List<Task> tasks = taskMapper.selectList(queryWrapper);

        if (!tasks.isEmpty()) {
            List<StudentTask> studentTasks = tasks.stream().map(task -> {
                StudentTask st = new StudentTask();
                st.setStudentId(studentId);
                st.setTaskId(task.getId());
                st.setStatus(0); // 初始状态：未完成
                st.setCreateTime(LocalDateTime.now());
                st.setUpdateTime(LocalDateTime.now());
                return st;
            }).collect(Collectors.toList());
            studentTaskMapper.batchInsert(studentTasks); // 使用已有的批量插入方法
        }

        String token = UUID.fastUUID().toString(true);
        StudentDto studentDto = BeanUtil.copyProperties(student, StudentDto.class);
        Map<String, Object> studentDtoMap = BeanUtil.beanToMap(studentDto);
        stringRedisTemplate.opsForHash().putAll(RedisConstants.LOGIN_TOKEN_KEY + token, studentDtoMap);
        stringRedisTemplate.expire(RedisConstants.LOGIN_TOKEN_KEY + token, RedisConstants.LOGIN_TOKEN_TTL);

        return Result.ok(token);
    }

    /**
     *  登录
     * @param loginDto
     * @return
     */
    @Override
    public Result login(LoginDto loginDto) {
        String username = loginDto.getUsername();
        // 登录时也要对密码进行 MD5 加密
        String password = DigestUtils.md5DigestAsHex(loginDto.getPassword().getBytes(StandardCharsets.UTF_8));

        Student stu = lambdaQuery()
                .eq(Student::getUsername, username)
                .eq(Student::getPassword, password).one();
        if (stu == null) {
            return Result.fail("用户名或密码错误");
        }
        if (stu.getId() == null) {
            log.error("学生ID为空，无法登录：{}");
            return Result.fail("用户信息不完整");
        }

        String token = UUID.fastUUID().toString(true);
        StudentDto studentDto = new StudentDto();
        BeanUtil.copyProperties(stu, studentDto);

        log.info("登录成功：{}", studentDto);

        // 将studentDto对象转换为Map，并对id字段进行特殊处理
        Map<String, Object> studentDtoMap = BeanUtil.beanToMap(studentDto, new HashMap<>(), CopyOptions.create().setFieldValueEditor(
                (fieldName, fieldValue) -> {
                    if (fieldName.equals("id")) return fieldValue.toString();
                    else return fieldValue;
                }
        ));
        // 重新设置id字段的值，确保为字符串类型
        studentDtoMap.put("id", stu.getId().toString());
        stringRedisTemplate.opsForHash().putAll(RedisConstants.LOGIN_TOKEN_KEY + token, studentDtoMap);
        stringRedisTemplate.expire(RedisConstants.LOGIN_TOKEN_KEY + token, RedisConstants.LOGIN_TOKEN_TTL);

        //将学生token 与基本信息封装成Map返回给前端
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("userInfo",BeanUtil.copyProperties(stu, StudentShowDto.class));
        return Result.ok(result);
    }

    @Override
    public Result updateStudent(Student student) {
        updateById(student);
        return Result.ok();
    }

    @Override
    public Result logout() {
        //获取token 解析获取userid
        String token = LoginUserUtils.getCurrentToken();
        if (token == null) {
            return Result.fail("未找到登录信息");
        }

        // 构造Redis中的key
        String redisKey = RedisConstants.LOGIN_TOKEN_KEY + token;

        // 从Redis中删除该用户的登录信息
        Boolean deleted = stringRedisTemplate.delete(redisKey);

        if (Boolean.TRUE.equals(deleted)) {
            log.info("用户登出成功，token: {}", token);
            return Result.ok("登出成功");
        } else {
            log.warn("用户登出失败，未找到对应的登录信息，token: {}", token);
            return Result.fail("登出失败，未找到登录信息");
        }
    }
}




