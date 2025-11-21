package xyz.rkgn.controller;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import xyz.rkgn.annotation.mySystemLog;
import xyz.rkgn.common.RedisConstants;
import xyz.rkgn.common.Result;
import xyz.rkgn.entity.StudentInfo;
import xyz.rkgn.mapper.StudentInfoMapper;
import xyz.rkgn.service.StudentInfoService;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 学生信息接口
 */
@RestController
@RequestMapping("studentInfo")
@Slf4j
public class StudentInfoController {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private StudentInfoMapper studentInfoMapper;
    @Resource
    private StudentInfoService studentInfoService;

    /**
     * 进行注册前身份验证
     *
     * @param studentInfo 包含字段idNumber和admissionNumber
     * @return Result对象
     */
    @PostMapping("/validate")
    @mySystemLog(xxbusinessName = "身份验证")
    public Result validate(HttpServletRequest request, @RequestBody StudentInfo studentInfo) {
        return studentInfoService.validate(request, studentInfo);
    }

    /**
     * 人脸验证
     *
     * @param request  请求
     * @param faceFile 前端传来的人脸图片
     * @return Result对象
     * @throws IOException
     */
    @PostMapping("/face")
    @mySystemLog(xxbusinessName = "人脸识别")
    public Result checkFace(HttpServletRequest request, MultipartFile faceFile) throws IOException {
        return studentInfoService.checkFace(request, faceFile);
    }

    /**
     * 积分排行榜列表
     *
     * @param count       要查询的人数
     * @param studentInfo 查询条件参数，支持学院参数
     * @return Result对象
     */
    @GetMapping("/credit/rank/{count}")
    //TODO:Get请求体能被接收到吗
    public Result queryCreditRank(@PathVariable Long count, @RequestBody StudentInfo studentInfo) {
        return studentInfoService.queryCreditRank(count, studentInfo);
    }


    @PutMapping("/updateInfo")
    public Result updateStudentInfo(@RequestBody StudentInfo studentInfo) {
        boolean updated = studentInfoService.updateById(studentInfo);
        if (!updated) {
            return Result.fail("id不存在");
        }
        return Result.ok();
    }

    @GetMapping("/getInfo")
    @mySystemLog(xxbusinessName = "获取学生信息")
    public Result listStudentInfo() {
        return Result.ok(studentInfoService.list());
    }


    @GetMapping("/getByToken")
    @mySystemLog(xxbusinessName = "通过token获取学生信息")
    public Result getStudentInfoByToken(HttpServletRequest request) {
        //从请求头中获取 token
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7); // 去掉 "Bearer " 前缀
        }

        // 从 Redis 中获取用户信息
        Map<Object, Object> studentInfoMap = stringRedisTemplate.opsForHash().entries(RedisConstants.LOGIN_TOKEN_KEY + token);

        // 如果 Redis 中没有该 token
        if (studentInfoMap.isEmpty()) {
            return Result.fail("Token 无效或已过期");
        }

        // 创建 StudentInfo 对象，封装从 Redis 获取到的用户信息
        //目前redis存有 (id=1965697408151420929, nickName=null, avatar=, type=Student)
        StudentInfo studentInfo = new StudentInfo();
        studentInfo.setName((String) studentInfoMap.get("name"));
        studentInfo.setGender((String) studentInfoMap.get("gender"));
        studentInfo.setSchool((String) studentInfoMap.get("school"));
        // 你可以根据需要继续设置其他字段，比如积分、身份证等

        // 返回获取到的用户信息
        return Result.ok(studentInfo);
    }


    @PutMapping("/updateByToken")
    @mySystemLog(xxbusinessName = "通过token修改学生信息")
    public Result updateStudentInfoByToken(@RequestBody StudentInfo studentInfo, @RequestParam String token) {

        // 从 Redis 中获取用户信息
        Map<Object, Object> studentInfoMap = stringRedisTemplate.opsForHash().entries(RedisConstants.LOGIN_TOKEN_KEY + token);

        // 如果 Redis 中没有该 token
        if (studentInfoMap.isEmpty()) {
            return Result.fail("Token 无效或已过期");
        }

        // 获取原始用户信息，并将其转换为 StudentInfo 对象
        StudentInfo existingStudentInfo = new StudentInfo();
        existingStudentInfo.setId((Long) studentInfoMap.get("id"));  // 直接从 Redis 获取 id 并设置为 Long 类型
        existingStudentInfo.setName((String) studentInfoMap.get("nickName"));
        existingStudentInfo.setGender((String) studentInfoMap.get("gender"));
        existingStudentInfo.setSchool((String) studentInfoMap.get("school"));

        // 更新用户信息
        if (studentInfo.getName() != null) {
            existingStudentInfo.setName(studentInfo.getName());
        }
        if (studentInfo.getGender() != null) {
            existingStudentInfo.setGender(studentInfo.getGender());
        }
        if (studentInfo.getSchool() != null) {
            existingStudentInfo.setSchool(studentInfo.getSchool());
        }

        // 使用 MyBatis-Plus 更新数据库中的用户信息
        UpdateWrapper<StudentInfo> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", existingStudentInfo.getId()); // 使用 id 字段进行更新

        boolean updateSuccess = studentInfoMapper.update(existingStudentInfo, updateWrapper) > 0;

        if (!updateSuccess) {
            return Result.fail("更新失败");
        }


        // 更新 Redis 中的用户信息
        Map<String, Object> updatedStudentInfoMap = new HashMap<>();

        // 在更新 Redis 之前，添加日志查看数据
        System.out.println(updatedStudentInfoMap);

        updatedStudentInfoMap.put("id", existingStudentInfo.getId());  // 保证更新 Redis 时包含 id
        updatedStudentInfoMap.put("nickName", existingStudentInfo.getName());
        updatedStudentInfoMap.put("gender", existingStudentInfo.getGender());
        updatedStudentInfoMap.put("school", existingStudentInfo.getSchool()); // 修正这里

        // 更新 Redis 中的用户信息
        stringRedisTemplate.opsForHash().putAll(RedisConstants.LOGIN_TOKEN_KEY + token, updatedStudentInfoMap);
        stringRedisTemplate.expire(RedisConstants.LOGIN_TOKEN_KEY + token, RedisConstants.LOGIN_TOKEN_TTL);
        

        return Result.ok();
    }
}


