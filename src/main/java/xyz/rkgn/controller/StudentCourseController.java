package xyz.rkgn.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import xyz.rkgn.annotation.mySystemLog;
import xyz.rkgn.common.Result;
import xyz.rkgn.service.StudentCourseService;

import java.util.List;
import java.util.Map;

/**
 * 学生课程接口
 */
@RestController
@RequestMapping("/student/get-course-schedule")
public class StudentCourseController {

    @Autowired
    StudentCourseService studentCourseService;

    /**
     * 根据学生id 获取对应课表
     *
     * @param studentId 用户id
     * @return Result对象
     */
    @GetMapping
    @mySystemLog(xxbusinessName = "课表")
    public Result getCourseSchedule(@RequestParam Long studentId) {
        List<Map<String, Object>> schedule = studentCourseService.getCourseScheduleByStudentId(studentId);
        return Result.ok(schedule);
    }
}
