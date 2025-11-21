-- 创建数据库并指定字符集为 utf8mb4
# 此处使用test用于测试
CREATE DATABASE IF NOT EXISTS `xinmiao`;
# CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci;
USE `xinmiao`;
-- 如果 conversation_history 表存在则删除
DROP TABLE IF EXISTS `conversation_history`;
# -- 创建 conversation_history 表并指定字符集为 utf8mb4
# CREATE TABLE `conversation_history` (
#                                         `id` bigint(20) NOT NULL AUTO_INCREMENT,
#                                         `student_id` bigint(20) NOT NULL COMMENT '关联 tb_student 表的学生 id',
#                                         `user_message` text CHARACTER SET utf8mb4 NOT NULL COMMENT '用户问题',
#                                         `ai_response` text CHARACTER SET utf8mb4 NOT NULL COMMENT '助手回复',
#                                         `create_time` datetime NOT NULL COMMENT '创建时间',
#                                         `update_time` datetime NOT NULL COMMENT '更新时间',
#                                         PRIMARY KEY (`id`),
#     -- 外键约束，关联 tb_student 表的 id 字段
#                                         FOREIGN KEY (`student_id`) REFERENCES `tb_student` (`id`)
# ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT '对话历史记录';

