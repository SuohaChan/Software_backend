CREATE DATABASE IF NOT EXISTS `xinmiao`;
USE `xinmiao`;


DROP TABLE IF EXISTS `tb_student`;
CREATE TABLE `tb_student`
(
    `id`               BIGINT(20)                   NOT NULL COMMENT '主键',
    `username`         VARCHAR(32) COLLATE utf8_bin          DEFAULT 'default_value' COMMENT '用户名',
    `password`         VARCHAR(64) COLLATE utf8_bin NOT NULL COMMENT '密码',
    `avatar`           VARCHAR(255) COLLATE utf8_bin         DEFAULT '' COMMENT '头像路径',
    `nickname`         VARCHAR(32) COLLATE utf8_bin          DEFAULT '' COMMENT '昵称',
    `level`            INT(11)                      NOT NULL DEFAULT 1 COMMENT '等级，默认1级',
    `total_experience` INT(11)                      NOT NULL DEFAULT 0 COMMENT '总经验值，默认0',
    `create_time`      DATETIME                     NOT NULL COMMENT '创建时间',
    `update_time`      DATETIME                     NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE = utf8_bin COMMENT ='学生';
