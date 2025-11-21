package xyz.rkgn.config;

import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.datetime.standard.DateTimeFormatterRegistrar;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.*;
import xyz.rkgn.common.JacksonObjectMapper;
import xyz.rkgn.interceptor.LoginInterceptor;
import xyz.rkgn.interceptor.PermissionInterceptor;
import xyz.rkgn.interceptor.RefreshTokenInterceptor;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Configuration
public class MvcConfig extends WebMvcConfigurationSupport {
    @Resource
    private PermissionInterceptor permissionInterceptor;
    @Resource
    private RefreshTokenInterceptor refreshTokenInterceptor;
    @Resource
    private LoginInterceptor loginInterceptor;

    // 新增：CORS 配置
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:8000", "http://127.0.0.1:5500", "http://localhost:80", "http://47.117.80.173", "http://127.0.0.1:5501")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true) // 如果你前端用了 credentials: 'include'
                .maxAge(3600);

    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //registry.addInterceptor(refreshTokenInterceptor).order(0);

        registry.addInterceptor(loginInterceptor)
//              .addPathPatterns("/**") // 加这一句：默认拦所有路径
                .excludePathPatterns(
                        // 添加排除注册路径
                        "/studentInfo/validate",
                        "/studentInfo/face",
                        "/student/login",
                        "/student/logout",
                        "/api/chat/**",
                        "/student/register",
                        "/chat/**",
                        // 添加临时排除登录路径
                        "/task/**",
                        "/notice/**",
                        "/information/**",
                        "/appeal/**"
                ).order(1);

        registry.addInterceptor(permissionInterceptor)
                .addPathPatterns(
                        "/gift/**",
                        "/student/gift/**",
                        "/item/**",
                        "/student/item/**",
                        "/student/task/**"
                ).order(2);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Override
    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(new JacksonObjectMapper());
        converters.add(0, converter);
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        DateTimeFormatterRegistrar registrar = new DateTimeFormatterRegistrar();
        registrar.setDateTimeFormatter(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
        registrar.setDateTimeFormatter(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));
        registrar.registerFormatters(registry);
    }


    @Override
    public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
        // 配置线程池任务执行器
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(25);
        executor.setThreadNamePrefix("async-executor-");
        executor.initialize();

        configurer.setTaskExecutor(executor);
    }


}
