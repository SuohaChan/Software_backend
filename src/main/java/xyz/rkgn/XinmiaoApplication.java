package xyz.rkgn;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan({"xyz.rkgn.mapper"})
@SpringBootApplication
public class XinmiaoApplication {

    public static void main(String[] args) {
        SpringApplication.run(XinmiaoApplication.class, args);
    }

}
