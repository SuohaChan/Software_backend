package xyz.rkgn.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;
import xyz.rkgn.annotation.mySystemLog;
import xyz.rkgn.common.Result;
import xyz.rkgn.dto.LoginDto;
import xyz.rkgn.dto.RegisterDto;
import xyz.rkgn.entity.Student;
import xyz.rkgn.service.StudentService;

/**
 * 学生接口
 */
@RestController
@RequestMapping("student")
public class StudentController {
    @Resource
    private StudentService studentService;

    /**
     * 注册
     *
     * @param registerDto 包含用户名和密码，如果用户名为空，则默认使用身份证号
     * @return Result对象
     */

    @PostMapping("register")
    @mySystemLog(xxbusinessName = "注册用户")
    public Result register(HttpServletRequest request, @RequestBody RegisterDto  registerDto) {
        return studentService.register(request, registerDto);
    }

    /**
     * 登录
     *
     * @param
     * @return
     */
    @PostMapping("login")
    @mySystemLog(xxbusinessName = "登录用户")
    public Result login(@RequestBody LoginDto loginDto) {
        return studentService.login(loginDto);
    }

    /**
     * 登出
     *
     */
    @PostMapping("logout")
    @mySystemLog(xxbusinessName = "登出用户")
    public Result logout() {
        return studentService.logout();
    }


    /**
     * 修改学生头像、账号、密码、昵称
     *
     * @param student id字段必需，头像、账号、密码、昵称可选
     * @return Result对象
     */
    @PutMapping
    @mySystemLog(xxbusinessName = "修改用户")
    public Result updateStudent(@RequestBody Student student) {
        return studentService.updateStudent(student);
    }

    @GetMapping
    @mySystemLog(xxbusinessName = "展示用户？")
    public Result listStudent() {
        return Result.ok(studentService.list());
    }
}
