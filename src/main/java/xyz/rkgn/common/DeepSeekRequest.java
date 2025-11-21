package xyz.rkgn.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
//已废弃
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeepSeekRequest {
    private String model;
    private List<Message> messages;

    // 构造方法、getter/setter
    @Data
    @AllArgsConstructor
    public static class Message {
        private String role;
        private String content;
    }
}
