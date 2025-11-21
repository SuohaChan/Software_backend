package xyz.rkgn.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("tb_student_class")
public class StudentClass {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long studentId;
    private String college;
    private String clazz;
}