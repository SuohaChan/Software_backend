package xyz.rkgn.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartFile;
import xyz.rkgn.common.Result;
import xyz.rkgn.entity.StudentInfo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.io.IOException;

/**
* @author ljx
* @description 针对表【tb_student_info(学生信息)】的数据库操作Service
* @createDate 2024-02-17 14:26:22
*/
public interface StudentInfoService extends IService<StudentInfo> {

    Result validate(HttpServletRequest request, StudentInfo studentInfo);

    Result checkFace(HttpServletRequest request, MultipartFile faceFile) throws IOException;

    Result queryCreditRank(Long count, StudentInfo studentInfo);
}
