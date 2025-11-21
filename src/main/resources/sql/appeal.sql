CREATE DATABASE IF NOT EXISTS `xinmiao`;
USE `xinmiao`;
DROP TABLE IF EXISTS `tb_appeal`;

CREATE TABLE tb_appeal (
     id                 BIGINT(20)      NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '申诉ID',
     user_id            BIGINT(20)      comment '申诉人ID',
     appeal_type        VARCHAR(20)     NOT NULL COMMENT '申诉类型 account content reward other',
     appeal_title       VARCHAR(100)    NOT NULL    COMMENT '申诉标题',
     appeal_description TEXT            NOT NULL    COMMENT '申诉内容',
     contact_info       VARCHAR(100)    NOT NULL    COMMENT '联系方式，如邮箱、电话等',
     submit_time        DATETIME        NOT NULL COMMENT '提交时间',
     accept_time        DATETIME        COMMENT '受理时间',
     processing_time    DATETIME        COMMENT '处理时间',
     complete_time      DATETIME        COMMENT '完成时间',
     handler_id         BIGINT(20)      COMMENT '处理人 ID',
     reply_content      TEXT            COMMENT '处理回复内容',
     status             VARCHAR(20)     NOT NULL DEFAULT 'submit'  COMMENT '当前状态（submit-已提交、accept-已受理、processing-处理中、completed-已完成通过、rejected-不通过）',
     create_time        DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
     update_time        DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
     KEY `idx_user_id` (`user_id`) COMMENT '按申诉人查询',
     KEY `idx_handler_id` (`handler_id`) COMMENT '按处理人查询',
     KEY `idx_status` (`status`) COMMENT '按状态筛选',
     FOREIGN KEY (user_id) REFERENCES tb_student(id)   ON DELETE CASCADE,
     FOREIGN KEY (handler_id) REFERENCES tb_counselor(id)  ON DELETE SET NULL
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin COMMENT='学生申诉表';