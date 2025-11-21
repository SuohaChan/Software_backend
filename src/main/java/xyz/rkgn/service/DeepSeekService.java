package xyz.rkgn.service;

public interface DeepSeekService {
    public String getChatResponse(String userMessage);

    public String getChatResponse1(String userMessage, Long userId);

    String getAnswerFromAIServer(String message);
}
