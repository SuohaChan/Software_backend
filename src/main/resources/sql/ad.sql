CREATE DATABASE IF NOT EXISTS `xinmiao`;
USE `xinmiao`;


DROP TABLE IF EXISTS `tb_ad`;
CREATE TABLE `tb_ad`
(
    `id`          BIGINT(20)     NOT NULL COMMENT '主键',
    `title`       VARCHAR(32)    NOT NULL COMMENT '广告标题',
    `content`     VARCHAR(255)   NOT NULL COMMENT '广告内容',
    `image`       VARCHAR(255) DEFAULT '' COMMENT '广告图片，存储图片的URL',
    `fee`         DECIMAL(10, 2) NOT NULL COMMENT '广告费用，精确到小数点后两位',
    `keywords`    VARCHAR(255) DEFAULT '' COMMENT '广告关键词，用逗号分隔',
    `create_time` DATETIME       NOT NULL COMMENT '创建时间',
    `update_time` DATETIME       NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE = utf8_bin COMMENT ='广告';