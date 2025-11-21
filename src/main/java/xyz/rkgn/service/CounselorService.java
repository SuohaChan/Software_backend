package xyz.rkgn.service;

import jakarta.servlet.http.HttpServletRequest;
import xyz.rkgn.common.Result;
import xyz.rkgn.dto.LoginDto;
import xyz.rkgn.entity.Counselor;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author ljx
* @description 针对表【tb_counselor(辅导员)】的数据库操作Service
* @createDate 2024-02-21 20:21:31
*/
public interface CounselorService extends IService<Counselor> {

    Result register(HttpServletRequest request, Counselor counselor);

    Result login(LoginDto loginDto);
}
