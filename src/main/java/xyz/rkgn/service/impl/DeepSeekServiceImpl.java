package xyz.rkgn.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import xyz.rkgn.entity.Conversation;
import xyz.rkgn.mapper.ConversationMapper;
import xyz.rkgn.service.DeepSeekService;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
public class DeepSeekServiceImpl extends ServiceImpl<ConversationMapper, Conversation> implements DeepSeekService {

    @Value("${deepseek.api.key}")
    private String apiKey;

    @Value("${deepseek.api.url}")
    private String apiUrl;

    @Value("${chat.context.limit:2}")
    private int contextLimit;

    @Value("${python.server.url}")
    private String aiServerUrl;


    //发送 HTTP 请求的工具类
    private final RestTemplate DpRestTemplate;

    public DeepSeekServiceImpl(RestTemplate DpRestTemplate) {
        this.DpRestTemplate = DpRestTemplate;
    }

    // 构建请求头
    private HttpHeaders buildHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey);
        return headers;
    }

    // 使用 JSONObject 构建请求体
    private JSONObject buildRequest(String userMessage, List<JSONObject> historyMessages) {
        JSONObject request = new JSONObject();
        request.put("model", "deepseek-chat");

        JSONArray messages = new JSONArray();

        // 添加系统消息
        JSONObject systemMsg = new JSONObject();
        systemMsg.put("role", "system");
        systemMsg.put("content", "你是一个大学迎新助手，帮助新生适应校园生活");
        messages.add(systemMsg);

        //获取历史消息
        if (historyMessages != null) {
            messages.addAll(historyMessages);
        }

        //添加用户提问
        JSONObject newUserMessage = new JSONObject();
        newUserMessage.put("role", "user");
        newUserMessage.put("content", userMessage);
        messages.add(newUserMessage);

        request.put("messages", messages);
        return request;
    }

    // 发送请求并处理响应
    private JSONObject sendRequest(JSONObject request) {
        HttpHeaders headers = buildHeaders();
        HttpEntity<String> entity = new HttpEntity<>(request.toJSONString(), headers);
        try {
            String response = DpRestTemplate.postForObject(apiUrl, entity, String.class);
            return JSON.parseObject(response);
        } catch (Exception e) {
            log.error("请求发生错误: {}", e.getMessage(), e);
            return null;
        }
    }



    @Override
    //简单调用 deepseek API 无记忆对话
    public String getChatResponse(String userMessage) {
        JSONObject request = buildRequest(userMessage, null);
        JSONObject responseJson = sendRequest(request);
        if (responseJson == null) {
            return "请求出错，请稍后重试";
        }
        logUsageInfo(responseJson);
        return extractAnswer(responseJson);
    }

    @Override
    //多轮交互 仅回顾两条消息（可改动）
    public String getChatResponse1(String userMessage, Long studentId) { // 修改参数为 studentId
        // 获取历史对话
        List<JSONObject> historyMessages = getConversationHistory(studentId);
        JSONObject request = buildRequest(userMessage, historyMessages);
        JSONObject responseJson = sendRequest(request);
        if (responseJson == null) {
            return "请求出错，请稍后重试";
        }
        String aiResponse = extractAnswer(responseJson);

        // 存储对话记录到 MySQL
        log.info(aiResponse);
        saveConversation(userMessage, aiResponse, studentId);
        return aiResponse;
    }

    //获得历史消息
    private List<JSONObject> getConversationHistory(Long studentId) { // 修改参数为 studentId
        List<JSONObject> historyMessages = new ArrayList<>();
        // 使用 MyBatis-Plus 查询历史对话记录 逆序Desc 查最晚的语句
        LambdaQueryWrapper<Conversation> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Conversation::getStudentId, studentId).orderByDesc(Conversation::getCreateTime); // 修改为 getStudentId
        List<Conversation> conversations = baseMapper.selectList(queryWrapper);

        // 此处仅拿两条历史记录
        int count = 0;
        for (Conversation conversation : conversations) {
            if (count >= contextLimit) {
                break;
            }
            // 用户对话
            JSONObject userMsg = new JSONObject();
            userMsg.put("role", "user");
            userMsg.put("content", conversation.getUserMessage());
            historyMessages.add(userMsg);
            // 助手回复
            JSONObject assistantMsg = new JSONObject();
            assistantMsg.put("role", "assistant");
            assistantMsg.put("content", conversation.getAiResponse());
            historyMessages.add(assistantMsg);
            count++;
        }
        return historyMessages;
    }

    //保存历史信息
    private void saveConversation(String userMessage, String aiResponse, Long studentId) {
        Conversation conversation = new Conversation();
        conversation.setStudentId(studentId);
        conversation.setUserMessage(userMessage);
        conversation.setAiResponse(aiResponse);
        conversation.setCreateTime(LocalDateTime.now());
        conversation.setUpdateTime(LocalDateTime.now());
        baseMapper.insert(conversation);
    }

    //  输出命中token
    private void logUsageInfo(JSONObject responseJson) {
        JSONObject usage = responseJson.getJSONObject("usage");
        if (Objects.nonNull(usage)) {
            long promptCacheHitTokens = usage.getLongValue("prompt_cache_hit_tokens");
            long promptCacheMissTokens = usage.getLongValue("prompt_cache_miss_tokens");
            log.info("本次请求输入中，缓存命中的 tokens 数: {}", promptCacheHitTokens);
            log.info("本次请求输入中，缓存未命中的 tokens 数: {}", promptCacheMissTokens);
        }
    }

    private String extractAnswer(JSONObject responseJson) {
        return responseJson.getJSONArray("choices")
                .getJSONObject(0)
                .getJSONObject("message")
                .getString("content");
    }

    @Override
    public String getAnswerFromAIServer(String question) {
        if (aiServerUrl == null || aiServerUrl.isEmpty()) {
            log.error("AI服务器URL配置为空，请检查配置文件。");
            return "Error: AI server URL configuration is empty.";
        }

        //构建 HTTP 请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        //构建请求体
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("question", question);
        //请求体和请求头封装在一起
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(requestBody, headers);

        try {
            var response = DpRestTemplate.postForEntity(aiServerUrl + "/get_answer", entity, Map.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                var body = response.getBody();
                if (body != null) {
                    if (body.containsKey("answer")) {
                        return (String) body.get("answer");
                    } else if (body.containsKey("error")) {
                        return "Error: " + body.get("error");
                    }
                }
                return "Error: Invalid response from AI server.";
            } else {
                return "Error: AI server responded with status code " + response.getStatusCode();
            }
        } catch (HttpStatusCodeException e) {
            log.error("请求AI服务器时发生HTTP错误，状态码: {}, 响应体: {}", e.getStatusCode(), e.getResponseBodyAsString(), e);
            return "Error: HTTP error occurred while calling AI server. Status code: " + e.getStatusCode();
        } catch (Exception e) {
            log.error("请求AI服务器时发生未知错误", e);
            return "Error: An unknown error occu";
        }
    }
}