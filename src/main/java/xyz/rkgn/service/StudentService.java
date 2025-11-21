package xyz.rkgn.service;

import jakarta.servlet.http.HttpServletRequest;
import xyz.rkgn.common.Result;
import xyz.rkgn.dto.LoginDto;
import xyz.rkgn.dto.RegisterDto;
import xyz.rkgn.entity.Student;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author ljx
* @description 针对表【tb_student(学生)】的数据库操作Service
* @createDate 2024-02-17 14:26:22
*/
public interface StudentService extends IService<Student> {

    Result register(HttpServletRequest request, RegisterDto student);

    Result login(LoginDto loginDto);

    Result updateStudent(Student student);

    Result logout();
}
