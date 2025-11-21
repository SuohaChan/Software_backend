package xyz.rkgn.config;

import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Configuration
//配置 RestTemplate，使其使用 Fastjson 作为 JSON 数据的处理工具
public class AppConfig {

    // 修改RestTemplate配置
    @Bean
    public RestTemplate DpRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();

        // 替换为Fastjson的转换器
        // HttpMessageConverter:将 HTTP 请求和响应的内容进行序列化和反序列化的接口
        List<HttpMessageConverter<?>> converters = new ArrayList<>();
        //实现Java 对象与 JSON 数据相互转换
        FastJsonHttpMessageConverter converter = new FastJsonHttpMessageConverter();

        // 设置支持的媒体类型 只会处理 JSON 类型的 HTTP 消息
        List<MediaType> mediaTypes = new ArrayList<>();
        mediaTypes.add(MediaType.APPLICATION_JSON);
        converter.setSupportedMediaTypes(mediaTypes);

        converters.add(converter);
        restTemplate.setMessageConverters(converters);

        return restTemplate;
    }
}