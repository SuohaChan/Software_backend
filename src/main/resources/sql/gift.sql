CREATE DATABASE IF NOT EXISTS `xinmiao`;
USE `xinmiao`;
DROP TABLE IF EXISTS `tb_gift`;
CREATE TABLE `tb_gift`
(
    `id`          BIGINT(20)                   NOT NULL COMMENT '主键',
    `credit`      BIGINT(20) COLLATE utf8_bin  NOT NULL COMMENT '消耗积分',
    `name`        VARCHAR(32) COLLATE utf8_bin NOT NULL COMMENT '名称',
    `description` VARCHAR(255) COLLATE utf8_bin DEFAULT '' COMMENT '描述',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE = utf8_bin COMMENT ='礼品';

DROP TABLE IF EXISTS `tb_student_gift`;

CREATE TABLE `tb_student_gift`
(
    `id`          BIGINT(20) NOT NULL COMMENT '主键',
    `student_id`  BIGINT(20) NOT NULL COMMENT '学生的id',
    `gift_id`     BIGINT(20) NOT NULL COMMENT '礼品的id',
    `status`      INT        NOT NULL COMMENT '礼品状态，0表示未完成，1表示已完成',
    `create_time` DATETIME   NOT NULL COMMENT '创建时间',
    `update_time` DATETIME   NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE
#     FOREIGN KEY (student_id) REFERENCES tb_student (id),
#     FOREIGN KEY (gift_id) REFERENCES tb_gift (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE = utf8_bin COMMENT ='学生和礼品关系';
