# CREATE DATABASE IF NOT EXISTS `xinmiao`;
# USE `xinmiao`;
#
# # 辅导员表
# DROP TABLE IF EXISTS `tb_counselor`;
# CREATE TABLE `tb_counselor`
# (
#     `id`          BIGINT(20)                   NOT NULL COMMENT '主键',
#     `username`    VARCHAR(32) COLLATE utf8_bin NOT NULL COMMENT '用户名',
#     `password`    VARCHAR(64) COLLATE utf8_bin NOT NULL COMMENT '密码',
#     `name`        VARCHAR(32) COLLATE utf8_bin NOT NULL COMMENT '名字',
#     `create_time` DATETIME                     NOT NULL COMMENT '创建时间',
#     `update_time` DATETIME                     NOT NULL COMMENT '更新时间',
#     PRIMARY KEY (`id`) USING BTREE,
#     UNIQUE KEY `idx_username` (`username`)
# ) ENGINE = InnoDB
#   DEFAULT CHARSET = utf8
#   COLLATE = utf8_bin COMMENT ='辅导员';
#
#
# # ? tb_admin
# DROP TABLE IF EXISTS `tb_admin`;
# CREATE TABLE `tb_admin`
# (
#     `id`          BIGINT(20)                   NOT NULL COMMENT '主键',
#     `username`    VARCHAR(32) COLLATE utf8_bin NOT NULL COMMENT '用户名',
#     `password`    VARCHAR(64) COLLATE utf8_bin NOT NULL COMMENT '密码',
#     `name`        VARCHAR(32) COLLATE utf8_bin NOT NULL COMMENT '名字',
#     `create_time` DATETIME                     NOT NULL COMMENT '创建时间',
#     `update_time` DATETIME                     NOT NULL COMMENT '更新时间',
#     PRIMARY KEY (`id`) USING BTREE,
#     UNIQUE KEY `idx_username` (`username`)
# ) ENGINE = InnoDB
#   DEFAULT CHARSET = utf8
#   COLLATE = utf8_bin COMMENT ='平台管理员';