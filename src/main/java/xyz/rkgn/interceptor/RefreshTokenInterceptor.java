package xyz.rkgn.interceptor;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import xyz.rkgn.common.CounselorHolder;
import xyz.rkgn.common.StudentHolder;
import xyz.rkgn.dto.CounselorDto;
import xyz.rkgn.dto.StudentDto;

import java.util.Map;

import static xyz.rkgn.common.RedisConstants.LOGIN_TOKEN_KEY;
import static xyz.rkgn.common.RedisConstants.LOGIN_TOKEN_TTL;

@Component
@Slf4j
public class RefreshTokenInterceptor implements HandlerInterceptor {
    //刷新 token 过期时间
    private final StringRedisTemplate stringRedisTemplate;

    public RefreshTokenInterceptor(@Autowired StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) throws Exception {
        // 排除预检请求（OPTIONS 请求）
        if ("OPTIONS".equals(request.getMethod())) {
            return true;
        }

        String uri = request.getRequestURI();

        // ✅ 放行登录和注册相关请求
        log.info("当前请求的 URI: {}", uri);
        if (uri.startsWith("/student/login") || uri.startsWith("/student/register")
                || uri.startsWith("/counselor/login") || uri.startsWith("/counselor/register")) {
            log.info("拦截1放行");
            return true;
        }

        String token = request.getHeader("Authorization");
        if (StrUtil.isBlank(token)) {
            // 没有 token 且不是登录注册请求，直接拦截
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            log.info("被拦截1");
            return false;
        }

        // 处理 Bearer 前缀
        if (token.startsWith("Bearer ")) {
            token = token.substring(7); // 去除 'Bearer ' 前缀
        }

        Map<Object, Object> dtoMap = stringRedisTemplate.opsForHash().entries(LOGIN_TOKEN_KEY + token);
        if (dtoMap.isEmpty()) {
            log.info("拦截1放行");
            return true;
        }

        String type = dtoMap.get("type").toString();
        switch (type) {
            case "Student" -> {
                StudentDto studentDto = BeanUtil.fillBeanWithMap(dtoMap, new StudentDto(), false);
                stringRedisTemplate.expire(LOGIN_TOKEN_KEY + token, LOGIN_TOKEN_TTL);
                StudentHolder.setStudent(studentDto);
            }
            case "Counselor" -> {
                CounselorDto counselorDto = BeanUtil.fillBeanWithMap(dtoMap, new CounselorDto(), false);
                stringRedisTemplate.expire(LOGIN_TOKEN_KEY + token, LOGIN_TOKEN_TTL);
                CounselorHolder.setCounselor(counselorDto);
            }
        }
        log.info("拦截1放行");
        return true;
    }

    @Override
    public void afterCompletion(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler, Exception ex) throws Exception {
        StudentHolder.removeStudent();
        CounselorHolder.removeCounselor();
    }
}
