package xyz.rkgn.interceptor;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import xyz.rkgn.common.CounselorHolder;
import xyz.rkgn.common.StudentHolder;

import static cn.hutool.core.lang.Console.print;

@Component
@Slf4j
public class LoginInterceptor implements HandlerInterceptor {
//登录拦截器 判断用户是否登录

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public boolean preHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) throws Exception
    {
        /*
        // 排除预检请求（OPTIONS 请求）
        if ("OPTIONS".equals(request.getMethod())) {
            return true;
        }
        log.info("登录拦截");
        // 2. 获取 token
        String token = request.getHeader("Authorization");
        log.info("Token: " + token);  // 调试输出 token
        if (token == null || token.isEmpty()) {
            response.setStatus(401);
            return false;
        }

        if (!token.startsWith("Bearer ")) {
            response.setStatus(400); // 可以返回 400 Bad Request，如果 token 格式错误
            return false;
        }

        token = token.substring(7);  // 去除 'Bearer ' 前缀
        // 3. 拼接 key 查询 redis
        String key = RedisConstants.LOGIN_TOKEN_KEY + token;

        System.out.println("Redis Key: " + key);  // 调试输出 Redis key

        Map<Object, Object> studentMap = stringRedisTemplate.opsForHash().entries(key);
        print(key);
        if (studentMap.isEmpty()) {
            response.setStatus(401);
            return false;
        }

        // 4. 将数据转为 StudentDto，并放入 ThreadLocal
        StudentDto studentDto = BeanUtil.fillBeanWithMap(studentMap, new StudentDto(), false);
        StudentHolder.setStudent(studentDto);

        // 5. 更新过期时间（可选，延迟登录态）
        stringRedisTemplate.expire(key, RedisConstants.LOGIN_TOKEN_TTL);

        return true;

         */

//        StudentDto student = StudentHolder.getStudent();
//        CounselorDto counselor = CounselorHolder.getCounselor();
//        if (student != null || counselor != null) {
//            return true;
//        }
//        response.setStatus(401);
//        return false;

        return true;
    }

    @Override
    public void afterCompletion(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler, Exception ex) throws Exception {
        StudentHolder.removeStudent();
        CounselorHolder.removeCounselor();
    }
}
