package xyz.rkgn.aspect;


import com.alibaba.fastjson.JSON;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import xyz.rkgn.annotation.mySystemLog;

import java.util.Map;

@Component
@Aspect
@Slf4j
public class myLogAspect {
    //AOP切面 实现日志
    // 定义切点，匹配被 @mySystemlog 注解标注的方法
    @Pointcut("@annotation(xyz.rkgn.annotation.mySystemLog)")
    public void xxpt() {
    }

    // 环绕通知，在切点方法执行前后记录日志
    @Around("xxpt()")
    public Object xxprintLog(ProceedingJoinPoint joinPoint) throws Throwable {
        Object ret;
        try {
            handleBefore(joinPoint);
            ret = joinPoint.proceed();
            handleAfter(ret);
        } finally {
            log.info("=======================end=======================" + System.lineSeparator());
        }
        return ret;
    }

    private void handleBefore(ProceedingJoinPoint joinPoint) {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        mySystemLog systemlog = getSystemLog(joinPoint);

        log.info("======================Start======================");
        log.info("请求URL   : {}", request.getRequestURL());
        log.info("接口描述   : {}", systemlog.xxbusinessName());
        log.info("请求方式   : {}", request.getMethod());
        log.info("请求类名   : {}.{}", joinPoint.getSignature().getDeclaringTypeName(), ((MethodSignature) joinPoint.getSignature()).getName());
        log.info("访问IP    : {}", request.getRemoteHost());

        // 过滤掉复杂对象（如 HttpServletRequest 和 MultipartFile）
        Object[] args = joinPoint.getArgs();
        Object[] filteredArgs = new Object[args.length];
        for (int i = 0; i < args.length; i++) {
            if (args[i] instanceof HttpServletRequest) {
                filteredArgs[i] = "HttpServletRequest";
            } else if (args[i] instanceof org.springframework.web.multipart.MultipartFile) {
                org.springframework.web.multipart.MultipartFile file = (org.springframework.web.multipart.MultipartFile) args[i];
                filteredArgs[i] = Map.of(
                        "fileName", file.getOriginalFilename(),
                        "size", file.getSize(),
                        "contentType", file.getContentType()
                );
            } else {
                filteredArgs[i] = args[i];
            }
        }
        log.info("传入参数   : {}", toJSONStringSafe(filteredArgs));
    }

    // 安全的JSON序列化方法
    private String toJSONStringSafe(Object obj) {
        try {
            return JSON.toJSONString(obj);
        } catch (Exception e) {
            return "Serialization failed: " + e.getMessage();
        }
    }

    // 处理方法执行后的日志记录
    private void handleAfter(Object ret) {
        log.info("返回参数   : {}", JSON.toJSONString(ret));
    }

    private mySystemLog getSystemLog(ProceedingJoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        return methodSignature.getMethod().getAnnotation(mySystemLog.class);
    }
}
