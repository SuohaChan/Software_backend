package xyz.rkgn.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;

import static xyz.rkgn.common.RedisConstants.LOGIN_TOKEN_KEY;

@Slf4j
@Component
public class PermissionInterceptor implements HandlerInterceptor {
    //权限拦截器 获取 token
    private final StringRedisTemplate stringRedisTemplate;

    public PermissionInterceptor(@Autowired StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public boolean preHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) throws Exception {
        String token = request.getHeader("Authorization");

        log.info("Token: " + token);  // 添加日志，查看请求中的 token

        // 如果请求的是注册接口，直接放行
        if (request.getRequestURI().equals("/student/register")) {
            return true;
        }

        if (token == null) {
            return true; // 如果没有 Token，允许继续请求
        }

        // 根据 Token 获取信息
        Map<Object, Object> dtoMap = stringRedisTemplate.opsForHash().entries(LOGIN_TOKEN_KEY + token);

        if (dtoMap.isEmpty()) {
            return false; // 如果 Token 不存在或无效，拒绝请求
        }

        String type = dtoMap.get("type").toString();
        // 如果 Token 属于学生，限制对某些请求的访问
        if (type.equals("Student")) {
            // 拦截不允许的请求
            switch (request.getMethod()) {
                case "POST", "PUT", "DELETE" -> {
                    return false; // 拦截不允许的学生操作
                }
                default -> {
                    // 对于 GET 或其他方法，不做拦截
                    return true;
                }
            }
        }

        return true; // 对于其他情况继续放行
    }
}
