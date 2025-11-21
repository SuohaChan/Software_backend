package xyz.rkgn.utils;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import xyz.rkgn.common.RedisConstants;

import java.util.Map;

/**
 * 登录用户工具类，提供获取当前登录学生ID的功能
 */
@Slf4j
public class LoginUserUtils {
    /**
     * 获取当前登录用户ID
     * 通过从HTTP请求头中获取token，然后在Redis中查找对应的用户信息来获取学生ID
     *
     * @param stringRedisTemplate Redis操作模板，用于查询用户信息
     * @return Long 当前登录用户的ID，
     */
    public static Long getCurrentUserId(StringRedisTemplate stringRedisTemplate) {
        String token = getCurrentToken();
        log.info("Token: {}", token);

        String redisKey = RedisConstants.LOGIN_TOKEN_KEY + token;
        Map<Object, Object> userMap = stringRedisTemplate.opsForHash().entries(redisKey);
        if (userMap.isEmpty()) {
            log.info("User map is empty.");
            return null;
        }

        Object idObj = userMap.get("id");
        if (idObj == null) {
            log.info("User id is null.");
            return null;
        }
        String idStr = idObj.toString().replace("\"", "");
        try {
            Long studentId = Long.valueOf(idStr);
            log.info("User id: {}", studentId);
            return studentId;
        } catch (NumberFormatException e) {
            log.error("id格式错误，无法转换为Long: {}", idStr, e);
            return null;
        }
    }

    public static String getCurrentToken() {
        // 获取当前请求的Servlet请求属性
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            return null;
        }
        HttpServletRequest request = requestAttributes.getRequest();

        // 优先从Authorization头获取Bearer token
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7); // 去掉 "Bearer " 前缀
        } else {
            token = request.getHeader("token");
        }

        if (token == null || token.isEmpty()) {
            log.info("Token is null or empty.");
            return null;
        }
        return token;
    }
}