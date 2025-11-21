//package xyz.rkgn;
//
//import cn.hutool.core.util.RandomUtil;
//import org.junit.jupiter.api.Test;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.client.RestTemplate;
//import xyz.rkgn.dto.FaceDto;
//import xyz.rkgn.entity.Student;
//import xyz.rkgn.entity.StudentInfo;
//import xyz.rkgn.service.StudentInfoService;
//import xyz.rkgn.service.StudentService;
//
//import javax.annotation.Resource;
//import java.time.LocalDateTime;
//
//@SpringBootTest
////测试人脸识别
//class XinmiaoApplicationTests {
//    @Resource
//    private StudentService studentService;
//    @Resource
//    private StudentInfoService studentInfoService;
//    @Resource
//    private RestTemplate restTemplate;
//
//    @Test
//    void addStudents() {
//        Student student = new Student(null, "111222333444555666", "123456", "", "张三", LocalDateTime.now(), LocalDateTime.now());
//        studentService.save(student);
//        Long id = student.getId();
//        StudentInfo studentInfo = new StudentInfo(id, "张三", "男", "111222333444555666", "12345678", "计算机与人工智能学院", "", 0L, LocalDateTime.now(), LocalDateTime.now());
//        studentInfoService.save(studentInfo);
//    }
//
//
//    @Test
//    void testFace() {
//        FaceDto faceDto = new FaceDto();
//        faceDto.setSrc("D:\\Projects\\PycharmProjects\\Face\\obama.png");
//        faceDto.setCheck("D:\\Projects\\PycharmProjects\\Face\\obama2.png");
//
//        ResponseEntity<Boolean> response = restTemplate.postForEntity("http://localhost:8000/face", faceDto, Boolean.class);
//        System.out.println(response.getBody());
//    }
//
//    @Test
//    void addCredit() {
//        for (int i = 0; i < 100; i++) {
//            String idNumber = RandomUtil.randomNumbers(18);
//            String admissionNumber = RandomUtil.randomNumbers(8);
//            boolean genderBool = RandomUtil.randomBoolean();
//            int credit = RandomUtil.randomInt(0, 50000);
//            String gender;
//            if (genderBool) {
//                gender = "男";
//            } else {
//                gender = "女";
//            }
//            LocalDateTime now = LocalDateTime.now();
//            studentInfoService.save(new StudentInfo(null, "stu" + i + 1, gender, idNumber, admissionNumber, "计算机学院", "", (long) credit, now, now));
//        }
//    }
//}
package xyz.rkgn;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class XinmiaoApplication {
    public static void main(String[] args) {
        SpringApplication.run(XinmiaoApplication.class, args);
    }
}
