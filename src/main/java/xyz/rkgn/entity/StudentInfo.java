package xyz.rkgn.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.annotations.Param;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * 学生信息
 *
 * @TableName tb_student_info
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class StudentInfo implements Serializable {
    /**
     * 主键
     */
    private Long id;

    /**
     * 姓名
     */
    private String name;

    /**
     * 性别
     */
    private String gender;

    /**
     * 身份证号
     */
    private String idNumber;

    /**
     * 录取通知书编号
     */
    private String admissionNumber;

    /**
     * 学院
     */
    private String school;

    /**
     * 人脸信息储存路径
     */
    private String face;

    /**
     * 积分
     */
    private Long credit;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @Serial
    private static final long serialVersionUID = 1L;
}