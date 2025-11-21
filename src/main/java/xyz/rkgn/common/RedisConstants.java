package xyz.rkgn.common;

import java.time.Duration;
//设置登录令牌的过期时间为30分钟
//定义Redis键名前缀为"xinmiao:"
//构建完整的登录令牌键名，格式为"xinmiao:login:token:"
public class RedisConstants {
    public static final Duration LOGIN_TOKEN_TTL = Duration.ofMinutes(30);
    private static final String PREFIX = "xinmiao:";
    public static final String LOGIN_TOKEN_KEY = PREFIX + "login:token:";
}
