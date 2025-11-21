package xyz.rkgn.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Optional;
//已废弃
// 响应结构
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeepSeekResponse {
    private List<Choice> choices;

    // getter/setter
    @Data
    public static class Choice {
        private Message message;

        // getter/setter
        @Data
        public static class Message {
            private String role;
            private String content;

        }
    }
    // 快速获取回复内容
    public String getFirstContent() {
        if (CollectionUtils.isEmpty(choices)) return "";
        return Optional.ofNullable(choices.get(0))
                .map(Choice::getMessage)
                .map(Choice.Message::getContent)
                .orElse("");
    }
}