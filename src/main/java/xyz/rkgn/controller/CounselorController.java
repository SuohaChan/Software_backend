package xyz.rkgn.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.rkgn.annotation.mySystemLog;
import xyz.rkgn.common.Result;
import xyz.rkgn.dto.LoginDto;
import xyz.rkgn.entity.Counselor;
import xyz.rkgn.service.CounselorService;

/**
 * 辅导员接口
 */
@RestController
@RequestMapping("counselor")
public class CounselorController {
    @Resource
    private CounselorService counselorService;

    /**
     * 辅导员登录
     * @return Result对象
     */
    @PostMapping("login")
    @mySystemLog(xxbusinessName = "登录辅导员")
    public Result login(@RequestBody LoginDto loginDto) {
        return counselorService.login(loginDto);
    }

    @PostMapping("register")
    @mySystemLog(xxbusinessName = "注册辅导员")
    public Result register(HttpServletRequest request, @RequestBody Counselor counselor) {
        return counselorService.register(request, counselor);
    }
}
