package xyz.rkgn.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.stereotype.Service;
import xyz.rkgn.entity.StudentClass;

/**
 * @author SuohaChan
 * @data 2025/9/13
 */


public interface StudentClassService  extends IService<StudentClass> {
    StudentClass searchClassByStudentId(Long id);
}
