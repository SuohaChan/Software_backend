package xyz.rkgn.dto;

import lombok.Data;

/**
 * @author SuohaChan
 * @data 2025/9/16
 */

@Data
public class RegisterDto {
    private String username;
    private String password;
    private String phone;
    private String name;//辅导员 辅导员姓名
    private String admissionCode;//学生 录取通知书书号
    private String verificationCode;//验证码
}
