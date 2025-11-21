package xyz.rkgn.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.UUID;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import xyz.rkgn.common.RedisConstants;
import xyz.rkgn.common.Result;
import xyz.rkgn.dto.CounselorDto;
import xyz.rkgn.dto.CounselorShowDto;
import xyz.rkgn.dto.LoginDto;
import xyz.rkgn.dto.StudentShowDto;
import xyz.rkgn.entity.Counselor;
import xyz.rkgn.mapper.CounselorMapper;
import xyz.rkgn.service.CounselorService;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * @author ljx
 * @description 针对表【tb_counselor(辅导员)】的数据库操作Service实现
 * @createDate 2024-02-21 20:21:31
 */
@Slf4j
@Service
public class CounselorServiceImpl extends ServiceImpl<CounselorMapper, Counselor>
        implements CounselorService {
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Result login(LoginDto loginDto) {
        String username = loginDto.getUsername();
        String password = loginDto.getPassword();
        String passwordEncoded = DigestUtils.md5DigestAsHex(password.getBytes(StandardCharsets.UTF_8));

        Counselor cs = lambdaQuery().eq(Counselor::getUsername, username)
                .eq(Counselor::getPassword, passwordEncoded).one();
        if (cs == null) {
            return Result.fail("用户名或密码错误");
        }

        String token = UUID.fastUUID().toString(true);
        CounselorDto counselorDto = BeanUtil.copyProperties(cs, CounselorDto.class);

        Map<String, Object> counselorDtoMap = BeanUtil.beanToMap(counselorDto);
        stringRedisTemplate.opsForHash().putAll(RedisConstants.LOGIN_TOKEN_KEY + token, counselorDtoMap);
        stringRedisTemplate.expire(RedisConstants.LOGIN_TOKEN_KEY + token, RedisConstants.LOGIN_TOKEN_TTL);

        log.info("counselorDtoMap: {}", counselorDtoMap);


        //将学生token 与基本信息封装成Map返回给前端
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("userInfo",BeanUtil.copyProperties(cs, CounselorShowDto.class));
        return Result.ok(result);
    }

    @Override
    @Transactional
    public Result register(HttpServletRequest request, Counselor counselor) {
        // === 1. 参数校验与初始化 ===
        // 若用户名为空，尝试从session获取（如身份证/工号）
        String username = counselor.getUsername();
        if (StringUtils.isEmpty(username) ){
            Object idNumber = request.getSession().getAttribute("idNumber");
            counselor.setUsername((String) idNumber);
        }

        // === 2. 密码加密处理 ===
        String password = DigestUtils.md5DigestAsHex(counselor.getPassword().getBytes(StandardCharsets.UTF_8));
        counselor.setPassword(password);

        // === 3. 保存辅导员基本信息 ===
        save(counselor);


        // === 6. 生成登录Token ===
        String token = UUID.fastUUID().toString(true);
        CounselorDto dto = BeanUtil.copyProperties(counselor, CounselorDto.class);
        Map<String, Object> dtoMap = BeanUtil.beanToMap(dto);
        stringRedisTemplate.opsForHash().putAll(RedisConstants.LOGIN_TOKEN_KEY + token, dtoMap);
        stringRedisTemplate.expire(RedisConstants.LOGIN_TOKEN_KEY + token, RedisConstants.LOGIN_TOKEN_TTL);

        log.info("dtoMap: {}", dtoMap);

        return Result.ok(token);
    }

}




