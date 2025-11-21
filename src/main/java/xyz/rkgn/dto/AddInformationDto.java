package xyz.rkgn.dto;


import lombok.Data;

/**
 * 接收前端通知文本参数的DTO
 */
@Data
public class AddInformationDto {
    private Integer isRequired;  // 对应前端is_required
    private String title;        // 通知标题
    private String desc;         // 通知简述
    private String scope;        // 发布范围
    private String college;      // 学院
    private String clazz;        // 班级
    private Integer isPublished; // 对应前端is_published
}