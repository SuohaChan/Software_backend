package xyz.rkgn.utils;

import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * @author SuohaChan
 * @data 2025/9/10
 */
public class PasswordUtil {

    /**
     * 使用与项目中相同的MD5加密方式加密密码
     * 用于手动往数据库插入加密后的密码
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("=== 学生系统密码加密工具 ===");
        System.out.print("请输入要加密的密码: ");

        // 读取用户输入的密码
        String rawPassword = scanner.nextLine();

        // 使用与项目中相同的MD5加密方式
        String encodedPassword = DigestUtils.md5DigestAsHex(rawPassword.getBytes(StandardCharsets.UTF_8));

        // 输出加密后的密码
        System.out.println("\n原始密码: " + rawPassword);
        System.out.println("MD5加密后密码: " + encodedPassword);

        scanner.close();
    }
}
