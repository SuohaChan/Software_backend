package xyz.rkgn.controller;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import xyz.rkgn.annotation.mySystemLog;

import java.util.Map;
/**
 * @author SuohaChan
 * @data 2025/9/9
 */

@RestController
@Slf4j
@RequestMapping("/chat")
public class BasicChatController {

    private final ChatClient chatClient;
    private final InMemoryChatMemoryRepository chatMemoryRepository;

    // 注入 Spring 自动配置的 ChatClient
    public BasicChatController(ChatClient chatClient, InMemoryChatMemoryRepository chatMemoryRepository) {
        this.chatClient = chatClient;
        this.chatMemoryRepository = chatMemoryRepository;
    }

    /**
     * 简单问答接口
     * 示例请求：http://localhost:9090/chat?question=什么是Spring AI
     */
    @PostMapping
    @mySystemLog(xxbusinessName = "基于springAI的无记忆对话")
    public String chat(@RequestParam String question) {
        // 使用 Fluent API 构建请求
        return chatClient.prompt()
                .system("你是一个专业的技术顾问，用简洁的语言回答问题。") // 系统提示
                .user(question) // 用户问题
                .call() // 同步调用
                .content(); // 提取回答内容
    }

    /**
     * 流式有记忆问答接口
     * 请求方式：POST
     * 请求路径：/chat/stream
     * 响应类型：text/event-stream（流式传输）
     */
    @mySystemLog(xxbusinessName = "基于springAI的流式有记忆对话")
    @PostMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> streamChat(@RequestBody ChatRequest request) {
        // 使用 record 定义的不可变数据类接收请求参数
        String userId = request.userId();
        String userMessage = request.message();

        // 构建动态参数的系统提示模版
        PromptTemplate systemTemplate = PromptTemplate.builder()
                .template("""
                    你是专业助手，基于用户{userId}的历史对话回答问题。
                    回答需：1. 简洁明了；2. 关联历史上下文；3. 用中文回复。
                    """)
                .build();

        String systemPrompt = systemTemplate.render(Map.of("userId", userId));

        // 构建消息窗口记忆 -- 基于内存仓库存储消息
        // - 限制最大消息数为10条（超出则自动移除最早消息，保留系统消息）
        MessageWindowChatMemory chatMemory = MessageWindowChatMemory.builder()
                .chatMemoryRepository(chatMemoryRepository)
                .maxMessages(10)
                .build();

        // Spring AI提供的记忆顾问 -- 关联记忆实例和会话ID（userId）
        MessageChatMemoryAdvisor memoryAdvisor = MessageChatMemoryAdvisor.builder(chatMemory)
                .conversationId(userId)
                .build();

        // 构建流式响应：
        return chatClient.prompt()
                .system(systemPrompt) // 系统提示
                .user(userMessage) // 用户当前问题
                .advisors(memoryAdvisor)// 绑定记忆顾问（自动读写历史消息）
                .stream()
                .content()
                .map(content -> ServerSentEvent.builder(content).event("message").build())
                .concatWithValues(ServerSentEvent.builder("[DONE]").build())
                .onErrorResume(e -> Flux.just(ServerSentEvent.builder("Error: " + e.getMessage()).event("error").build()));
    }

    /**
     * record 是 Java 16 引入的新型类结构，专门用于建模不可变数据。
     * 此处用于封装客户端发送的聊天请求数据
     */
    record ChatRequest(String userId, String message) {}
}