package xyz.rkgn.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 礼品
 *
 * @TableName tb_gift
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Gift implements Serializable {
    /**
     * 主键
     */
    private Long id;

    /**
     * 消耗积分
     */
    private Long credit;

    /**
     * 名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    @Serial
    private static final long serialVersionUID = 1L;
}