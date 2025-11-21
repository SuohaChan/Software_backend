package xyz.rkgn.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import reactor.util.retry.Retry;

import java.time.Duration;

@Configuration
public class AiChatConfiguration {
    // 注册内存对话记忆仓库
    @Bean
    public InMemoryChatMemoryRepository chatMemoryRepository() {
        return new InMemoryChatMemoryRepository();
    }

    @Bean
    public ChatClient defaultChatClient(ChatClient.Builder chatClientBuilder) {
        return chatClientBuilder
                .defaultAdvisors(new SimpleLoggerAdvisor()) // 添加日志增强器，方便调试
                .build();
    }

    // 增强重试机制以处理连接重置问题
    @Bean
    public Retry retry() {
        int maxAttempts = 5;
        return Retry.backoff(maxAttempts, Duration.ofSeconds(1))
                .maxBackoff(Duration.ofSeconds(30))
                .jitter(0.5)
                .filter(throwable -> {
                    if (throwable instanceof WebClientRequestException) {
                        return true;
                    }
                    String message = throwable.getMessage();
                    return message != null && (
                            message.contains("Connection reset") ||
                                    message.contains("Connection refused") ||
                                    message.contains("timeout") ||
                                    message.contains("timed out") ||
                                    message.contains("handshake")
                    );
                })
                .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) ->
                        new RuntimeException(
                                String.format("经过%d次重试后仍无法连接到AI服务: %s",
                                        maxAttempts,
                                        retrySignal.failure().getMessage()),
                                retrySignal.failure()));
    }
}