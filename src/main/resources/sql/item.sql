CREATE DATABASE IF NOT EXISTS `xinmiao`;
USE `xinmiao`;


DROP TABLE IF EXISTS `tb_item`;
CREATE TABLE `tb_item`
(
    `id`          BIGINT(20)                   NOT NULL COMMENT '主键',
    `name`        VARCHAR(32) COLLATE utf8_bin NOT NULL COMMENT '名称',
    `date`        DATE                         NOT NULL COMMENT '准备日期',
    `description` VARCHAR(255) COLLATE utf8_bin DEFAULT '' COMMENT '描述',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE = utf8_bin COMMENT ='开学前要准备的物品';

DROP TABLE IF EXISTS `tb_student_item`;
CREATE TABLE `tb_student_item`
(
    `id`         BIGINT(20) NOT NULL COMMENT '主键',
    `student_id` BIGINT(20) NOT NULL COMMENT '学生的id',
    `item_id`    BIGINT(20) NOT NULL COMMENT '物品的id',
    `status`     INT        NOT NULL COMMENT '任务状态，0表示未准备，1表示已准备',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE = utf8_bin COMMENT ='学生和开学前要准备的物品的关系';