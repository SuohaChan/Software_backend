package xyz.rkgn.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import xyz.rkgn.common.Result;
import xyz.rkgn.common.StudentHolder;
import xyz.rkgn.common.SystemConstants;
import xyz.rkgn.dto.FaceDto;
import xyz.rkgn.entity.StudentInfo;
import xyz.rkgn.mapper.StudentInfoMapper;
import xyz.rkgn.service.StudentInfoService;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * @author ljx
 * @description 针对表【tb_student_info(学生信息)】的数据库操作Service实现
 * @createDate 2024-02-17 14:26:22
 */
@Service
public class StudentInfoServiceImpl extends ServiceImpl<StudentInfoMapper, StudentInfo> implements StudentInfoService {
    @Resource
    private RestTemplate restTemplate;

    @Override
    public Result validate(HttpServletRequest request, StudentInfo studentInfo) {
        String idNumber = studentInfo.getIdNumber();
        String admissionNumber = studentInfo.getAdmissionNumber();
        boolean exists = lambdaQuery().eq(StudentInfo::getIdNumber, idNumber).eq(StudentInfo::getAdmissionNumber, admissionNumber).exists();
        if (!exists) {
            return Result.fail("身份证号码或录取通知书编号错误，验证失败！");
        }
        request.getSession().setAttribute("numberValidated", true);
        request.getSession().setAttribute("idNumber", idNumber);
        return Result.ok();
    }

    @Override
    public Result checkFace(HttpServletRequest request, MultipartFile faceFile) throws IOException {
        FaceDto faceDto = new FaceDto();
        Long id = StudentHolder.getStudent().getId();

        StudentInfo info = getById(id);
        String storedFace = info.getFace();
        faceDto.setSrc(storedFace);
        String filename = UUID.randomUUID() + ".jpg";
        String faceToCheck = SystemConstants.TEMP_IMAGE_PATH_PREFIX + filename;
        faceFile.transferTo(new File(SystemConstants.TEMP_IMAGE_PATH_PREFIX + filename));
        faceDto.setCheck(faceToCheck);

        ResponseEntity<Boolean> response = restTemplate.postForEntity("http://localhost:8000/face", faceDto, Boolean.class);
        Boolean success = response.getBody();
        request.getSession().setAttribute("faceValidated", success);
        return Result.ok(success);
    }

    @Override
    public Result queryCreditRank(Long count, StudentInfo studentInfo) {
        String school = studentInfo.getSchool();
        List<StudentInfo> records = lambdaQuery().eq(school != null, StudentInfo::getSchool, school)
                .orderByDesc(StudentInfo::getCredit)
                .page(new Page<>(0, count)).getRecords();
        return Result.ok(records);
    }
}




