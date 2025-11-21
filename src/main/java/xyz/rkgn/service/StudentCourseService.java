package xyz.rkgn.service;

import com.baomidou.mybatisplus.extension.service.IService;
import xyz.rkgn.entity.CourseSchedule;

import java.util.List;
import java.util.Map;

public interface StudentCourseService extends IService<CourseSchedule> {

    public List<Map<String, Object>> getCourseScheduleByStudentId(Long studentId);
}
