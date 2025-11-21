package xyz.rkgn.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xyz.rkgn.annotation.mySystemLog;
import xyz.rkgn.common.Result;
import xyz.rkgn.service.DeepSeekService;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Autowired
    private DeepSeekService deepSeekService;

    /**
     *  无记忆对话
     * @param request 对话信息 message:string
     * @return Result对象
     */
    @PostMapping
    @mySystemLog(xxbusinessName = "无记忆对话")
    public Result chat(@RequestBody Map<String, String> request) {
        String message = request.get("message");
        if (message == null || message.isEmpty()) {
            return Result.fail("请求参数message不能为空");
        }
        String response = deepSeekService.getChatResponse(message);
        return Result.ok(response);
    }


    /**
     *  有记忆对话 处理多轮对话请求
     *
     * @param userMessage 对话信息
     * @param studentId 用户id
     * @return Result对象
     */
    @PostMapping("/multi")
    public Result multiRoundConversation(@RequestParam String userMessage,
                                         @RequestParam  Long studentId) {
        if (userMessage == null || userMessage.isEmpty()) {
            return Result.fail("请求参数userMessage不能为空");
        }
        if (studentId == null ) {
            return Result.fail("请求参数studentId不能为空");
        }
        String response = deepSeekService.getChatResponse1(userMessage, studentId);
        return Result.ok(response);
    }


    /**
     * 调用FastAPI 进行无记忆对话
     *
     * @param request 对话信息 message:string
     * @return Result对象
     */
    @PostMapping("/with_python")
    public Result askQuestion(@RequestBody Map<String, String> request) {
        String message = request.get("message");
        if (message == null || message.isEmpty()) {
            return Result.fail("请求参数message不能为空");
        }
        String response = deepSeekService.getAnswerFromAIServer(message);
        return Result.ok(response);
    }

}
